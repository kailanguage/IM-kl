package com.kailang.bubblechat.network.client;

import android.os.AsyncTask;
import android.util.Log;

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
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

public class NettyClient {

    private final static String HOST = "192.168.2.104";
    private final static int PORT = 8888;
    private Channel channel;
    private volatile boolean isActive=false;

    private NettyClient() {
    }

    public static NettyClient getInstance() {
        return NettyClientSingleTon.INSTANCE;
    }

    private static class NettyClientSingleTon {
        private static final NettyClient INSTANCE = new NettyClient();
    }

    private void  run() {
        if(!isActive) {
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
                Log.e("NettyClient", "running...");
                ChannelFuture channelFuture = bootstrap.connect(HOST, PORT);
                channelFuture.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        if (channelFuture.isSuccess()) {
                            Log.e("NettyClient", "连接成功 [" + HOST + ":" + PORT + "]");
                        } else {
                            isActive=false;
                            Log.e("NettyClient", "连接失败 [" + HOST + ":" + PORT + "]");
                        }
                    }
                });
                //得到channel
                this.channel = channelFuture.channel();
                isActive=channel.isActive();
                channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                group.shutdownGracefully();
            }
        }
    }

    public void startClient(){
        new AsyncRun().execute();
    }

    public void sendMsg(ChatMessage.MyMsg myMsg) {
        channel.writeAndFlush(myMsg);
    }
    public boolean isConnectSuccess(){
        return isActive;
    }


    private static class AsyncRun extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            NettyClient.getInstance().run();
            return null;
        }
    }
}
