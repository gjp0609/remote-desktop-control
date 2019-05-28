package com.onysakura.server;

import com.onysakura.server.netty.NettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ServerStarter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerStarter.class);

    public static void main(String[] args) {
        final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("server-beans.xml");
        context.start();
        /*
         * 将启动服务器放在spring初始化bean后，以便阻塞
         */
        try {
            context.getBean(NettyServer.class).start();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
