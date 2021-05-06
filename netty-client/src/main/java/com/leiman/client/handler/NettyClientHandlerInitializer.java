package com.leiman.client.handler;

import com.leiman.codec.InvocationDecoder;
import com.leiman.codec.InvocationEncoder;
import com.leiman.dispatcher.MessageDispatcher;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName : NettyClientHandlerInitializer
 * @Description :
 * @Author :
 * @Date: 2021-04-07 15:58
 */
@Component
public class NettyClientHandlerInitializer extends ChannelInitializer {
    /**
     * 心跳超时时间
     */
    private static final Integer READ_TIMEOUT_SECONDS = 60;

    @Autowired
    private MessageDispatcher messageDispatcher;

    @Autowired
    private NettyClientHandler nettyClientHandler;

    @Override
    protected void initChannel(Channel channel) throws Exception {
        channel.pipeline()
                // 配置读空闲是60秒
                .addLast(new IdleStateHandler(READ_TIMEOUT_SECONDS,0,0))
                // 空闲检测 抛出异常,并关闭对端 channel
                .addLast(new ReadTimeoutHandler(3 * READ_TIMEOUT_SECONDS, TimeUnit.SECONDS))
                //编码器
                .addLast(new InvocationEncoder())
                //解码器
                .addLast(new  InvocationDecoder())
                //消息分发器
                .addLast(messageDispatcher)
                // 客户端处理器
                .addLast(nettyClientHandler);
    }
}
