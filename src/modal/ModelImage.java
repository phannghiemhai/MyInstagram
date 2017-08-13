/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modal;

import DAO.*;
import entity.DAO.ImageEntity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import queue.QueueManager;
import queue.cmd.db.ImageCmd;
import utils.Configuration;

/**
 *
 * @author generated by haipn
 */
public class ModelImage {

    private static ModelImage _instance = null;
    private List<Integer> _imageIds;
    private Map<Integer, ImageEntity> _images;
    private Map<String, List<Integer>> _userId2Images;
    private int _seq;

    private ModelImage() {
        _imageIds = new ArrayList<>();
        _images = new HashMap<>();
        _userId2Images = new HashMap<>();
        Collection<ImageEntity> entities = ModelImageDAO.getInstance().getEntities(null, null, null, null, "id ASC", 0);
        for (ImageEntity entity : entities) {
            _imageIds.add(entity.id);
            _images.put(entity.id, entity);
            insertToMap(entity);
        }
        _seq = ModelImageDAO.getInstance().getSeq();
    }

    private void insertToMap(ImageEntity entity) {
        List<Integer> images = null;
        if (_userId2Images.containsKey(entity.userId)) {
            images = _userId2Images.get(entity.userId);
        } else {
            images = new ArrayList<>();
            _userId2Images.put(entity.userId, images);
        }
        images.add(entity.id);
    }

    public static ModelImage getInstance() {
        if (_instance == null) {
            _instance = new ModelImage();
        }
        return _instance;
    }

    public int insert(ImageEntity entity) {
        if (entity.isValidImg()) {
            entity.id = ++_seq;
            _imageIds.add(entity.id);
            _images.put(entity.id, entity);
            insertToMap(entity);
            ImageCmd cmd = new ImageCmd(entity, ImageCmd.TYPES.INSERT);
            QueueManager.getInstance(Configuration.CmdQueue.QUEUE_NAME).put(cmd);
            return 1;
        }
        return -1;
    }

    public int update(ImageEntity entity) {
        if (_images.containsKey(entity.id)) {
            _images.put(entity.id, entity);
            ImageCmd cmd = new ImageCmd(entity, ImageCmd.TYPES.UPDATE);
            QueueManager.getInstance(Configuration.CmdQueue.QUEUE_NAME).put(cmd);
            return 1;
        }
        return -1;
    }

    public int delete(ImageEntity entity) {
        if (_images.containsKey(entity.id)) {
            for (int i = 0; i < _images.size(); i++) {
                if (_imageIds.get(i) == entity.id) {
                    _imageIds.remove(i);
                    break;
                }
            }
            if (_userId2Images.containsKey(entity.userId)) {
                List<Integer> entities = _userId2Images.get(entity.userId);
                for (int i = 0; i < entities.size(); i++) {
                    if (entities.get(i) == entity.id) {
                        entities.remove(i);
                        break;
                    }
                }
            }
            _images.remove(entity.id);
            ImageCmd cmd = new ImageCmd(entity, ImageCmd.TYPES.DELETE);
            QueueManager.getInstance(Configuration.CmdQueue.QUEUE_NAME).put(cmd);
            return 1;
        }
        return -1;
    }

    public List<ImageEntity> getEntities(int page, int itemsPerPage) {
        ArrayList<ImageEntity> res = new ArrayList<>();
        if (page <= 0) {
            return res;
        }
        int startOffset = _imageIds.size() - 1 - (page - 1) * itemsPerPage;
        if (startOffset < 0) {
            return res;
        }
        for (int i = 0; i < itemsPerPage; i++) {
            int idx = startOffset - i;
            if (idx < 0) {
                break;
            }
            res.add(_images.get(_imageIds.get(idx)));
        }
        return res;
    }

    public List<ImageEntity> getEntities(String userId, int page, int itemsPerPage) {
        ArrayList<ImageEntity> res = new ArrayList<>();
        if (page <= 0) {
            return res;
        }
        if (_userId2Images.containsKey(userId)) {
            List<Integer> entities = _userId2Images.get(userId);
            int startOffset = entities.size() - 1 - (page - 1) * itemsPerPage;
            if (startOffset < 0) {
                return res;
            }
            for (int i = 0; i < itemsPerPage; i++) {
                int idx = startOffset - i;
                if (idx < 0) {
                    break;
                }
                res.add(_images.get(entities.get(idx)));
            }
        }
        return res;
    }

    public ImageEntity getEntity(int id) {
        if (_images.containsKey(id)) {
            return _images.get(id);
        }
        return null;
    }

    public static void main(String[] args) {
        Collection<ImageEntity> entities = getInstance().getEntities(1, Integer.MAX_VALUE);
        for (ImageEntity entity : entities) {
            System.out.println(entity.ratio);
        }
    }
}
