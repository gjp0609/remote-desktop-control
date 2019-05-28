package com.onysakura.common.dto;

import com.onysakura.common.exception.ServerException;

/**
 * @author: cool coding
 * @date: 2018/1/7
 * 请求响应结果
 */
public class Response extends Invocation {
    /**
     * 异常
     */
    private ServerException error;

    public ServerException getError() {
        return error;
    }

    public void setError(ServerException error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "Response{" +
                "id='" + getId() + '\'' +
                ", puppetName='" + getPuppetName() + '\'' +
                ", error=" + error +
                ", command=" + getCommand() +
                ", value=" + getValue() +
                '}';
    }
}
