package com.leiman.server;

import com.leiman.codec.Invocation;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 *  :客户端 Channel 管理器。提供两种功能
 *  * 1. 客户端 Channel 的管理
 *  * 2. 向客户端 Channel 发送消息
 * @Author :
 * @Date: 2021-04-07 14:41
 */
@Component
public class NettyChannelManager {
    /**
     * {@link Channel#attr(AttributeKey)} 属性中，表示 Channel 对应的用户
     */
    private static final AttributeKey<Integer> CHANNEL_ATTR_KEY_USER = AttributeKey.newInstance("user");

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Channel 映射
     */
    private ConcurrentMap<ChannelId, Channel> channels = new ConcurrentHashMap<>();

    /**
     * 用户与 Channel 的映射
     * 通过它，可以获取用户对应的 channel 这样，我们可以向指定用户发送消息
     */
    private ConcurrentMap<Integer, Channel> userChannels = new ConcurrentHashMap<>();

    /**
     * 添加 Channel 到 Channels 中
     * @param channel
     */
    public void add(Channel channel){
        channels.put(channel.id(),channel);
        logger.info("[add] [一个连接({}) 加入]",channel.id());
    }

    public void addUser(Channel channel,Integer accountId){
        Channel existChannel = channels.get(channel.id());
        if (existChannel == null){
            logger.error("[addUser] [连接 ({}) 不存在]",channel.id());
            return;
        }
        //设置属性
        channel.attr(CHANNEL_ATTR_KEY_USER).set(accountId);
        // 添加到 userChannels
        userChannels.put(accountId,channel);
    }

    public void remove(Channel channel){
        // 移除 channels
        channels.remove(channel.id());
        // 移除 userChannels
        if (channel.hasAttr(CHANNEL_ATTR_KEY_USER)){
            userChannels.remove(channel.attr(CHANNEL_ATTR_KEY_USER).get());
        }
        logger.info("[remove] [一个连接({}离开)]", channel.id());
    }

    public void send(Integer accountId, Invocation invocation){
        Channel channel = userChannels.get(accountId);
        if (channel == null){
            logger.error("[send] [连接不存在]");
        }
        if (!channel.isActive()) {
            logger.error("[send] [连接({}) 未激活]", channel.id());
        }
        //发送消息
        channel.writeAndFlush(invocation);
    }

    public void sendAll(Invocation invocation){
        for (Channel channel : channels.values()) {
            if (channel.isActive()) {
                logger.error("[send] [连接({})未激活]",channel.id());
                return;
            }
            // 发送消息
            channel.writeAndFlush(invocation);
        }
    }


























}
