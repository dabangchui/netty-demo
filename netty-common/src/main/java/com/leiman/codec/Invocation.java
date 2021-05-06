package com.leiman.codec;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通信协议的消息体
 * 功能描述:讯协议数据包类，可以是请求包，也可以是返回包
 */
@Data

public class Invocation {
    /**
     * 连接通道
     **/
    protected Channel channel;
    /**
     * 协议号
     **/
    private int protoId;
    /**
     * 包体字节数组
     **/
    protected byte[] bodyBytes;
    /**
     * IP
     **/
    protected String remoteAddress ;
    /**
     * 最后登录时间
     **/
    protected long lastTime;

    public Invocation() {}

    public Invocation(Channel channel, int protoId, byte[] bodyBytes, String remoteAddress, long lastTime) {
        this.channel = channel;
        this.protoId = protoId;
        this.bodyBytes = bodyBytes;
        this.remoteAddress = remoteAddress;
        this.lastTime = lastTime;
    }

    public Invocation(int protoId, byte[] bodyBytes) {
        this.protoId = protoId;
        this.bodyBytes = bodyBytes;
    }
}
