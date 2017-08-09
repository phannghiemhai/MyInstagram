/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.http;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author quocpt
 */
public class HttpClientUtils {

    public static class KeepAliveStratery extends DefaultConnectionKeepAliveStrategy {

        @Override
        public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
            long keepAlive = super.getKeepAliveDuration(response, context);
            if (keepAlive == -1) // Keep connections alive 30 seconds if a keep-alive value
            // has not be explicitly set by the server
            {
                keepAlive = 30000;
            }

            return keepAlive;
        }
    }
    
    private static CloseableHttpClient mHttpClient;

    public static void warmUp() {
        // Create http client
        PoolingHttpClientConnectionManager connMng = new PoolingHttpClientConnectionManager();
        connMng.setMaxTotal(200);

        HttpClientBuilder clientBuilder = HttpClients.custom();
        clientBuilder.setConnectionManager(connMng);
        clientBuilder.disableCookieManagement();
        clientBuilder.disableAuthCaching();
        clientBuilder.disableRedirectHandling();
        clientBuilder.setUserAgent("HaiPN/1.0");
        clientBuilder.setKeepAliveStrategy(new KeepAliveStratery());
        String proxyHost = System.getProperty("proxyHost");
        String proxyPort = System.getProperty("proxyPort");
        if (!StringUtils.isBlank(proxyHost) && !StringUtils.isBlank(proxyPort)) {
            HttpHost proxy = new HttpHost(proxyHost, Integer.valueOf(proxyPort));
            clientBuilder.setRoutePlanner(new DefaultProxyRoutePlanner(proxy));
        }

        mHttpClient = clientBuilder.build();
        System.out.println("HttpClientUtils warm-up completed!");
    }

    public static URIBuilder createRequest(String url) {
        String protocal;
        if (url.startsWith("https")) {
            protocal = "https";
            url = url.substring(8, url.length()); // Remove https://
        } else if (url.startsWith("http")) {
            protocal = "http";
            url = url.substring(7, url.length()); // Remove http://
        } else {
            return null; // Unknown protocal
        }
        String domain, path;
        if (url.contains("/")) {
            int si = url.indexOf("/");
            domain = url.substring(0, si);
            path = url.substring(si, url.length());
        } else {
            domain = url;
            path = "";
        }

        URIBuilder uriBuilder = new URIBuilder();
        uriBuilder.setScheme(protocal);
        uriBuilder.setHost(domain);
        uriBuilder.setPath(path);

        return uriBuilder;
    }

    public static String executeGet(URIBuilder request) throws HttpResponseException, ClientProtocolException, IOException, URISyntaxException {
        HttpGet httpGet = new HttpGet(request.build());
        HttpClientContext context = HttpClientContext.create();
        BasicResponseHandler rh = new BasicResponseHandler();
        return mHttpClient.execute(httpGet, rh, context);
    }

    public static String executePost(URIBuilder request) throws HttpResponseException, ClientProtocolException, IOException, URISyntaxException {
        HttpPost httpPost = new HttpPost(request.build());
        HttpClientContext context = HttpClientContext.create();
        BasicResponseHandler rh = new BasicResponseHandler();
        return mHttpClient.execute(httpPost, rh, context);
    }

    public static String executePost(URIBuilder request, List<NameValuePair> urlEncodedFormParameters) throws HttpResponseException, ClientProtocolException, IOException, URISyntaxException {
        HttpPost httpPost = new HttpPost(request.build());
        httpPost.setHeader("content-type", "application/json");
        httpPost.setEntity(new UrlEncodedFormEntity(urlEncodedFormParameters));
        HttpClientContext context = HttpClientContext.create();
        BasicResponseHandler rh = new BasicResponseHandler();
        return mHttpClient.execute(httpPost, rh, context);
    }

    public static String executePost(URIBuilder request, List<NameValuePair> urlEncodedFormParameters, String type) throws HttpResponseException, ClientProtocolException, IOException, URISyntaxException {
        HttpPost httpPost = new HttpPost(request.build());
        httpPost.setHeader("content-type", type);
        httpPost.setEntity(new UrlEncodedFormEntity(urlEncodedFormParameters));
        HttpClientContext context = HttpClientContext.create();
        BasicResponseHandler rh = new BasicResponseHandler();
        return mHttpClient.execute(httpPost, rh, context);
    }

    public static String executePost(URIBuilder request, HttpEntity entity) throws HttpResponseException, ClientProtocolException, IOException, URISyntaxException {

        HttpPost httpPost = new HttpPost(request.build());
        httpPost.setEntity(entity);
        httpPost.setHeader("content-type", "application/json");
        HttpClientContext context = HttpClientContext.create();
        BasicResponseHandler rh = new BasicResponseHandler();
        return mHttpClient.execute(httpPost, rh, context);
    }

    public static HttpResponse executeRequestResponse(HttpRequestBase httpMethod) throws HttpResponseException, ClientProtocolException,
            IOException {
        HttpClientContext context = HttpClientContext.create();
        return mHttpClient.execute(httpMethod, context);
    }

    public static String callPHPSoapService(String url, String soapBody) {
        try {
            URIBuilder createRequest = createRequest(url);
            HttpPost httppost = new HttpPost(createRequest.build());
            httppost.setURI(new URI(url));

            // Request parameters and other properties.
            byte[] data = soapBody.getBytes("UTF-8");
            HttpEntity entity = new ByteArrayEntity(data, ContentType.create("text/xml", "utf-8"));
            httppost.setEntity(entity);
            httppost.addHeader("Content-Type", "text/xml; charset=utf-8");
            httppost.addHeader("SOAPAction", "\"urn:LegacyServicesAction\"");
            httppost.addHeader("User-Agent", "PHP-SOAP/5.2.10");

            //Execute and get the response.
            HttpResponse resp = executeRequestResponse(httppost);
            StatusLine statusLine = resp.getStatusLine();
            entity = resp.getEntity();
            if (statusLine.getStatusCode() >= 300) {
                EntityUtils.consume(entity);
                throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
            }
            return EntityUtils.toString(entity);
        } catch (URISyntaxException | UnsupportedCharsetException | IOException ex) {
        }

        return null;
    }
}
