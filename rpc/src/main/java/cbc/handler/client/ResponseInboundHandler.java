package cbc.handler.client;

import cbc.message.Message;
import cbc.message.ResponseMessage;
import cbc.message.Serializer;
import cbc.pojo.TestUsTwo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;

import java.util.Map;

/**
 * @Author Cbc
 * @DateTime 2024/4/25 21:31
 * @Description
 */
public class ResponseInboundHandler extends SimpleChannelInboundHandler<Message> {
    private Map<Integer, Promise<Object>> map;

    public void setMap(Map<Integer, Promise<Object>> map) {
        this.map = map;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message m) throws Exception {
        ResponseMessage msg = (ResponseMessage) m;
        //移除promise
        Promise<Object> promise = map.remove(msg.getId());
        Exception exception = msg.getException();

        //如果返回的消息有错误
        if(exception != null){
            promise.setFailure(exception);
            return;
        }
        //反序列化
        Object result = msg.getResult();
        Object o;
        if(msg.getSerializeType().equals(Message.SERIALIZE_JSON)){
           o = Serializer.Algorithm.json.deSerialize(msg.getResultType(), result);
        }else {
            o = Serializer.Algorithm.java.deSerialize(msg.getResultType(), result);
        }
        //设置promise为成功
        promise.setSuccess(o);
    }
}
