package com.leiman.codec;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName : InvocationEncoder
 * @Description : 实现将 Invocation 序列化，并写入到 TCP Socket 中
 * @Author :
 * @Date: 2021-04-07 16:24
 */
public class InvocationEncoder extends MessageToByteEncoder<Invocation> {

    private Logger logger = LoggerFactory.getLogger(getClass());


    /**
     * 对于粘包和拆包问题，常见的解决方案有三种：
     * 🔥 ① 客户端在发送数据包的时候，每个包都固定长度。比如 1024 个字节大小，如果客户端发送的数据长度不足 1024 个字节，则通过补充空格的方式补全到指定长度。
     * 这种方式，艿艿暂时没有找到采用这种方式的案例。
     * 🔥 ② 客户端在每个包的末尾使用固定的分隔符。例如 \r\n，如果一个包被拆分了，则等待下一个包发送过来之后找到其中的 \r\n，然后对其拆分后的头部部分与前一个包的剩余部分进行合并，这样就得到了一个完整的包。
     * 具体的案例，有 HTTP、WebSocket、Redis。
     * 🔥 ③ 将消息分为头部和消息体，在头部中保存有当前整个消息的长度，只有在读取到足够长度的消息之后才算是读到了一个完整的消息。
     *  在每次 Invocation 序列化成字节数组写入 TCP Socket 之前，先将字节数组的长度写到其中
     */

    /**
     * 实现将 Invocation 序列化，并写入到 TCP Socket 中
     * @param channelHandlerContext
     * @param invocation
     * @param byteBuf
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Invocation invocation, ByteBuf byteBuf) throws Exception {
        //将Invocation 序列化 转换成 byte[] 数组
        byte[] content = JSON.toJSONBytes(invocation);
        //写入length
        //后续 InvocationDecoder 可以根据该长度，解析到消息，解决粘包和拆包的问题。
        byteBuf.writeInt(content.length);
        //写入内容
        byteBuf.writeBytes(content);
        logger.info("[encode][连接({}) 编码了一条消息({})]", channelHandlerContext.channel().id(), invocation.toString());
    }
}
