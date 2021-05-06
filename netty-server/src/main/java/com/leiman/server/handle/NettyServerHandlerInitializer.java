package com.leiman.server.handle;

import com.leiman.codec.InvocationDecoder;
import com.leiman.codec.InvocationEncoder;
import com.leiman.dispatcher.MessageDispatcher;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.omg.CORBA.TIMEOUT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName : NettyServerHandlerInitializer
 * @Description :
 * @Author :
 * @Date: 2021-04-07 14:27
 */
@Component
public class NettyServerHandlerInitializer extends ChannelInitializer<Channel> {
    private static final Integer READ_TIMEOUT_SECONDS = 3 * 60;

    @Autowired
    private MessageDispatcher messageDispatcher;
    @Autowired
    private NettyServerHandler nettyServerHandler;

    @Override
    protected void initChannel(Channel channel) throws Exception {
        // 添加一堆 NettyServerHandler 到 ChannelPipeline 中
        channel.pipeline()
                /**
                 * TCP 自带的空闲检测机制，默认是 2 小时
                 * 在业务层面，自己实现空闲检测，保证尽快发现客户端与服务端实际已经断开的情况
                 * 1，服务端发现 180 秒未从客户端读取到消息，主动断开连接。
                 * 2，客户端发现 180 秒未从服务端读取到消息，主动断开连接。
                 *      180秒？ 过短的时间，会导致心跳过于频繁，占用过多资源
                 *
                 * 考虑到客户端和服务端之间并不是一直有消息的交互，所以我们需要增加心跳机制
                 * 客户端每 60 秒向服务端发起一次心跳消息，保证服务端可以读取到消息。
                 * 服务端在收到心跳消息时，回复客户端一条确认消息，保证客户端可以读取到消息
                 *      60 秒？三次机会，确认是否心跳超时
                 */
                // 空闲检测 抛出异常,并关闭对端 channel
                .addLast(new ReadTimeoutHandler(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS))
                .addLast(new ProtobufVarint32FrameDecoder())
                .addLast(new ProtobufVarint32LengthFieldPrepender())
                //编码器
                .addLast(new ProtobufEncoder())
                //.addLast(new InvocationEncoder())
                //解码器
                .addLast(new InvocationDecoder())
                //消息分发器
                .addLast(messageDispatcher)
                //服务端处理器
                .addLast(nettyServerHandler);

    }
}
