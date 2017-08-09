/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queue;

import queue.cmd.IQueueCmd;
import java.util.Map;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

/**
 *
 * @author haipn
 */
public class QueueManager {

    private static Map<String, QueueManager> _instances = new NonBlockingHashMap();
    private IQueue _queue;

    public QueueManager() {
    }

    public static QueueManager getInstance(String name) {
        QueueManager instance = null;
        if (!_instances.containsKey(name)) {
            synchronized (QueueManager.class) {
                if (instance == null) {
                    instance = new QueueManager();
                    _instances.put(name, instance);
                }
            }
        } else {
            instance = _instances.get(name);
        }
        return instance;
    }

    public void init(int workerNum, int maxLength) {
        _queue = new BaseQueueImp(workerNum, maxLength);
        _queue.process();
    }

    public void process() {
        _queue.process();
    }

    public int size() {
        return _queue.size();
    }

    public int remaining() {
        return _queue.remaining();
    }

    public void put(IQueueCmd cmd) {
        _queue.put(cmd);
    }
}
