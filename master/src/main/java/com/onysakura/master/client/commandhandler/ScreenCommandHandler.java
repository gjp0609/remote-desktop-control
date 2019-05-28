package com.onysakura.master.client.commandhandler;

import com.onysakura.common.dto.Response;
import com.onysakura.common.util.BeanUtil;
import com.onysakura.master.client.constant.MessageConstants;
import com.onysakura.master.client.ui.IMasterDesktop;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author Cool-Coding
 *         2018/7/27
 *傀儡传过来的屏幕命令处理逻辑
 */
public class ScreenCommandHandler extends AbstractMasterCommandHandler {

    @Override
    protected void handle0(ChannelHandlerContext ctx, Response response) throws Exception {
       refreshScreen(response);
    }

    private void refreshScreen(Response response){
        debug(response, MessageConstants.RECEIVE_SCREEN_SNAPSHOT);
        if(response.getValue() instanceof byte[]) {
            byte[] bytes=(byte[])response.getValue();
            final IMasterDesktop desktop = BeanUtil.getBean(IMasterDesktop.class);
            desktop.refreshScreen(response.getPuppetName(),bytes);
        }
    }
}
