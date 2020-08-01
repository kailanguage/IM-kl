package com.kailang.bubblechat.network.client;

import androidx.lifecycle.MutableLiveData;

import com.kailang.bubblechat.network.codec.ChatMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kailang.bubblechat.network.client.MsgClientHandler.currentUserID;
import static com.kailang.bubblechat.network.client.MsgClientHandler.currentUserIcon;

public class MsgDataSource {

    private NettyClient nettyClient;

    private MsgDataSource() {
        nettyClient = NettyClient.getInstance();
    }

    public static MsgDataSource getInstance() {
        return Inner.INSTANCE;
    }

    private static class Inner {
        private final static MsgDataSource INSTANCE = new MsgDataSource();
    }

    private MutableLiveData isOnline=new MutableLiveData<Boolean>();

    private MutableLiveData usersData = new MutableLiveData<List<ChatMessage.User>>();
    private MutableLiveData groupsData = new MutableLiveData<List<ChatMessage.Group>>();
    private MutableLiveData privateChatData = new MutableLiveData<ChatMessage.PrivateChat>();
    private MutableLiveData groupChatData = new MutableLiveData<ChatMessage.GroupChat>();
    private static Map<Integer, List<ChatMessage.GroupChat>> groupChatMsg = new HashMap<>();
    private static Map<Integer, List<ChatMessage.PrivateChat>> privateChatMsg = new HashMap<>();
    private static Map<Integer, ChatMessage.User> onlineUser = new HashMap<>();

    public MutableLiveData getGroupChatData() {
        return groupChatData;
    }

    public void setGroupChatData(ChatMessage.GroupChat groupChatData) {

        int id = groupChatData.getGroupID();
        List<ChatMessage.GroupChat> groupChats;
        if (groupChatMsg.get(id) != null)
            groupChats = groupChatMsg.get(id);
        else groupChats = new ArrayList<>();
        groupChats.add(groupChatData);
        groupChatMsg.put(id, groupChats);

        this.groupChatData.postValue(groupChatData);
    }

    public MutableLiveData getUsersData() {
        return usersData;
    }
    public void setUsersData(List<ChatMessage.User> usersData) {
        for(ChatMessage.User u:usersData){
            onlineUser.put(u.getUserID(),u);
        }
        this.usersData.postValue(usersData);
    }

    public MutableLiveData getGroupsData() {
        return groupsData;
    }

    public void setGroupsData(List<ChatMessage.Group> groupsData) {
        this.groupsData.postValue(groupsData);
    }

    public MutableLiveData getPrivateChatData() {
        return privateChatData;
    }

    public void setPrivateChatData(ChatMessage.PrivateChat privateChatData) {

        int id = privateChatData.getReceiverID() ^ privateChatData.getSenderID();
        List<ChatMessage.PrivateChat> privateChats;
        if (privateChatMsg.get(id) != null)
            privateChats = privateChatMsg.get(id);
        else privateChats = new ArrayList<>();
        privateChats.add(privateChatData);
        privateChatMsg.put(id, privateChats);
        this.privateChatData.postValue(privateChatData);
    }

    public static List<ChatMessage.GroupChat> getGroupChatMsg(int groupID) {
        return groupChatMsg.get(groupID);
    }

    public static List<ChatMessage.PrivateChat> getPrivateChatMsg(int senderID, int receiverID) {
        return privateChatMsg.get(senderID ^ receiverID);
    }
    public static ChatMessage.User getUserByID(int userID){
        return onlineUser.get(userID);
    }

    public void sendMsg(ChatMessage.MyMsg msg) {
        if (msg.hasPrivateChat())
            this.setPrivateChatData(msg.getPrivateChat());
        else if (msg.hasGroupChat())
            this.setGroupChatData(msg.getGroupChat());
        else if(msg.hasUser()) {
            currentUserID = msg.getUser().getUserID();
            currentUserIcon = msg.getUser().getUserIcon().toByteArray();
        }
        nettyClient.sendMsg(msg);
    }
}
