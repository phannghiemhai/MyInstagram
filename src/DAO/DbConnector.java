/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import entity.db.DbConnectorEntity;

/**
 *
 * @author haipn
 */
public interface DbConnector { 
    public boolean openConnection(DbConnectorEntity entity);
    public void closeConnection(DbConnectorEntity entity);
}
