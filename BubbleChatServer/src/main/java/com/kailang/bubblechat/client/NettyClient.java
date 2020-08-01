package com.kailang.bubblechat.client;

import com.kailang.bubblechat.codec.ChatMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

import java.util.Random;
import java.util.Scanner;

/*
模拟客户端连接
 */

public class NettyClient {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        try {
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)//// 设置客户端通道的实现类(反射)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //out
                            pipeline.addLast("encoder", new LengthFieldPrepender(3, false));
                            pipeline.addLast( new ProtobufEncoder());

                            //in
                            pipeline.addLast("decoder", new LengthFieldBasedFrameDecoder(100000, 0, 3, 0, 3));
                            pipeline.addLast(new ProtobufDecoder(ChatMessage.MyMsg.getDefaultInstance()));
                            pipeline.addLast(new MsgClientHandler());


                        }
                    });
            System.out.println("Client running...");
            ChannelFuture channelFuture = bootstrap.connect("192.168.2.104", 8888);

            //得到channel
            Channel channel = channelFuture.channel();

            //客户端需要输入信息，创建一个扫描器
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                int senderID=scanner.nextInt();
                int receiverID=scanner.nextInt();
                scanner.nextLine();
                String msg = scanner.nextLine();
                System.out.println(msg);
                //通过channel 发送到服务器端
                ChatMessage.MyMsg myMsg2 = ChatMessage.MyMsg.newBuilder()
                        .setDataType(ChatMessage.MyMsg.DataType.PrivateChat)
                        .setPrivateChat(ChatMessage.PrivateChat.newBuilder().setReceiverID(receiverID).setSenderID(senderID).setMsg(msg).build())
                        .build();
                channel.writeAndFlush(myMsg2);
            }
            channelFuture.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
