package com.leiman.dispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @ClassName : MessageHandlerContainer
 * @Description : 作为 MessageHandler 的容器
 * @Author :
 * @Date: 2021-04-07 16:59
 */
public class MessageHandlerContainer implements InitializingBean {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<Integer,MessageHandler> handlers = new HashMap<>();

    @Autowired
    private ApplicationContext applicationContext;

    /**
     *  通过 ApplicationContext 获得所有 MessageHandler Bean 添加到 handlers 中
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        applicationContext.getBeansOfType(MessageHandler.class).values()
                .forEach(messageHandler -> handlers.put(messageHandler.getProtoId(), messageHandler));
        logger.info("[afterPropertiesSet][消息处理器数量：{}]", handlers.size());
    }

    /**
     * 获得类型对应的 MessageHandler
     * @param protoId 协议号
     * @return MessageHandler
     */
    public MessageHandler getMessageHandler(Integer protoId){
        MessageHandler handler = handlers.get(protoId);
        if (handler == null) {
            throw new IllegalArgumentException(String.format("协议号 (%s) 找不到匹配的 MessageHandler 处理器",protoId));
        }
        return handler;
    }

    /**
     * 通过 MessageHandler 中，通过解析其类上的泛型，获得消息类型对应的 Class 类
     * @param handler
     * @return
     */
    static Class<? extends Message> getMessageClass(MessageHandler handler){
        // 获得 Bean 对应的 Class 类名。因为有可能被 AOP 代理过。
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(handler);
        // 获取接口的 Type 数组
        Type[] interfaces = targetClass.getGenericInterfaces();
        Class<?> superclass = targetClass.getSuperclass();
        while ((Objects.isNull(interfaces) || 0 == interfaces.length) && Objects.nonNull(superclass)){
            interfaces = superclass.getGenericInterfaces();
            superclass = targetClass.getSuperclass();
        }
        if (Objects.nonNull(interfaces)){
            // 遍历 interfaces 数组
            for (Type type : interfaces){
                // 要求 type 是泛型数组
                if(type instanceof ParameterizedType){
                    ParameterizedType parameterizedType = (ParameterizedType) type;
                    // 要求是 MessageHandler 接口
                    if (Objects.equals(parameterizedType.getRawType(),MessageHandler.class)){
                        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                        // 取 首个元素
                        if (Objects.nonNull(actualTypeArguments) && actualTypeArguments.length > 0){
                            return (Class<Message>) actualTypeArguments[0];
                        }
                        else {
                            throw new IllegalStateException(String.format("类型(%s) 获得不到消息类型", handler));
                        }
                    }
                }
            }
        }
        throw new IllegalStateException(String.format("类型(%s) 获得不到消息类型", handler));
    }

}
