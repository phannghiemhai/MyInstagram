/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queue;

import queue.cmd.IQueueCmd;

/**
 *
 * @author haipn
 */
public class QueueWorker implements Runnable {

    private IQueue _queue;
    private long _msleep_idle = 1000L;

    public QueueWorker(IQueue queue) {
        this._queue = queue;
    }

    @Override
    public void run() {
        try {
            for (;;) {
                IQueueCmd command = _queue.take();
                if (command != null) {
                    command.execute();

                } else if (_msleep_idle > 0L) {
                    Thread.sleep(_msleep_idle);
                }
            }
        } catch (Exception ex) {
//      logger.error("Error in exec QueueWorker", ex);
        }
    }
}
