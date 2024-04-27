package cbc.server;

import cbc.exception.ServerException;
import cbc.handler.server.RequestInboundHandler;
import cbc.protocol.MessageCodec;
import cbc.protocol.ProtocolFrameDecoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.Data;

/**
 * @Author Cbc
 * @DateTime 2024/4/23 22:23
 * @Description
 */

public class StartServer {
    public Channel getChannel() {
        return channel;
    }

    private final Channel channel;
    private final NioEventLoopGroup boss;
    private final NioEventLoopGroup worker;

    public StartServer(int port) {
        LoggingHandler loggingHandler = new LoggingHandler();
        //先开启服务再进行注册
        //防止延迟找不到
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        boss = new NioEventLoopGroup(1);
        worker = new NioEventLoopGroup();

        serverBootstrap
                .group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ProtocolFrameDecoder());//LTC解码
                        ch.pipeline().addLast(new MessageCodec());
                        ch.pipeline().addLast(new RequestInboundHandler());//处理进来的请求
                        ch.pipeline().addLast(loggingHandler);//日志处理器

                    }
                });
        channel = serverBootstrap.bind(port).channel();

    }

    public void closeServer() {
        if (channel == null) {
            return;
        }
        //关闭通道
        channel.close();
        //关闭线程池
        boss.shutdownGracefully();
        worker.shutdownGracefully();

    }
}
