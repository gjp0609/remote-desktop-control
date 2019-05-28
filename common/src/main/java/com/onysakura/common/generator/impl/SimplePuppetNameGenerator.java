package com.onysakura.common.generator.impl;

import com.onysakura.common.generator.PuppetNameGenerate;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

/**
 * @author Cool-Coding
 *         2018/8/3
 */
public class SimplePuppetNameGenerator implements PuppetNameGenerate {
    @Override
    public String getPuppetName(ChannelHandlerContext ctx) {
        return UUID.randomUUID().toString();
    }
}
