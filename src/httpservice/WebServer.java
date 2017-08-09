/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpservice;

import utils.http.HttpClientUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.GzipHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import queue.QueueManager;
import utils.Config;
import utils.Configuration;

/**
 *
 * @author haipn
 */
public class WebServer {

    private static Logger _logger = Logger.getLogger(WebServer.class);
    private String _hostname;
    private int _zport;
    private Server _server;

    public static void main(String[] args) {
        int zport = Integer.valueOf(System.getProperty("zport"));
        WebServer webServer = new WebServer("MyServer", zport);
        webServer.startServer();
    }

    public WebServer(String hostname, int zport) {
        _hostname = hostname;
        _zport = zport;

        //init service
        System.out.println("warm-up...");
        HttpClientUtils.warmUp();
        QueueManager.getInstance(Configuration.CmdQueue.QUEUE_NAME)
                .init(Configuration.CmdQueue.N_WORKER, Configuration.CmdQueue.QUEUE_SIZE);
        System.out.println("warm-up completed...");
    }

    private static class ShutdownThread extends Thread {

        private final Server _server;

        public ShutdownThread(Server _server) {
            this._server = _server;
        }

        @Override
        public void run() {
            System.out.println("Shutting down...");
            _server.setGracefulShutdown(MIN_PRIORITY);
            System.out.println("Bye.");
        }
    }

    public void startServer() {
        try {
            //init Server
            _server = new Server();
            
            //init Handler
            HandlerCollection handlers = new HandlerCollection();

            ContextHandler resourceHandler = getResourceHandler("static", "/static", false);
            handlers.addHandler(resourceHandler);

            GzipHandler gzipHandler = new GzipHandler();
            gzipHandler.setHandler(new MainHandler());
            handlers.addHandler(gzipHandler);
            _server.setHandler(handlers);

            //init pool
            ThreadPool threadPool = initThreadPool();
            _server.setThreadPool(threadPool);

            //init nio Connector
            SelectChannelConnector connector = initNioConnector();
            _server.setConnectors(new Connector[]{connector});

            //add shutdown hook
            Runtime.getRuntime().addShutdownHook(new ShutdownThread(_server));
            _server.setStopAtShutdown(true);

            runJettyServer();
        } catch (Exception ex) {
            _logger.error("WebServer", ex);
        }
    }

    private ContextHandler getResourceHandler(String base, String context, boolean isDirListed) {
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase(base);
        resourceHandler.setDirectoriesListed(isDirListed);
        ContextHandler contextHandler = new ContextHandler(context);
        contextHandler.setHandler(resourceHandler);
        return contextHandler;
    }

    private ThreadPool initThreadPool() {
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMaxThreads(Config.getParamInt("service", "threadpool_max"));
        threadPool.setMaxThreads(Config.getParamInt("service", "threadpool_min"));
        return threadPool;
    }

    private SelectChannelConnector initNioConnector() {
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(_zport);
        connector.setMaxIdleTime(Config.getParamInt("service", "connector_maxidle"));
        connector.setRequestHeaderSize(Config.getParamInt("service", "connector_headersize"));
        connector.setResponseBufferSize(Config.getParamInt("service", "connector_buffersize"));
        connector.setRequestHeaderSize(Config.getParamInt("service", "connector_headersize"));
        connector.setRequestBufferSize(Config.getParamInt("service", "connector_buffersize"));
        connector.setAcceptors(Config.getParamInt("service", "connector_acceptors"));
        return connector;
    }

    private void runJettyServer() throws InterruptedException, Exception {
        System.out.println("server start...");
        _server.start();
        System.out.println(String.format("Successful Start '%s'. Server port: %s",
                _hostname, _zport));
        _server.join();
        System.exit(0);
    }
}
