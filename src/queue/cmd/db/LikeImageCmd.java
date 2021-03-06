/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queue.cmd.db;

import DAO.ModelLikeImageDAO;
import entity.DAO.LikeImageEntity;
import queue.cmd.IQueueCmd;

/**
 *
 * @author haipn
 */
public class LikeImageCmd implements IQueueCmd {
    public enum TYPES {
        INSERT, UPDATE, DELETE, DELETE_BY_ID;
    }
    private LikeImageEntity _entity;
    private TYPES _type;
    
    public LikeImageCmd(LikeImageEntity entity, TYPES type) {
        this._entity = entity;
        this._type = type;
    }

    @Override
    public void execute() {
        if (null != _type) switch (_type) {
            case INSERT:
                ModelLikeImageDAO.getInstance().insert(_entity);
                break;
            case UPDATE:
                ModelLikeImageDAO.getInstance().update(_entity);
                break;
            case DELETE:
                ModelLikeImageDAO.getInstance().delete(_entity);
                break;
            case DELETE_BY_ID:
                ModelLikeImageDAO.getInstance().delete(_entity.imageId);
                break;
            default:
                break;
        }
    }
}
