package com.leiman.server;

import com.leiman.server.handle.NettyServerHandlerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @ClassName : NettyServer
 * @Description :
 * @Author :
 * @Date: 2021-04-07 13:57
 */
@Component
public class NettyServer {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${netty.port}")
    private Integer port;

    @Autowired
    private NettyServerHandlerInitializer nettyServerHandlerInitializer;

    /**
     * boss 线程组，用于服务端接收客户端的连接
     */
    private EventLoopGroup bossGroup = new NioEventLoopGroup();

    /**
     * worker 线程组，用于服务端接收客户端的读写
     */
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    /**
     * Netty Server Channel
     */
    private Channel channel;

    @PostConstruct
    public void start() throws InterruptedException {
        // 创建serverBootstrap对象,用于Netty Server 启动
        ServerBootstrap  bootStrap = new ServerBootstrap();
        // 设置 ServerBootStrap 的各种属性
        bootStrap.group(bossGroup,workerGroup)
                // 指定Channel 为服务端 NioServerSocketChannel
                // 它是 Netty 定义的 NIO 服务端 TCP Socket 实现类。
                .channel(NioServerSocketChannel.class)
                // 设置Netty Server的端口
                .localAddress(port)
                // 服务端连接受客户端的连接队列大小
                // 因为 TCP 建立连接是三次握手，所以第一次握手完成后会添加到服务端的连接队列中
                .childOption(ChannelOption.SO_BACKLOG,1024)
                // TCP Keepalive 机制，实现TCP层级的心跳保活功能
                .childOption(ChannelOption.SO_KEEPALIVE,true)
                // 允许较小的数据包发送，较低延迟
                .childOption(ChannelOption.TCP_NODELAY,true)
                .childHandler(nettyServerHandlerInitializer);

        //绑定端口,并同步等待成功，既启动服务端
        ChannelFuture future = bootStrap.bind().sync();
        if (future.isSuccess()){
            channel = future.channel();
            logger.info("[start] [Netty Server 启动在 {} 端口]",port);
            logger.info("Git Master 提交测试");
            logger.info("Git hot—fix 提交测试");
            logger.info("Git push1111 提交测试");
        }
    }

    @PreDestroy
    public void shutdown(){
        // 关闭 Netty Server
        if (channel != null){
            channel.close();
        }
        // 优雅关闭两个 EventLoopGroup 对象
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }












}
