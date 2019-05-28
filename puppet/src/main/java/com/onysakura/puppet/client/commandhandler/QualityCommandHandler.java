package com.onysakura.puppet.client.commandhandler;

import com.onysakura.common.dto.Response;
import com.onysakura.puppet.client.constant.ExceptionMessageConstants;
import com.onysakura.puppet.client.exception.NullValueException;
import com.onysakura.puppet.client.ui.impl.PuppetDesktop;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public class QualityCommandHandler extends AbstractPuppetCommandHandler {


    @Override
    protected void handle0(ChannelHandlerContext ctx, Response request) throws Exception {
        final Object result = request.getValue();
        if (result==null){
            error(request,ExceptionMessageConstants.QUALITY_EVENT_VALUE_NULL);
            throw new NullValueException(ExceptionMessageConstants.QUALITY_EVENT_VALUE_NULL);
        }

        if (!(request.getValue() instanceof Integer)){
            error(request,ExceptionMessageConstants.QUALITY_EVENT_VALUE_ERROR);
            throw new ClassCastException(ExceptionMessageConstants.QUALITY_EVENT_VALUE_ERROR);
        }


        ((PuppetDesktop) getReplay()).setQuality((Integer)result);
    }
}
