package com.kailang.bubblechat.server;

import com.kailang.bubblechat.codec.ChatMessage;
import com.kailang.bubblechat.codec.ChatMessage.MyMsg.DataType;
import com.kailang.bubblechat.util.SessionUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.kailang.bubblechat.codec.ChatMessage.MyMsg.DataType.*;

public class MsgServerHandler extends SimpleChannelInboundHandler<ChatMessage.MyMsg> {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static ChannelGroup AllChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /*
    用户上线
    传输所有的在线用户信息和群聊信息
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        AllChannels.add(ctx.channel());
        //broadcastAll(ctx);
    }

    /*
    用户离线
    对在线用户广播更新后的用户列表
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ChatMessage.User removeUser = SessionUtil.userUnbindChannel(ctx.channel());//移除用户
        SessionUtil.userRemove(removeUser.getUserID());
        ChatMessage.MyMsg toMsg = ChatMessage.MyMsg.newBuilder()
                .setDataType(SysInfo)
                .setSysInfo(ChatMessage.SysInfo.newBuilder().addAllUserList(SessionUtil.getAllUser()).build())
                .build();
        AllChannels.writeAndFlush(toMsg);
        System.out.println(sdf.format(new Date()) + " 用户:" + removeUser.getUserID() + " 下线了");
    }


    /*
    接收数据请求
    根据接收的数据类型做相应的处理
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatMessage.MyMsg myMsg) throws Exception {
        DataType dataType = myMsg.getDataType();
        System.out.println("有数据来了");
        if (dataType == User) {//用户信息
            userLoginHandler(ctx, myMsg);
            broadcastAll(ctx);
        } else if (dataType == PrivateChat) {//私聊
            privateChatHandler(ctx, myMsg);
        } else if (dataType == GroupChat) {//群聊
            groupChatHandler(ctx, myMsg);
        } else if (dataType == GroupChatCreate) {//创建群聊
            groupCreateHandler(ctx, myMsg);
        } else if (dataType == GroupChatJoin) {//加入群聊
            groupJoinHandler(ctx, myMsg);
        } else if (dataType == GroupChatExit) {//退出群聊
            groupExitHandler(ctx, myMsg);
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
            if (!SessionUtil.hasUser(userID)) {
                SessionUtil.userAdd(userID, user);
                SessionUtil.userBindChannel(userID, ctx.channel());
                //通知上线
//                DefaultChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
//                ChatMessage.MyMsg toMsg = ChatMessage.MyMsg.newBuilder()
//                        .setDataType(SysInfo)
//                        .setSysInfo(ChatMessage.SysInfo.newBuilder().addAllUserList(SessionUtil.getAllUser()).addAllGroupList(SessionUtil.getAllGroup()).build())
//                        .build();
//                channels.writeAndFlush(toMsg);
                //broadcastAll(ctx);
            } else {//更新？????????????????
                SessionUtil.userBindChannel(userID, ctx.channel());
            }
            System.out.println(sdf.format(new Date()) + " 用户:" + user.getUserID() + " 上线了");
        }
    }

    private void privateChatHandler(ChannelHandlerContext ctx, ChatMessage.MyMsg myMsg) {
        ChatMessage.PrivateChat privateChat = myMsg.getPrivateChat();
        int receiverID = privateChat.getReceiverID();
        int senderID = privateChat.getSenderID();
        if (SessionUtil.hasUserChannel(receiverID)) {//转发私聊消息
            String fromMsg = privateChat.getMsg();
            //获取私聊对象的channel
            Channel channel = SessionUtil.getUserChannel(receiverID);

            ChatMessage.MyMsg toMsg = ChatMessage.MyMsg.newBuilder()
                    .setDataType(PrivateChat)
                    .setPrivateChat(ChatMessage.PrivateChat.newBuilder().setSenderID(senderID).setReceiverID(receiverID).setMsg(fromMsg).build())
                    .build();
            channel.writeAndFlush(toMsg);
            System.out.println(sdf.format(new Date()) + " " + senderID + " to " + receiverID + " : " + privateChat.getMsg());
        } else {
            System.out.println(sdf.format(new Date()) + " " + senderID + " to " + receiverID + " : 【发送失败】" + privateChat.getMsg());
        }
    }

    private void groupChatHandler(ChannelHandlerContext ctx, ChatMessage.MyMsg myMsg) {
        ChatMessage.GroupChat groupChat = myMsg.getGroupChat();
        int groupID = groupChat.getGroupID();
        int senderID = groupChat.getSenderID();

        if (SessionUtil.hasGroupChannelGroup(groupID)) {//转发群聊消息
            String fromMsg = groupChat.getMsg();
            ChannelGroup channels = SessionUtil.getGroupChannelGroup(groupID);
            ChatMessage.MyMsg toMsg = ChatMessage.MyMsg.newBuilder()
                    .setDataType(GroupChat)
                    .setGroupChat(ChatMessage.GroupChat.newBuilder().setSenderID(senderID).setGroupID(groupID).setMsg(fromMsg).build())
                    .build();
            channels.writeAndFlush(toMsg);
        } else {
            System.out.println(sdf.format(new Date()) + " " + senderID + " to  group:" + groupID + " : 【发送失败】" + groupChat.getMsg());
        }
    }

    private void groupCreateHandler(ChannelHandlerContext ctx, ChatMessage.MyMsg myMsg) {
        ChatMessage.GroupChatCreate groupChatCreate = myMsg.getGroupCreate();
        int groupID = groupChatCreate.getGroupID();
        int userID = groupChatCreate.getCreatorID();
        if (SessionUtil.hasUser(userID) && !SessionUtil.hasGroupChannelGroup(groupID)) {
            ChannelGroup channelGroup = new DefaultChannelGroup(ctx.executor());
            //把创建者拉入群聊
            channelGroup.add(ctx.channel());

            ChatMessage.Group group = ChatMessage.Group.newBuilder()
                    .setGroupID(groupID).setGroupName(groupChatCreate.getGroupName()).setCreatorID(userID)
                    .build();
            SessionUtil.groupAdd(groupID, group);

            SessionUtil.groupBindChannelGroup(groupID, channelGroup);

            broadcastAll(ctx);

            System.out.println(sdf.format(new Date()) + " " + "UserID:" + userID + " 创建群聊成功：groupID:" + groupID);
        } else {
            System.out.println(sdf.format(new Date()) + " " + "UserID:" + userID + " 创建群聊失败：groupID:" + groupID);
        }
    }

    private void groupJoinHandler(ChannelHandlerContext ctx, ChatMessage.MyMsg myMsg) {
        ChatMessage.GroupChatJoin groupJoin = myMsg.getGroupJoin();
        int userID = groupJoin.getUserID();
        int groupID = groupJoin.getGroupID();
        if (SessionUtil.hasUser(userID) && SessionUtil.hasGroupChannelGroup(groupID)) {
            ChannelGroup channels = SessionUtil.getGroupChannelGroup(groupID);
            channels.add(ctx.channel());
            //SessionUtil.groupBindChannelGroup(groupID,channels);
            //组员加入,发送通知
            String fromMsg = "欢迎 " + SessionUtil.getUserName(userID) + " 加入群聊";
            ChatMessage.MyMsg toMsg = ChatMessage.MyMsg.newBuilder()
                    .setDataType(GroupChat)
                    .setGroupChat(ChatMessage.GroupChat.newBuilder().setSenderID(groupID).setGroupID(groupID).setMsg(fromMsg).build())
                    .build();

            channels.writeAndFlush(toMsg);
        } else {
            System.out.println(sdf.format(new Date()) + " " + "UserID:" + userID + " 加入群聊失败：groupID:" + groupID);
        }
    }

    private void groupExitHandler(ChannelHandlerContext ctx, ChatMessage.MyMsg myMsg) {
        ChatMessage.GroupChatExit groupExit = myMsg.getGroupExit();
        int userID = groupExit.getUserID();
        int groupID = groupExit.getGroupID();
        if (SessionUtil.hasUser(userID) && SessionUtil.hasGroupChannelGroup(groupID)) {
            ChannelGroup channels = SessionUtil.getGroupChannelGroup(groupID);
            channels.remove(ctx.channel());
            if (channels.size() == 0) {//群员个数为0时,解散群聊
                SessionUtil.groupUnbindChannelGroup(groupID);
            } else {
                String fromMsg = SessionUtil.getUserName(userID) + " 退出群聊";
                ChatMessage.MyMsg toMsg = ChatMessage.MyMsg.newBuilder()
                        .setDataType(GroupChat)
                        .setGroupChat(ChatMessage.GroupChat.newBuilder().setSenderID(groupID).setGroupID(groupID).setMsg(fromMsg).build())
                        .build();
                channels.writeAndFlush(toMsg);
            }
        } else {
            System.out.println(sdf.format(new Date()) + " " + "UserID:" + userID + " 加入群聊失败：groupID:" + groupID);
        }
    }

    private void broadcastAll(ChannelHandlerContext ctx) {

        ctx.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                List<ChatMessage.User> users = SessionUtil.getAllUser();
                List<ChatMessage.Group> groups = SessionUtil.getAllGroup();
                System.out.println("在线人数: "+users.size());
                System.out.println("群聊个数: "+groups.size());
//                for (int i = 0; i <users.size() ; i++) {
//                    System.out.print(users.get(i).getUserID()+"\t");
//                }
                //System.out.println();
                ChatMessage.MyMsg toMsg = ChatMessage.MyMsg.newBuilder()
                        .setDataType(SysInfo)
                        .setSysInfo(ChatMessage.SysInfo.newBuilder().addAllUserList(users).addAllGroupList(groups).build())
                        .build();
                ChatMessage.SysInfo sysInfo1 = toMsg.getSysInfo();
                //List<ChatMessage.User> userListList = sysInfo1.getUserListList();
                //System.out.println(userListList.size());
//                for(int i=0;i<userListList.size();i++){
//                    System.out.print(userListList.get(i).getUserID()+"\t");
//                }
                System.out.println(sysInfo1.getUserListCount()+" "+sysInfo1.getGroupListCount());
                AllChannels.writeAndFlush(toMsg);
            }
        });
    }
}
