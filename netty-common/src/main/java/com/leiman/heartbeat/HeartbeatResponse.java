package com.leiman.heartbeat;

import com.leiman.dispatcher.Message;

/**
 * @ClassName : HeartbeatResponse
 * @Description : 消息 - 心跳响应
 * @Author :
 * @Date: 2021-04-08 09:17
 */
public class HeartbeatResponse implements Message {

    /**
     * 类型 - 心跳响应
     */
    public static final String TYPE = "HEARTBEAT_RESPONSE";

    @Override
    public String toString() {
        return "HeartbeatResponse{}";
    }
}
