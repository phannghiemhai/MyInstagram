/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author haipn
 */
public class Config {

    private static final Logger _logger = Logger.getLogger(Config.class);
    private static CompositeConfiguration _config = null;
    private static ConcurrentHashMap<String, String> _hashConfig = null;

    private enum ATTR {
        ENV("environment", "development"),
        APPCONFIG("appconfig", "conf");
        public String name;
        public String defaultVal;

        private ATTR(String name, String defaultVal) {
            this.name = name;
            this.defaultVal = defaultVal;
        }
    }

    static {
        init();
    }

    private static void init() {
        String env = System.getProperty(ATTR.ENV.name);
        if (StringUtils.isBlank(env)) {
            env = ATTR.ENV.defaultVal;
        }
        String appConfig = System.getProperty(ATTR.APPCONFIG.name);
        if (StringUtils.isBlank(appConfig)) {
            appConfig = ATTR.APPCONFIG.defaultVal;
        }
        String configFile = appConfig + java.io.File.separator + env + ".app.ini";
        System.out.println("confFile: " + configFile);
        _config = new CompositeConfiguration();
        _hashConfig = new ConcurrentHashMap<>();
        try {
            _config.addConfiguration(new HierarchicalINIConfiguration(configFile));
        } catch (ConfigurationException ex) {
            _logger.error("Can't load configuration file", ex);
            System.err.println("Bad configuration; unable to start server");
            System.exit(1);
        }
    }
    
    public static String getParam(String section, String name) {
        String key = section + "." + name;
        if (_hashConfig.containsKey(key)){
            return _hashConfig.get(key);
        } else {
            String value = _config.getString(key);
            if (value != null) {
                _hashConfig.put(key, value);
                return value;
            }
        }
        return null;
    }
    
    public static int getParamInt(String section, String name) {
        String value = getParam(section, name);
        try {
            return Integer.valueOf(value);
        } catch (Exception ex) {
            _logger.error(ex);
        }
        return 0;
    }
    
    public static long getParamLong(String section, String name) {
        String value = getParam(section, name);
        try {
            return Long.valueOf(value);
        } catch (Exception ex) {
            _logger.error(ex);
        }
        return 0l;
    }
    
    public static float getParamFloat(String section, String name) {
        String value = getParam(section, name);
        try {
            return Float.valueOf(value);
        } catch (Exception ex) {
            _logger.error(ex);
        }
        return 0f;
    }
    
    public static double getParamDouble(String section, String name) {
        String value = getParam(section, name);
        try {
            return Double.valueOf(value);
        } catch (Exception ex) {
            _logger.error(ex);
        }
        return 0;
    }
    
    public static boolean getParamBoolean(String section, String name) {
        String value = getParam(section, name);
        try {
            return Boolean.valueOf(value);
        } catch (Exception ex) {
            _logger.error(ex);
        }
        return false;
    }
    
    public static void reloadConfig(){
        init();
    }
}
