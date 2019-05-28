package com.onysakura.puppet.client.commandhandler;

import com.onysakura.common.command.Commands;
import com.onysakura.common.constant.ExceptionMessageConstants;
import com.onysakura.common.dto.Request;
import com.onysakura.common.dto.Response;
import com.onysakura.common.command.handler.ICommandHandler;
import com.onysakura.common.constant.Constants;
import com.onysakura.common.exception.ResponseHandleException;
import com.onysakura.common.generator.SequenceGenerate;
import com.onysakura.common.util.BeanUtil;
import com.onysakura.common.util.MacUtils;
import com.onysakura.puppet.client.ui.IReplay;
import com.onysakura.puppet.client.ui.MessageDialog;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Arrays;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public abstract class AbstractPuppetCommandHandler implements ICommandHandler<Response> {

    /** logger */
    private static final Logger LOGGER= LoggerFactory.getLogger(AbstractPuppetCommandHandler.class);


    /**
     * 序号生成器
     */
    private static final SequenceGenerate generator=BeanUtil.getBean(SequenceGenerate.class);

    /**
     * 桌面控制器
     */
    protected static final IReplay REPLAY = BeanUtil.getBean(IReplay.class);
    /**
     * 标识:是否正处于被控制中
     */
    private static volatile boolean isUnderControlled=false;

    /**
     * 傀儡名
     */
    private static String puppetName;

    /**
     * 前一个截屏
     */
    private  static byte[] previousScreen;

    @Override
    public void handle(ChannelHandlerContext ctx, Response response) throws Exception {
        final Enum<Commands> command = response.getCommand();
        if (command == null){
            LOGGER.error(ExceptionMessageConstants.REQUIRED_COMMAND);
            ctx.channel().close();
            throw new ResponseHandleException(ExceptionMessageConstants.REQUIRED_COMMAND);
        }


        handle0(ctx,response);
    }

    protected abstract void handle0(ChannelHandlerContext ctx, Response response) throws Exception;

    public static Request buildConnectionRequest(int connectionCount){
        return buildRequest(Commands.CONNECT,connectionCount);
    }

    public static Request buildRequest(Enum<Commands> command, Object value){
        String mac= MacUtils.getMAC();
        if (StringUtils.isEmpty(mac)){
            return null;
        }
        Request request = new Request();
        request.setId(Constants.PUPPET + mac + generator.next());
        request.setCommand(command);
        request.setPuppetName(puppetName);
        request.setValue(value);
        return  request;
    }

    void error(Response response,String message){
        LOGGER.error("{}:{}",response,message);
    }

    void error(Object object,String message){
        LOGGER.error("{}:{}",object.getClass().getCanonicalName(),message);
    }

    void debug(Response response,String... message){
        LOGGER.debug("{}:{}",response,Arrays.toString(message));
    }

    void debug(Request request,String... message){
        LOGGER.debug("{}:{}",request,Arrays.toString(message));
    }

    void info(Response response,String... message){
        LOGGER.info("{}:{}",response,Arrays.toString(message));
    }

    void warn(Response response,String message){
        LOGGER.warn("{}:{}",response,message);
    }

    protected void startUnderControlled(){
        AbstractPuppetCommandHandler.isUnderControlled=true;
        AbstractPuppetCommandHandler.previousScreen = null;
    }

    protected void stopUnderControlled(){
        AbstractPuppetCommandHandler.isUnderControlled=false;
        AbstractPuppetCommandHandler.previousScreen=null;
    }

    protected static void setPreviousScreen(byte[] previousScreen){
        AbstractPuppetCommandHandler.previousScreen = previousScreen;
    }

    protected static byte[] getPreviousScreen() {
        return previousScreen;
    }

    public static IReplay getReplay() {
        return REPLAY;
    }

    protected boolean isUnderControlled(){
        return AbstractPuppetCommandHandler.isUnderControlled;
    }

    public static void setPuppetName(String puppetName) {
        AbstractPuppetCommandHandler.puppetName = puppetName;
    }

    public static String getPuppetName() {
        return puppetName;
    }

    /**
     * 显示消息对话框
     * @param title
     * @param message
     */
    public static void popMessageDialog(String title,String message){
        BeanUtil.getBean(MessageDialog.class,title).showMessage(message);
    }
}
