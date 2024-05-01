package cbc.client;

import cbc.handler.client.ResponseInboundHandler;
import cbc.message.RequestMessage;
import cbc.protocol.MessageCodec;
import cbc.protocol.ProtocolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author Cbc
 * @DateTime 2024/4/25 21:22
 * @Description
 */
@Slf4j
public class StartClient {
    private final String serverName;
    private Channel channel;
    private final Map<Integer, Promise<Object>> map;
    private NioEventLoopGroup work;
    private final String host;
    private final Integer port;
    public ReentrantLock lock;

    public StartClient(String host, int port, String serverName) {
        this.serverName = serverName;
        this.host = host;
        this.port = port;
        //初始化map
        map = new HashMap<>();
        //初始化channel
        channel = getChannel(host, port);
        lock = new ReentrantLock();

    }

    /**
     * 初始化channel
     *
     * @param host
     * @param port
     * @return
     */
    private  Channel getChannel(String host, int port) {
        LoggingHandler loggingHandler = new LoggingHandler();
        ResponseInboundHandler responseInboundHandler = new ResponseInboundHandler();
        responseInboundHandler.setMap(map);
        work = new NioEventLoopGroup(1);//初始化工作线程
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
                        ch.pipeline().addLast(new IdleStateHandler(0, 6, 0));//到期自动断开
                        ch.pipeline().addLast(new ChannelDuplexHandler() {
                            @Override
                            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                IdleStateEvent stateEvent = (IdleStateEvent) evt;
                                if (stateEvent.state() == IdleState.WRITER_IDLE && lock.tryLock()) {

                                    try {
                                        //移除实例
                                        StartInvoke.removeClient(serverName);
                                        //断开连接
                                        channel.close();
                                        channel = null;
                                        work.shutdownGracefully();
                                        log.info("客户端关闭时间:{}", new Date());
                                    } finally {
                                        lock.unlock();
                                    }
                                }
                            }
                        });//处理触发的未写事件

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
    public  Object send(RequestMessage request) {

           lock.lock();
            if(channel == null || !channel.isActive()){
                channel = getChannel(host, port);
            }
            DefaultPromise<Object> promise = new DefaultPromise<>(channel.eventLoop());
            map.put(request.getId(), promise);
            channel.writeAndFlush(request);

            try {
                promise.await().sync();
                if (promise.isSuccess()) {
                    return promise.get();
                }
                throw promise.cause();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }finally {
               lock.unlock();
            }
    }

}
