package com.leiman.config;

import com.leiman.dispatcher.MessageDispatcher;
import com.leiman.dispatcher.MessageHandlerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName : NettyConfig
 * @Description :
 * @Author :
 * @Date: 2021-04-07 13:56
 */
@Configuration
public class NettyServerConfig {

    @Bean
    public MessageDispatcher messageDispatcher(){
        return new MessageDispatcher();
    }

    @Bean
    public MessageHandlerContainer messageHandlerContainer(){
        return new MessageHandlerContainer();
    }
}
