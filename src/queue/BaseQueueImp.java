/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queue;

import queue.cmd.IQueueCmd;
import java.util.concurrent.ArrayBlockingQueue;

/**
 *
 * @author haipn
 */
public class BaseQueueImp implements IQueue {

    private ArrayBlockingQueue<IQueueCmd> _queue;
    private int _workerNum;
    private int _maxLength;

    public BaseQueueImp(int workerNum, int maxLength) {
        this._workerNum = workerNum;
        this._maxLength = maxLength;
        _queue = new ArrayBlockingQueue(maxLength);
    }

    @Override
    public int size() {
        return _queue.size();
    }

    @Override
    public boolean put(IQueueCmd i) {
        if (_queue.remainingCapacity() < _workerNum) {
//            logger.error("Queue exceed max length!!!!!!");
            return false;
        }
        boolean ret = false;
        try {
            _queue.put(i);
            ret = true;
        } catch (InterruptedException e) {
//            logger.error("Exception in put", e);
        }
        return ret;
    }

    @Override
    public IQueueCmd take() {
        IQueueCmd cmd = null;
        try {
            cmd = (IQueueCmd) _queue.take();
        } catch (InterruptedException e) {
//            logger.error("Exception in take", e);
        }
        return cmd;
    }

    @Override
    public void process() {
        for (int i = 0; i < _workerNum; i++) {
            QueueWorker qw = new QueueWorker(this);
            new Thread(qw).start();
        }
    }

    @Override
    public int getWorkerNum() {
        return _workerNum;
    }

    @Override
    public int getMaxLength() {
        return _maxLength;
    }

    @Override
    public int remaining() {
        return _queue.remainingCapacity();
    }
}
