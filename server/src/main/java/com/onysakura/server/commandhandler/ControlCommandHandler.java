package com.onysakura.server.commandhandler;

import com.onysakura.common.dto.Request;
import com.onysakura.common.util.TaskExecutors;
import com.onysakura.common.command.Commands;
import com.onysakura.common.exception.ServerException;
import com.onysakura.common.exception.TaskExecutorException;
import com.onysakura.common.util.PropertiesUtil;
import com.onysakura.server.netty.ChannelPair;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import static com.onysakura.common.constant.ExceptionMessageConstants.CONNECTION_EXIST;
import static com.onysakura.server.constant.MessageConstants.CONNECTION_SUCCEED;
import static com.onysakura.common.constant.ExceptionMessageConstants.CONNECT_PUPPET_FAILED;
import static com.onysakura.server.constant.ConfigConstants.CONFIG_FILE_PATH;
import static com.onysakura.server.constant.ConfigConstants.MASTER_CONNECT_PUPPET_RETRY_INTERVAL;
import static com.onysakura.server.constant.ConfigConstants.MASTER_CONNECT_PUPPET_RETRY_TIMES;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public class ControlCommandHandler extends AbstractServerCommandHandler {
    @Override
    public void handle0(ChannelHandlerContext ctx, Request request) throws Exception {
        final String puppetName = request.getPuppetName();
        //根据傀儡名称得到channel
        try {
            Boolean result=notifyPuppetConnect(ctx,puppetName);
            if(result==null || !result){
                info(request,CONNECT_PUPPET_FAILED);
                sendError(request,ctx,CONNECT_PUPPET_FAILED);
                return;
            }

            final ChannelPair channelPair = CONNECTED_CHANNELPAIRS.get(puppetName);
            if (channelPair != null) {
                final Channel masterChannel = channelPair.getMasterChannel();
                if (masterChannel!=null && masterChannel.isOpen()){
                    info(request,CONNECT_PUPPET_FAILED,CONNECTION_EXIST);
                    sendError(request,ctx,CONNECT_PUPPET_FAILED);
                }else {
                    channelPair.setMasterChannel(ctx.channel());
                    info(request, CONNECTION_SUCCEED);
                    //通知傀儡
                    channelPair.getPuppetChannel().writeAndFlush(buildResponse(request, Commands.CONTROL));
                    //给控制端返回消息，通知其做好准备
                    ctx.writeAndFlush(buildResponse(request, Commands.CONTROL, puppetName));
                }
               }

        }catch (TaskExecutorException e){
            error(request,CONNECT_PUPPET_FAILED,e.getMessage());
            throw new ServerException(e.getMessage(),e);
        }
    }


    /**
     * 通知傀儡建立连接
     * @param name 傀儡名
     * @return 通知结果
     * @throws TaskExecutorException 任务执行异常
     */
    private Boolean notifyPuppetConnect(ChannelHandlerContext ctx,String name) throws TaskExecutorException{
        int interval= PropertiesUtil.getInt(CONFIG_FILE_PATH, MASTER_CONNECT_PUPPET_RETRY_INTERVAL,1000);
        int times=PropertiesUtil.getInt(CONFIG_FILE_PATH, MASTER_CONNECT_PUPPET_RETRY_TIMES,1);

        return TaskExecutors.submit(()->{
            //1.检查已连接的傀儡中是否有目标傀儡
            if (CONNECTED_CHANNELPAIRS.containsKey(name)) {
                final ChannelPair pair = CONNECTED_CHANNELPAIRS.get(name);
                final Channel masterChannel = pair.getMasterChannel();
                //如果当前傀儡已经在被不同的控制端控制，则拒绝
                if(masterChannel!=null && ctx.channel()!=masterChannel && masterChannel.isOpen()){
                    return false;
                }

                final Channel puppetChannel = pair.getPuppetChannel();
                if (puppetChannel != null && puppetChannel.isOpen()) {
                    return true;
                }
            }

            //2.返回null时，会重试
            return null;
        },interval,times);
    }
}
