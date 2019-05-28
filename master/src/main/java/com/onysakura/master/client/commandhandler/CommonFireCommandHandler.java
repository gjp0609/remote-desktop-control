package com.onysakura.master.client.commandhandler;

import com.onysakura.common.InputEvent.MasterMouseEvent;
import com.onysakura.common.command.Commands;
import com.onysakura.common.dto.Request;
import com.onysakura.common.util.TaskExecutors;
import com.onysakura.master.client.constant.ExceptionMessageConstants;
import com.onysakura.master.client.exception.ConnectionException;
import com.onysakura.master.client.exception.FireCommandHandlerException;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public class CommonFireCommandHandler extends AbstractMasterFireCommandHandler<Object> {
   private ArrayBlockingQueue<Request> blockingQueue=new ArrayBlockingQueue<>(100);

   private Runnable task=new Runnable() {
       @Override
       public void run() {
           while (true) {
               Request mouseMovingRequest = null;
               Request request=null;
               try {
                   while ((request = blockingQueue.take()) != null) {
                       //从队列中取得最后一个mouse moving的请求
                       if (request.getCommand() == Commands.MOUSE) {
                           MasterMouseEvent mouseEvent = (MasterMouseEvent) request.getValue();
                           if (mouseEvent.isMouseMoved()) {
                               mouseMovingRequest = request;
                               continue;
                           }
                       }
                       //发送鼠标位置命令
                       try {
                           if (mouseMovingRequest != null) {
                               sendRequest(mouseMovingRequest);
                               mouseMovingRequest = null;
                           }
                       } catch (ConnectionException e) {
                           error(mouseMovingRequest, e.getMessage());
                       }

                       //发送其它命令
                       try {
                           sendRequest(request);
                       } catch (ConnectionException e) {
                           error(request, e.getMessage());
                       }
                   }
               }catch (InterruptedException e){
                   error(this,e.getMessage());
               }
               //如果队列中或队列的后面都是mouse moving请求
               try {
                   if (mouseMovingRequest != null) {
                       sendRequest(mouseMovingRequest);
                   }
               } catch (ConnectionException e) {
                   error(mouseMovingRequest, e.getMessage());
               }
           }
       }
   };

   public CommonFireCommandHandler(){
       TaskExecutors.submit(task,1000);
   }

    @Override
    public void fire(String puppetName,Enum<Commands> command,Object data) throws FireCommandHandlerException {
        if (puppetName==null){
            throw new FireCommandHandlerException(ExceptionMessageConstants.PUPPET_NAME_EMPTY);
        }

        final Request request = buildRequest(command, puppetName, data);

        blockingQueue.offer(request);
        /*try {
            sendRequest(request);
        }catch (ConnectionException e){
            throw  new FireCommandHandlerException(e);
        }*/
    }
}
