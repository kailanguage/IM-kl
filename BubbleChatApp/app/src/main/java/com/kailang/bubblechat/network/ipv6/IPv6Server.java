package com.kailang.bubblechat.network.ipv6;

import android.util.Log;

import com.kailang.bubblechat.network.codec.ChatMessage;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

public class IPv6Server {
    private static String HOST;
    private static int PORT;


    public void run() throws InterruptedException {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();

        try {
            bootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel nioSocketChannel) throws Exception {
                            ChannelPipeline pipeline = nioSocketChannel.pipeline();
                            pipeline.addLast( new ProtobufDecoder(ChatMessage.MyMsg.getDefaultInstance()));
                            pipeline.addLast("encoder", new ProtobufEncoder());
                            pipeline.addLast(new MsgServerHandler());
                        }
                    });
            System.out.println("Netty Server start....");
            Log.e("IPv6 Server","start....");
            ChannelFuture channelFuture = bootstrap.bind(PORT).sync();//异步阻塞
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess()) {
                        Log.e("IPv6 Server", "绑定成功 [" + HOST + ":" + PORT + "]");
                    } else {
                        Log.e("IPv6 Server", "绑定失败 [" + HOST + ":" + PORT + "]");
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
