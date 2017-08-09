/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import entity.db.DbConnectorEntity;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.log4j.Logger;
import service.CheckingConnector;

/**
 *
 * @author quocpt
 */
public class SQLiteDAO implements DbConnector, CheckingConnector {

    private static Logger _logger = Logger.getLogger(SQLiteDAO.class);
    private String _connectionStr;
    private GenericObjectPool<Connection> pool;

    public SQLiteDAO(String path) {
        try {
            Class.forName("org.sqlite.JDBC");
            this._connectionStr = "jdbc:sqlite:" + path;
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
        }
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxIdle(1);
        config.setMaxTotal(5);
        config.setMaxWaitMillis(15000);
        config.setTestOnBorrow(true);
        pool = new GenericObjectPool<>(new PooledObjectFactory<Connection>() {
            @Override
            public PooledObject<Connection> makeObject() throws Exception {
                Connection conn = DriverManager.getConnection(_connectionStr);
                DefaultPooledObject<Connection> obj = new DefaultPooledObject<>(conn);
                return obj;
            }

            @Override
            public void destroyObject(PooledObject<Connection> po) throws Exception {
                po.getObject().close();
            }

            @Override
            public boolean validateObject(PooledObject<Connection> po) {
                try {
                    return !po.getObject().isClosed();
                } catch (SQLException ex) {
                }
                return false;
            }

            @Override
            public void activateObject(PooledObject<Connection> po) throws Exception {
            }

            @Override
            public void passivateObject(PooledObject<Connection> po) throws Exception {
            }
        }, config);
    }

    @Override
    public boolean openConnection(DbConnectorEntity entity) {
        try {
            entity.conn = pool.borrowObject();
            return entity.conn != null;
        } catch (Exception ex) {
            _logger.error(ex.getCause(), ex);
        }
        return false;
    }

    @Override
    public void closeConnection(DbConnectorEntity entity) {
        try {
            if (entity.conn != null) {
                pool.returnObject(entity.conn);
            }
            if (entity.stmt != null) {
                entity.stmt.close();
            }
            for (ResultSet rs : entity.resultSets) {
                if (rs != null) {
                    rs.close();
                }
            }
        } catch (Exception ex) {
            _logger.error(ex.getMessage(), ex);
        }
    }

    @Override
    public boolean check() {
        DbConnectorEntity entity = new DbConnectorEntity();
        boolean res = this.openConnection(entity);
        System.out.println("SQLite load at: " + _connectionStr + (res ? "Successfull" : "Fail"));
        return res;
    }
}
