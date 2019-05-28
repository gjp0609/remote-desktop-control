package com.onysakura.master.client.ui;

import com.onysakura.common.command.Commands;
import com.onysakura.common.netty.INettyClient;
import com.onysakura.master.client.exception.MasterClientException;

/**
 * @author cool-coding
 * @date 2018/8/2
 */
public interface IMasterDesktop extends INettyClient{
    /**
     * 桌面窗体设置
     */
    void setting();

    /**
     * 菜单
     */
    void initMenu();

    /**
     * 窗体
     */
    void initBody();

    /**
     * 启动
     */
    void lanuch();

    /**
     * 启动远程傀儡的屏幕
     * @param puppetName
     */
    void lanuch(String puppetName);

    /**
     * 刷新远程屏幕
     * @param puppetName 傀儡名
     * @param bytes      截屏字节数组或截屏部分字节
     */
    void refreshScreen(String puppetName,byte[] bytes);

    /**
     * 终止远程
     * @param puppetName 傀儡名
     */
    void terminate(String puppetName);

    /**
     * 发送命令
     * @param puppetName 傀儡名
     * @param command    命令
     * @param data       数据
     */
    void fireCommand(String puppetName,Enum<Commands> command,Object data) throws MasterClientException;
}
