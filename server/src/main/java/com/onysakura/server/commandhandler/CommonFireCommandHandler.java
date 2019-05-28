package com.onysakura.server.commandhandler;

import com.onysakura.common.constant.ExceptionMessageConstants;
import com.onysakura.common.dto.Request;
import com.onysakura.common.dto.Response;
import com.onysakura.server.netty.ChannelPair;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import static com.onysakura.common.constant.ExceptionMessageConstants.PUPPET_CONNECTION_LOST;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public class CommonFireCommandHandler extends AbstractServerCommandHandler{
    @Override
    public void handle0(ChannelHandlerContext ctx, Request request) throws Exception {
        final String puppetName = request.getPuppetName();

        //检查傀儡端连接是否正常
        ChannelPair channelPair = CONNECTED_CHANNELPAIRS.get(puppetName);
        if (channelPair==null || channelPair.getPuppetChannel()==null || !channelPair.getPuppetChannel().isOpen()){
            error(request, ExceptionMessageConstants.PUPPET_CONNECTION_LOST);
            sendError(request, ctx,PUPPET_CONNECTION_LOST);
            return;
        }

        //发送数据
        final Channel puppetChannel = channelPair.getPuppetChannel();
        Response response=null;
        response = buildResponse(request,request.getCommand(),request.getValue());
        puppetChannel.writeAndFlush(response);
    }
}
