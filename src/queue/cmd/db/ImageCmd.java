/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queue.cmd.db;

import DAO.ModelImageDAO;
import entity.DAO.ImageEntity;
import queue.cmd.IQueueCmd;

/**
 *
 * @author haipn
 */
public class ImageCmd implements IQueueCmd {
    public enum TYPES {
        INSERT, UPDATE, DELETE;
    }
    private ImageEntity _entity;
    private TYPES _type;
    
    public ImageCmd(ImageEntity entity, TYPES type) {
        this._entity = entity;
        this._type = type;
    }

    @Override
    public void execute() {
        if (null != _type) switch (_type) {
            case INSERT:
                ModelImageDAO.getInstance().insert(_entity);
                break;
            case UPDATE:
                ModelImageDAO.getInstance().update(_entity);
                break;
            case DELETE:
                ModelImageDAO.getInstance().delete(_entity);
                break;
            default:
                break;
        }
    }
}
