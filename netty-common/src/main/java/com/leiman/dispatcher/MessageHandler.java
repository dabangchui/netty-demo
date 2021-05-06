package com.leiman.dispatcher;


import com.google.protobuf.InvalidProtocolBufferException;
import com.leiman.codec.Invocation;
import io.netty.channel.Channel;

/**
 * @ClassName : MessageHandler
 * @Description : 消息处理器接口
 * 定义了泛型 <T> ，需要是 Message 的实现类
 * @Author :
 * @Date: 2021-04-07 16:55
 */

public interface MessageHandler {
// TODO: 2021/4/8  <T extends Message>
    /**
     * 执行处理消息
     * @param channel 通道
     * @param invocation 协议包
     */
    void execute(Channel channel, Invocation invocation) throws InvalidProtocolBufferException;

    /**
     * 每个 Message 实现类上的 ProtoId 静态字段
     * @return 消息类型
     */
    Integer getProtoId();
}
