package com.leiman.messagehandler.chat;

import com.google.protobuf.InvalidProtocolBufferException;
import com.leiman.codec.Invocation;
import com.leiman.dispatcher.MessageHandler;
import com.leiman.protobuf.MessageAll;
import com.leiman.server.NettyChannelManager;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChatSendToOneHandler implements MessageHandler {

    @Autowired
    private NettyChannelManager nettyChannelManager;

    @Override
    public void execute(Channel channel, Invocation invocation) throws InvalidProtocolBufferException {
        MessageAll.csSbkToken csMsg = MessageAll.csSbkToken.parseFrom(invocation.getBodyBytes());
        // 这里，假装直接成功
        MessageAll.scSbkEventPreStart.Builder builder = MessageAll.scSbkEventPreStart.newBuilder();
        builder.setRes(MessageAll.EMRes.succsess);
        channel.writeAndFlush(new Invocation(invocation.getProtoId(), builder.build().toByteArray()));

        // 创建转发的消息，发送给指定用户
        //nettyChannelManager.send(csMsg.getAccountId(), new Invocation(invocation.getProtoId(), builder.build().toByteArray()));
    }

    @Override
    public Integer getProtoId() {
        return 49;
    }

}
