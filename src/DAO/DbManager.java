/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author haipn
 */
public class DbManager {

    private static Logger _logger = Logger.getLogger(DbManager.class);
    private static DbManager _instance;
    private static Map<String, DbConnector> _mapDBConnector;

    private DbManager() {
        _mapDBConnector = new HashMap<>();
    }

    public static DbManager getInstance() {
        if (_instance == null) {
            _instance = new DbManager();
        }
        return _instance;
    }

    public boolean addDbConnector(String key, DbConnector connector) {
        if (!_mapDBConnector.containsKey(key)){
            _mapDBConnector.put(key, connector);
            return true;
        }
        return false;
    }
}
