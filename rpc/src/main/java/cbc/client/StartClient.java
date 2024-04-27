package cbc.client;

import cbc.handler.client.ResponseInboundHandler;
import cbc.message.RequestMessage;
import cbc.protocol.MessageCodec;
import cbc.protocol.ProtocolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Cbc
 * @DateTime 2024/4/25 21:22
 * @Description
 */
public class StartClient {
    private Channel channel;
    private Map<Integer, Promise<Object>> map;
    private NioEventLoopGroup work;

    public StartClient(String host, int port){
        //初始化map
        map = new HashMap<>();
        //初始化channel
        channel = getChannel(host, port);
    }

    /**
     * 初始化channel
     * @param host
     * @param port
     * @return
     */
    private  Channel getChannel(String host, int port) {
        LoggingHandler loggingHandler = new LoggingHandler();
        ResponseInboundHandler responseInboundHandler = new ResponseInboundHandler();
        responseInboundHandler.setMap(map);
        work = new NioEventLoopGroup(1);
        Bootstrap bootstrap = new Bootstrap()
                .group(work)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ProtocolFrameDecoder());//LTC解码
                        ch.pipeline().addLast(new MessageCodec());//数据的解码与编码
                        ch.pipeline().addLast(responseInboundHandler);//处理进来的请求
                        ch.pipeline().addLast(loggingHandler);//日志处理器
                    }
                });

        try {

            return bootstrap.connect(host, port).sync().channel();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 发送信息
     */
    public Object send(RequestMessage request){
        DefaultPromise<Object> promise = new DefaultPromise<>(channel.eventLoop());
        map.put(request.getId(), promise);
        channel.writeAndFlush(request);
        try {
            promise.await().sync();
            if(promise.isSuccess()){
                return promise.get();
            }
            throw promise.cause();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }finally {
            //关闭资源
            close();
        }

    }

    /**
     * 关闭channel
     */
    private void close(){
        channel.close();
        work.shutdownGracefully();
    }
}
