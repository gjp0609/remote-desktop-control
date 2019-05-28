package com.onysakura.server.commandhandler;

import com.onysakura.common.dto.Request;
import com.onysakura.server.constant.MessageConstants;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public class HeartBeatCommandHandler extends AbstractServerCommandHandler {
    @Override
    public void handle0(ChannelHandlerContext ctx, Request request) throws Exception {
        final String puppetName = request.getPuppetName();
        debug(request, MessageConstants.RECEIVE_A_HEARTBEAT,puppetName);
    }
}
