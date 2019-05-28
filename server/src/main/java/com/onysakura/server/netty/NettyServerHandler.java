package com.onysakura.server.netty;

import com.onysakura.common.util.CommandHandlerLoader;
import com.onysakura.common.dto.Request;
import com.onysakura.common.command.handler.ICommandHandler;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Cool-Coding 2018/7/24
 */
@ChannelHandler.Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<Request> {

    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServerHandler.class);

    @SuppressWarnings("unchecked")
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Request msg) throws Exception {
        LOGGER.debug(msg.toString());
        final ICommandHandler commandHandler = CommandHandlerLoader.getCommandHandler(msg.getCommand());
        commandHandler.handle(ctx,msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.info(cause.getMessage());
        ctx.close();
    }
}
