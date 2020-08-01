package com.kailang.bubblechat.ui.chat;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kailang.bubblechat.network.client.MsgDataSource;
import com.kailang.bubblechat.network.codec.ChatMessage;

import java.util.List;

public class ChatViewModel extends ViewModel {
    private MsgDataSource msgDataSource;

    public ChatViewModel() {
        this.msgDataSource =MsgDataSource.getInstance();
    }
    public MutableLiveData getPrivateChatMsg(){
        return msgDataSource.getPrivateChatData();
    }
    public MutableLiveData getGroupChatMsg(){
        return msgDataSource.getGroupChatData();
    }
    public List<ChatMessage.PrivateChat> getPrivateChatMsg(int senderID,int receiverID ){
        return msgDataSource.getPrivateChatMsg(senderID,receiverID);
    }
    public void sendMsg(ChatMessage.MyMsg msg){
        msgDataSource.sendMsg(msg);
    }
    public List<ChatMessage.GroupChat> getGroupChatMsg(int groupID ){
        return msgDataSource.getGroupChatMsg(groupID);
    }
}