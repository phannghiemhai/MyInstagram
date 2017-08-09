/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author haipn
 */
public class DbConnectorEntity {
    public Connection conn;
    public PreparedStatement stmt;
    public List<ResultSet> resultSets;
    public DbConnectorEntity(){
        conn = null;
        stmt = null;
        resultSets = new ArrayList<>();
    }
    
    public ResultSet executeQuery() throws SQLException{
        ResultSet rs = stmt.executeQuery();
        resultSets.add(rs);
        return rs;
    }
    
    public ResultSet getGeneratedKeys() throws SQLException{
        return stmt.getGeneratedKeys();
    }
}
