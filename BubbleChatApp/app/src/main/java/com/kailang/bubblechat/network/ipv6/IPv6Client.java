package com.kailang.bubblechat.network.ipv6;

import android.util.Log;

import com.kailang.bubblechat.network.client.MsgClientHandler;
import com.kailang.bubblechat.network.codec.ChatMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

public class IPv6Client {

    private static String HOST;
    private static int PORT;
    private Channel channel;

    private IPv6Client() {
        run();
    }

    public static IPv6Client getInstance(String host, int port) {
        HOST = host;
        PORT = port;
        return IPv6ClientSingleTon.INSTANCE;
    }

    private static class IPv6ClientSingleTon {
        private static final IPv6Client INSTANCE = new IPv6Client();
    }

    private void run() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        try {
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)// 设置客户端通道的实现类(反射)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new ProtobufDecoder(ChatMessage.MyMsg.getDefaultInstance()));
                            pipeline.addLast(new ProtobufEncoder());
                            pipeline.addLast(new MsgClientHandler());


                        }
                    });

            Log.e("IPv6 Client", "run...");
            ChannelFuture channelFuture = bootstrap.connect(HOST, PORT);

            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess()) {
                        Log.e("IPv6 Client", "连接成功 [" + HOST + ":" + PORT + "]");
                    } else {
                        Log.e("IPv6 Client", "连接失败 [" + HOST + ":" + PORT + "]");
                    }
                }
            });
            //得到channel
            this.channel = channelFuture.channel();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
    public void sendMsg(ChatMessage.MyMsg myMsg) {
        channel.writeAndFlush(myMsg);
    }
}
