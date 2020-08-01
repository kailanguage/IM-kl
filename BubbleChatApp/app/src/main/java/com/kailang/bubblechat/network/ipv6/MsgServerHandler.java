package com.kailang.bubblechat.network.ipv6;

import com.kailang.bubblechat.network.codec.ChatMessage;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.kailang.bubblechat.network.codec.ChatMessage.MyMsg.DataType.PrivateChat;
import static com.kailang.bubblechat.network.codec.ChatMessage.MyMsg.DataType.User;


public class MsgServerHandler extends SimpleChannelInboundHandler<ChatMessage.MyMsg> {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /*
    用户上线
    传输所有的在线用户信息和群聊信息
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        ctx.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {

            }
        });

    }

    /*
    用户离线
    对在线用户广播更新后的用户列表
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

    }


    /*
    接收数据请求
    根据接收的数据类型做相应的处理
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatMessage.MyMsg myMsg) throws Exception {
        ChatMessage.MyMsg.DataType dataType = myMsg.getDataType();

        if (dataType == User) {//用户信息
            userLoginHandler(ctx, myMsg);
        } else if (dataType == PrivateChat) {//私聊
            privateChatHandler(ctx, myMsg);
        } else {//其他
            System.out.println("不支持的传输数据类型!");
        }
    }

    /*
    异常处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    /*
    以下方法为具体数据类型处理
     */
    private void userLoginHandler(ChannelHandlerContext ctx, ChatMessage.MyMsg myMsg) {
        ChatMessage.User user = myMsg.getUser();
        int userID = user.getUserID();
        String userName = user.getUserName();
        if (userName != null) {
            System.out.println(sdf.format(new Date()) + " 用户:" + user.getUserID() + " 上线了");
        }
    }

    private void privateChatHandler(ChannelHandlerContext ctx, ChatMessage.MyMsg myMsg) {
        ChatMessage.PrivateChat privateChat = myMsg.getPrivateChat();
        int receiverID = privateChat.getSenderID();
        int senderID = privateChat.getReceiverID();
        String fromMsg = privateChat.getMsg();

        Channel channel = ctx.channel();

        ChatMessage.MyMsg toMsg = ChatMessage.MyMsg.newBuilder()
                .setDataType(PrivateChat)
                .setPrivateChat(ChatMessage.PrivateChat.newBuilder().setSenderID(senderID).setReceiverID(receiverID).setMsg(fromMsg).build())
                .build();
        channel.writeAndFlush(toMsg);
        System.out.println(sdf.format(new Date()) + " " + senderID + " to " + receiverID + " : " + privateChat.getMsg());
    }
}
