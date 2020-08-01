package com.kailang.bubblechat.server;

import com.kailang.bubblechat.codec.ChatMessage;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufEncoderNano;

public class NettyServer {
    private int port;

    public NettyServer(int port) {
        this.port = port;
    }

    public void run() throws InterruptedException {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
                    /*  option

                    ChannelOption.SO_BACKLOG用于构造服务端套接字ServerSocket对象，标识当服务器请求处理线程全满时，
                    用于临时存放已完成三次握手的请求的队列的最大长度

                    ChannelOption.SO_KEEPALIVE, true
                    是否启用心跳保活机制。在双方TCP套接字建立连接后（即都进入ESTABLISHED状态）
                    并且在两个小时左右上层没有任何数据传输的情况下，这套机制才会被激活

                    ChannelOption.TCP_NODELAY,true
                    true为关闭Nagle的算法，该算法的目的就是为了尽可能发送大块数据，避免网络中充斥着许多小数据块。
                     */
        try {
            bootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //.childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel nioSocketChannel) throws Exception {
                            ChannelPipeline pipeline = nioSocketChannel.pipeline();
                            //out
                            pipeline.addLast("encoder", new LengthFieldPrepender(3, false));
                            pipeline.addLast( new ProtobufEncoder());

                            //in
                            pipeline.addLast("decoder", new LengthFieldBasedFrameDecoder(100000, 0, 3, 0, 3));
                            pipeline.addLast( new ProtobufDecoder(ChatMessage.MyMsg.getDefaultInstance()));
                            pipeline.addLast(new MsgServerHandler());
                        }
                    });
            System.out.println("Netty Server start....");
            ChannelFuture channelFuture = bootstrap.bind(port).sync();//异步阻塞
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess()) {
                        System.out.println("监听端口成功");
                    } else {
                        System.out.println("监听端口失败");
                    }
                }
            });
            channelFuture.channel().closeFuture().sync();//监听关闭
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
