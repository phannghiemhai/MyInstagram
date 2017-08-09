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
public abstract interface IQueue
{
  public abstract IQueueCmd take();
  
  public abstract boolean put(IQueueCmd paramQueueCommand);
  
  public abstract void process();
  
  public abstract int size();
  
  public abstract int remaining();
  
  public abstract int getWorkerNum();
  
  public abstract int getMaxLength();
}
