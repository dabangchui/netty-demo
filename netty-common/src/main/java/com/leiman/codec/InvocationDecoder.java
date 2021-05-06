package com.leiman.codec;

import com.leiman.protobuf.MessageAll;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName : InvocationDecoder
 * @Description : 解码器 实现从 TCP Socket 读取字节数组，反序列化成 Invocation
 * @Author :
 * @Date: 2021-04-07 16:30
 */
public class InvocationDecoder extends ByteToMessageDecoder {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        /*// 标记当前读取位置
        byteBuf.markReaderIndex();
        //判断是否能够读取 length 长度
        if (byteBuf.readableBytes() <= BigDecimal.ROUND_CEILING){
            return;
        }
        // 读取长度
        int length = byteBuf.readInt();
        if (length < 0){
            throw new CorruptedFrameException("negative length: " + length);
        }

        // 如果 message 不够可读,则退回到原读取位置
        if (byteBuf.readableBytes() < length){
            byteBuf.resetReaderIndex();
            return;
        }*/

        //该消息包超过10KB,过滤该消息包,防止socket字节流攻击
        if (byteBuf.readableBytes() > 10 * 1024) {
            String remoteAddress = ctx.channel().remoteAddress().toString().substring(1);
            byteBuf.skipBytes(byteBuf.readableBytes());
            logger.error(String.format("({})非法请求,该次消息包超过10KB,关闭连接.", remoteAddress));
            return;
        }
        // 读取内容
        byte[] content = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(content);

        MessageAll.Message message = MessageAll.Message.parseFrom(content);
        int protoId = message.getPId().getNumber();
        Invocation packet = new Invocation(ctx.channel(),protoId,message.getDataContent().toByteArray()
                ,ctx.channel().remoteAddress().toString(),System.currentTimeMillis());
        list.add(packet);
    }
}
