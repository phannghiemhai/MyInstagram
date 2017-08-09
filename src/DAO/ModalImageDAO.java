/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import entity.DAO.ImageEntity;
import entity.db.DbConnectorEntity;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.LinkedList;
import utils.Config;

/**
 *
 * @author generated by haipn
 */
public class ModalImageDAO {

    private static ModalImageDAO _instance = null;

    private static final String TABLE_NAME = "image_table";
    private static final String[] TABLE_PRIMARY_KEYS = {"id"};
    private static final String[] TABLE_NORMAL_COLS = {"user_id", "src", "type", "des", "hashtag", "timestamp", "width", "height", "ratio"};
    private static final String[] TABLE_COLS = {"id", "user_id", "src", "type", "des", "hashtag", "timestamp", "width", "height", "ratio"};

    private final DbConnector _connector;

    private ModalImageDAO() {
        _connector = new SQLiteDAO(Config.getParam("sqlite", "raw_data"));
    }

    public static ModalImageDAO getInstance() {
        if (_instance == null) {
            _instance = new ModalImageDAO();
        }
        return _instance;
    }

    public int insert(ImageEntity entity) {
        DbConnectorEntity ce = new DbConnectorEntity();
        try {
            if (!_connector.openConnection(ce)) {
                return -1;
            }
            String sql = BuildQueryString.buildInsertQuery(TABLE_NAME, TABLE_COLS, 1);
            ce.stmt = ce.conn.prepareStatement(sql);
            int idx = 1;
            entity.setParameters(ce.stmt, idx);
            return ce.stmt.executeUpdate();
//            conn.commit();
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            _connector.closeConnection(ce);
        }
        return -1;
    }

    public int update(ImageEntity entity) {
        DbConnectorEntity ce = new DbConnectorEntity();
        try {
            if (!_connector.openConnection(ce)) {
                return -1;
            }
            String sql = BuildQueryString.buildUpdateQuery(TABLE_NAME, TABLE_PRIMARY_KEYS, TABLE_NORMAL_COLS);
            ce.stmt = ce.conn.prepareStatement(sql);
            int idx = 1;
            entity.setParametersForUpdate(ce.stmt, idx);
            return ce.stmt.executeUpdate();
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            _connector.closeConnection(ce);
        }
        return -1;
    }

    public int delete(ImageEntity entity) {
        DbConnectorEntity ce = new DbConnectorEntity();
        try {
            if (!_connector.openConnection(ce)) {
                return -1;
            }
            String sql = BuildQueryString.buildDeleteQuery(TABLE_NAME, TABLE_PRIMARY_KEYS);
            ce.stmt = ce.conn.prepareStatement(sql);
            int idx = 1;
            entity.setPrimaryKeyParameters(ce.stmt, idx);
            return ce.stmt.executeUpdate();
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            _connector.closeConnection(ce);
        }
        return -1;
    }

    public int getSeq() {
        DbConnectorEntity ce = new DbConnectorEntity();
        try {
            if (!_connector.openConnection(ce)) {
                return -1;
            }
            String sql = "select seq from sqlite_sequence where name = ?";
            ce.stmt = ce.conn.prepareStatement(sql);
            int idx = 1;
            ce.stmt.setString(idx++, TABLE_NAME);
            ResultSet rs = ce.executeQuery();
            if (rs != null && rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            _connector.closeConnection(ce);
        }
        return -1;
    }

    public Collection<ImageEntity> getEntities(String select, String where, String groupBy, String having, String orderBy, int limit) {
        Collection<ImageEntity> res = new LinkedList<>();
        DbConnectorEntity ce = new DbConnectorEntity();
        try {
            if (!_connector.openConnection(ce)) {
                return res;
            }
            String sql = BuildQueryString.buildSelectQuery(TABLE_NAME, select, where, groupBy, having, orderBy, limit);
            ce.stmt = ce.conn.prepareStatement(sql);
            ResultSet rs = ce.executeQuery();
//            conn.commit();
            while (rs.next()) {
                ImageEntity entity = new ImageEntity(rs);
                res.add(entity);
            }
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            _connector.closeConnection(ce);
        }
        return res;
    }

    public ImageEntity getEntity(ImageEntity entity) {
        DbConnectorEntity ce = new DbConnectorEntity();
        try {
            if (!_connector.openConnection(ce)) {
                return null;
            }
            String sql = BuildQueryString.buildSelectByKeyQuery(TABLE_NAME, TABLE_PRIMARY_KEYS);
            ce.stmt = ce.conn.prepareStatement(sql);
            int idx = 1;
            entity.setPrimaryKeyParameters(ce.stmt, idx);
            ResultSet rs = ce.executeQuery();
//            conn.commit();
            if (rs.next()) {
                return new ImageEntity(rs);
            }
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            _connector.closeConnection(ce);
        }
        return null;
    }

    public static void main(String[] args) {
//        ImageEntity imageEntity = new ImageEntity(0, "phannghiemhai", 
//                "https://scontent.fsgn2-2.fna.fbcdn.net/v/t1.0-9/18424212_1385372334886861_3572322844774159264_n.jpg?oh=64b35fb26d65950e35d5a7009eca2799&oe=5A02DF75", 
//                0, "", "", 0, 0, 0, 0);
//        int id = getInstance().insert(imageEntity);
//        System.out.println(id);
        Collection<ImageEntity> entities = getInstance().getEntities(null, null, null, null, null, 0);
        for (ImageEntity entity : entities) {
            entity.updateSize();
            entity.timestamp = System.currentTimeMillis();
            getInstance().update(entity);
        }
        entities = getInstance().getEntities(null, null, null, null, null, 0);
        for (ImageEntity entity : entities) {
            System.out.println(entity.ratio);
        }
    }
}