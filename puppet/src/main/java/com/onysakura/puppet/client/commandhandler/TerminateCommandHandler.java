package com.onysakura.puppet.client.commandhandler;

import com.onysakura.common.dto.Response;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public class TerminateCommandHandler extends AbstractPuppetCommandHandler {
    @Override
    protected void handle0(ChannelHandlerContext ctx, Response response) throws Exception {
        stopUnderControlled();
    }
}
