package com.kailang.bubblechat.client;

import com.kailang.bubblechat.codec.ChatMessage;
import com.kailang.bubblechat.codec.ChatMessage.MyMsg.DataType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;

import static com.kailang.bubblechat.codec.ChatMessage.MyMsg.DataType.*;

public class MsgClientHandler extends SimpleChannelInboundHandler<ChatMessage.MyMsg> {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /*
    当通道就绪时
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        //模拟登录
        int userID = new Random().nextInt(1000);
        ChatMessage.MyMsg myMsg1 = ChatMessage.MyMsg.newBuilder()
                .setDataType(DataType.User)
                .setUser(ChatMessage.User.newBuilder().setUserID(userID).setUserName(String.valueOf(userID)).build())
                .build();
        ctx.writeAndFlush(myMsg1);
    }

    /*
    当通道有读取事件时
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ChatMessage.MyMsg myMsg) throws Exception {
        DataType dataType = myMsg.getDataType();
        if (dataType == SysInfo) {//获取在线成员和群聊信息
            ChatMessage.SysInfo fromMsg = myMsg.getSysInfo();
            List<ChatMessage.User> users = fromMsg.getUserListList();
            List<ChatMessage.Group> groups = fromMsg.getGroupListList();
            if (users != null && !users.isEmpty()) {
                System.out.println("在线人数：" + users.size());
                for (ChatMessage.User u : users) {
                    System.out.print(u.getUserID()+"\t");
                }
                System.out.println();
            }
            if (groups != null && !groups.isEmpty()) {
                System.out.println("在线群聊：" + groups.size());
                for (ChatMessage.Group g : groups) {
                    System.out.print(g.getGroupID()+"\t");
                }
                System.out.println();
            }
        } else if (dataType == PrivateChat) {//私聊
            ChatMessage.PrivateChat fromMsg = myMsg.getPrivateChat();
            System.out.println(fromMsg.getSenderID() + " : " + fromMsg.getMsg());
        } else if (dataType == GroupChat) {//群聊
            ChatMessage.GroupChat fromMsg = myMsg.getGroupChat();
            System.out.println(fromMsg.getGroupID() + fromMsg.getMsg());
        } else {
            System.out.println("不支持的传输类型:" + myMsg.toByteArray());
        }
    }
    /*
    与服务器断开连接
     */

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("与服务器断开连接");
    }

    /*
        异常处理
         */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
