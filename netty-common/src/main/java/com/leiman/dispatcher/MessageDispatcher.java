package com.leiman.dispatcher;

import com.alibaba.fastjson.JSON;
import com.google.protobuf.InvalidProtocolBufferException;
import com.leiman.codec.Invocation;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @ClassName : MessageDispatcher
 * @Description : 将 Invocation 分发到其对应的 MessageHandler 中，进行业务逻辑的执行
 * @Author :
 * @Date: 2021-04-07 17:24
 */
@ChannelHandler.Sharable
public class MessageDispatcher extends SimpleChannelInboundHandler<Invocation> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MessageHandlerContainer messageHandlerContainer;

    /**
     * EventGroup 我们可以先简单理解成一个线程池，并且线程池的大小仅仅是 CPU 数量 * 2。
     * 每个 Channel 仅仅会被分配到其中的一个线程上，进行数据的读写。
     * 并且，多个 Channel 会共享一个线程，即使用同一个线程进行数据的读写。
     *
     * MessageHandler 的具体逻辑视线中，往往会涉及到 IO 处理，例如说进行数据库的读取。
     * 这样，就会导致一个 Channel 在执行 MessageHandler 的过程中，阻塞了共享当前线程的其它 Channel 的数据读取
     *
     * 因此，我们在这里创建了 executor 线程池，进行 MessageHandler 的逻辑执行，避免阻塞 Channel 的数据读取
     *
     * 如果 EventGroup 的线程池设置大一点，例如说 200 呢？
     * 对于长连接的 Netty 服务端，往往会有 1000 ~ 100000 的 Netty 客户端连接上来，这样无论设置多大的线程池，都会出现阻塞数据读取的情况
     */
    private final ExecutorService executor = Executors.newFixedThreadPool(200);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Invocation invocation) throws Exception {
        // 获得type 对应的 MessageHandler 处理器
        MessageHandler messageHandler = messageHandlerContainer.getMessageHandler(invocation.getProtoId());

        // todo 获得 MessageHandler 处理器的消息类
        // Class<? extends Message> messageClass = MessageHandlerContainer.getMessageClass(messageHandler);
        //解析消息 将 Invocation 的 message 解析成 MessageHandler 对应的消息对象
        // Message message = JSON.parseObject(invocation.getMessage(), messageClass);

        //执行逻辑
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    messageHandler.execute(ctx.channel(), invocation);
                }catch (InvalidProtocolBufferException e){
                    logger.error("[channelRead0][数据处理异常]",e);
                }

            }
        });
    }
















}
