package cbc.handler.server;

import cbc.message.Message;
import cbc.message.RequestMessage;
import cbc.message.ResponseMessage;
import cbc.utils.SetProperties;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;

/**
 * @Author Cbc
 * @DateTime 2024/4/25 21:06
 * @Description
 */
public class RequestInboundHandler extends SimpleChannelInboundHandler<Message> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message m) throws Exception {
        //预先response
        ResponseMessage response = new ResponseMessage();

        try {
            RequestMessage msg = (RequestMessage) m;
            //解析request进行处理
            String interfaceName = msg.getInterfaceName();
            String methodName = msg.getMethodName();
            String implName = SetProperties.getProperty(interfaceName);
            Class<?> clazz = Class.forName(implName);
            Method method = clazz.getMethod(methodName, msg.getParameterTypes());
            Object res = method.invoke(clazz.newInstance(), msg.getParameterValues());

            //设置返回的数据
            response.setSerializeType(msg.getSerializeType());
            response.setResult(res);
            response.setId(msg.getId());
            response.setResultType(msg.getResultType());


        } catch (Exception e){
            response.setException((Exception) e.getCause());
        }finally {
            //发送response
            ctx.writeAndFlush(response);
        }
    }
}
