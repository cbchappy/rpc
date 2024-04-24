package cbc.protocol;

import cbc.message.Message;
import cbc.message.RequestMessage;
import cbc.message.Serializer;
import cbc.utils.ReadProperties;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

/**
 * @Author Cbc
 * @DateTime 2024/4/24 19:19
 * @Description 信息的编码与解码
 */
public class MessageCodec extends ByteToMessageCodec<Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message message, ByteBuf byteBuf) throws Exception {

        //先判断message的类型
        //message固定序列化为字节流
        if(message.messageType == 0) {
            //完善message的参数
            RequestMessage request = (RequestMessage) message;
            String type = ReadProperties.readProperties("serializeType");
            if (type != null && type.equals(Message.SERIALIZE_JAVA)) {
                request.setSerializeType(Message.SERIALIZE_JAVA);

            } else {
                request.setSerializeType(Message.SERIALIZE_JSON);
            }
        }
        Object serialize = Serializer.Algorithm.java.serialize(message);
        byteBuf.writeBytes((byte[]) serialize);

    }

    @Override
    protected void decode(ChannelHandlerContext cxt, ByteBuf byteBuf, List<Object> list) throws Exception {
        //获取字节长度
        int len = byteBuf.readInt();

        byte[] bytes = new byte[len];
        byteBuf.readBytes(bytes);

        //反序列化
        Message message = Serializer.Algorithm.java.deSerialize(Message.class, bytes);
        list.add(message);
    }
}
