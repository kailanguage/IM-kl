package com.kailang.bubblechat.network.client;

import android.graphics.Bitmap;
import android.util.Log;

import com.kailang.bubblechat.network.codec.ChatMessage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static com.kailang.bubblechat.network.codec.ChatMessage.MyMsg.DataType.*;

public class MsgClientHandler extends SimpleChannelInboundHandler<ChatMessage.MyMsg> {
    public static int currentUserID;
    public static byte[] currentUserIcon;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    final MsgDataSource msgDataSource = MsgDataSource.getInstance();

    /*
    当通道就绪时
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        //模拟登录
//        int userID = new Random().nextInt(1000);
//        currentUserID = userID;
//        ChatMessage.MyMsg myMsg1 = ChatMessage.MyMsg.newBuilder()
//                .setDataType(ChatMessage.MyMsg.DataType.User)
//                .setUser(ChatMessage.User.newBuilder().setUserID(userID).setUserName(String.valueOf(userID)).build())
//                .build();
//        ctx.writeAndFlush(myMsg1);
    }

    /*
    当通道有读取事件时
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ChatMessage.MyMsg myMsg) throws Exception {
        Log.e("MsgClientHandler", "有数据来了");

        ChatMessage.MyMsg.DataType dataType = myMsg.getDataType();
        if (dataType == SysInfo) {//获取在线成员和群聊信息
            ChatMessage.SysInfo fromMsg = myMsg.getSysInfo();
            List<ChatMessage.User> users = new ArrayList<>();
            users = fromMsg.getUserListList();
            List<ChatMessage.Group> groups = new ArrayList<>();
            groups = fromMsg.getGroupListList();
            if (users != null && !users.isEmpty()) {
                msgDataSource.setUsersData(users);
                    Log.e("MsgClientHandler", "SysInfo user size: " + users.size() );
            }
            if (groups != null && !groups.isEmpty()) {
                msgDataSource.setGroupsData(groups);
                Log.e("MsgClientHandler", "SysInfo group size: " + groups.size());
            }
        } else if (dataType == PrivateChat) {//私聊
            ChatMessage.PrivateChat fromMsg = myMsg.getPrivateChat();
            msgDataSource.setPrivateChatData(fromMsg);
            Log.e("MsgClientHandler", "PrivateChat " + fromMsg.getMsg());
        } else if (dataType == GroupChat) {//群聊
            ChatMessage.GroupChat fromMsg = myMsg.getGroupChat();
            msgDataSource.setGroupChatData(fromMsg);
            Log.e("MsgClientHandler", "GroupChat " + fromMsg.getMsg());
        } else {
            Log.e("MsgClientHandler", "不支持的传输类型");
        }
    }
    /*
    与服务器断开连接
     */

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Log.e("MsgClientHandler", "与服务器断开连接");
    }

    /*
    异常处理
    */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
