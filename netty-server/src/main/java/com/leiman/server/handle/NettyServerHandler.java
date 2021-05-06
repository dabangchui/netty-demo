package com.leiman.server.handle;

import com.leiman.server.NettyChannelManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName : NettyServerHandler
 * @Description : 服务端 Channel 的实现类，提供对客服端Channel 建立连接、断开连接、异常时的处理
 * @Author :
 * @Date: 2021-04-07 14:32
 */
@Component
//标记这个 ChannelHandler 可以被多个 Channel 使用。
@ChannelHandler.Sharable
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private NettyChannelManager channelManager;

    /**
     * 在客户端和服务端建立连接完成时
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 从管理器中添加
        channelManager.add(ctx.channel());
        channelManager.addUser(ctx.channel(),1);

    }

    /**
     * 在客户端和服务端断开连接时
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        channelManager.remove(ctx.channel());
    }

    /**
     * 在处理 Channel 的事件发生异常时
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("[exceptionCaught][连接({}) 发生异常]", ctx.channel().id(), cause);
        // 断开连接
        ctx.channel().close();
    }
}
