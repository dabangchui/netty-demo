package com.leiman.config;

import com.leiman.dispatcher.MessageDispatcher;
import com.leiman.dispatcher.MessageHandlerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName : NettyClientConfig
 * @Description :
 * @Author :
 * @Date: 2021-04-07 18:00
 */
@Configuration
public class NettyClientConfig {
    @Bean
    public MessageDispatcher messageDispatcher() {
        return new MessageDispatcher();
    }

    @Bean
    public MessageHandlerContainer messageHandlerContainer() {
        return new MessageHandlerContainer();
    }

}
