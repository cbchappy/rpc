package cbc.protocol;

import cbc.message.Message;
import cbc.message.RequestMessage;
import cbc.message.ResponseMessage;
import cbc.message.Serializer;
import cbc.utils.SetProperties;
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
            String type = SetProperties.getProperty("serializeType");
            if (type != null && type.equals(Message.SERIALIZE_JAVA)) {
                request.setSerializeType(Message.SERIALIZE_JAVA);

            } else {
                request.setSerializeType(Message.SERIALIZE_JSON);
            }
        }else {
            //完善response
            //获取序列化方式
            ResponseMessage response = (ResponseMessage) message;
            String type = response.getSerializeType();
            Object result = response.getResult();
            Object serialize;

            //对内容进行序列化
            if(type.equals(Message.SERIALIZE_JSON)){
                serialize = Serializer.Algorithm.json.serialize(result);
            }else {
                serialize = Serializer.Algorithm.java.serialize(result);
            }
            response.setResult(serialize);
        }

        //进行LTC编码
        byte[] serialize = (byte[]) Serializer.Algorithm.java.serialize(message);
        byteBuf.writeInt(serialize.length);
        byteBuf.writeBytes(serialize);

    }

    @Override
    protected void decode(ChannelHandlerContext cxt, ByteBuf byteBuf, List<Object> list) throws Exception {
        //获取字节长度
        int len = byteBuf.readInt();

        byte[] bytes = new byte[len];
        byteBuf.readBytes(bytes);

        //反序列化
        Message message = Serializer.Algorithm.java.deSerialize(Message.class, bytes);
        /*//完善message
        if(message.messageType == 1){
            ResponseMessage response = (ResponseMessage) message;
            Object result = response.getResult();
            Object deSerialize;
            if(response.getSerializeType().equals(Message.SERIALIZE_JSON)){
                deSerialize = Serializer.Algorithm.json.deSerialize(response.getResultType(), result);
            }else {
                deSerialize = Serializer.Algorithm.java.deSerialize(response.getResultType(), result);
            }
            response.setResult(deSerialize);
        }*/

        list.add(message);
    }
}
