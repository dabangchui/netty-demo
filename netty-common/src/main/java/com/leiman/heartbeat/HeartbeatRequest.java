package com.leiman.heartbeat;

import com.leiman.dispatcher.Message;

/**
 * @ClassName : HeartbeatRequest
 * @Description : 消息 - 心跳请求
 * @Author :
 * @Date: 2021-04-08 09:17
 */
public class HeartbeatRequest implements Message {

    /**
     * 类型 - 心跳请求
     */
    public static final String TYPE ="HEARTBEAT_REQUEST";

    @Override
    public String toString() {
        return "HeartbeatRequest{}";
    }
}
