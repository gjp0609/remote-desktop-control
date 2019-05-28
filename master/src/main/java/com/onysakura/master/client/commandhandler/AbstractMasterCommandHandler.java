package com.onysakura.master.client.commandhandler;

import com.onysakura.common.command.Commands;
import com.onysakura.common.dto.Request;
import com.onysakura.common.dto.Response;
import com.onysakura.common.command.handler.ICommandHandler;
import com.onysakura.common.constant.Constants;
import com.onysakura.common.constant.ExceptionMessageConstants;
import com.onysakura.common.exception.ResponseHandleException;
import com.onysakura.common.generator.SequenceGenerate;
import com.onysakura.common.util.BeanUtil;
import com.onysakura.common.util.MacUtils;
import com.onysakura.master.client.constant.MessageConstants;
import com.onysakura.master.client.exception.ConnectionException;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Arrays;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public abstract class AbstractMasterCommandHandler implements ICommandHandler<Response> {

    /** logger */
    private static final Logger LOGGER= LoggerFactory.getLogger(AbstractMasterCommandHandler.class);

    /**
     * 序号生成器
     */
    private static final SequenceGenerate generator= BeanUtil.getBean(SequenceGenerate.class);

    /**
     *连接时得到ChannelHandlerContext,供其它命令使用，故使用静态成员变量
     */
    private static ChannelHandlerContext ctx;

    @Override
    public void handle(ChannelHandlerContext ctx, Response response) throws Exception {
        final Enum<Commands> command = response.getCommand();
        if (command==null){
            error(response, ExceptionMessageConstants.REQUIRED_COMMAND);
            ctx.channel().close();
            throw new ResponseHandleException(ExceptionMessageConstants.REQUIRED_COMMAND);
        }

        handle0(ctx,response);
    }

    protected abstract void handle0(ChannelHandlerContext ctx, Response response) throws Exception;


    public Request buildRequest(Enum<Commands> command, String puppetName, Object obj){
        String mac= MacUtils.getMAC();
        if (StringUtils.isEmpty(mac)){
            return null;
        }

        Request request = new Request();
        request.setId(Constants.MASTER + mac + generator.next());
        request.setCommand(command);
        request.setPuppetName(puppetName);
        request.setValue(obj);
        return  request;
    }

    protected void setChannelHandlerConext(ChannelHandlerContext ctx){
        AbstractMasterCommandHandler.ctx=ctx;
    }

    protected void sendRequest(Request request) throws ConnectionException {
        if (ctx!=null && ctx.channel()!=null && ctx.channel().isOpen()){
            debug(request, MessageConstants.PREPARING_TO_FIRE);
            ctx.writeAndFlush(request);
        }else{
            error(request, com.onysakura.master.client.constant.ExceptionMessageConstants.CONNECTION_SERVER_FAILED);
            throw new ConnectionException(com.onysakura.master.client.constant.ExceptionMessageConstants.CONNECTION_SERVER_FAILED);
        }
    }
    void error(Request request,String... message){
        LOGGER.error("{}:{}",request, Arrays.toString(message));
    }

    void error(Object object,String... message){
        LOGGER.error("{}:{}",object.getClass().getCanonicalName(), Arrays.toString(message));
    }

    void debug(Request request,String... message){
        LOGGER.debug("{}:{}",request, Arrays.toString(message));
    }

    void info(Request request,String... message){
        LOGGER.info("{}:{}",request, Arrays.toString(message));
    }

    void warn(Request request,String... message){
        LOGGER.warn("{}:{}",request, Arrays.toString(message));
    }

    void error(Response response,String message){
        LOGGER.error("{}:{}",response,message);
    }

    void debug(Response response,String message){
        LOGGER.debug("{}:{}",response,message);
    }

    void debug(String format,String message){
        LOGGER.debug(format,message);
    }

    void info(Response response,String message){
        LOGGER.info("{}:{}",response,message);
    }

    void warn(Response response,String message){
        LOGGER.warn("{}:{}",response,message);
    }

}