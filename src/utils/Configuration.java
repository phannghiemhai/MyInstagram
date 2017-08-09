/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

/**
 *
 * @author haipn
 */
public class Configuration {

    public static class URL {

        public static final String STATIC_URL = Config.getParam("url", "static_url");
    }

    public static class Auth {

        public static final String ClientId = Config.getParam("auth", "client_id");
        public static final String ApiKey = Config.getParam("auth", "api_key");
    }

    public static class CmdQueue {
        public static int N_WORKER = Config.getParamInt("queue", "workers");
        public static int QUEUE_SIZE = Config.getParamInt("queue", "queue_size");
        public static String QUEUE_NAME = Config.getParam("queue", "queue_name");
    }
}
