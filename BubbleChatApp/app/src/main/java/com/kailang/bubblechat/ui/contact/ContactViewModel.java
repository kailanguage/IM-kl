package com.kailang.bubblechat.ui.contact;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kailang.bubblechat.network.client.MsgDataSource;
import com.kailang.bubblechat.network.codec.ChatMessage;

public class ContactViewModel extends ViewModel {
    private MsgDataSource msgDataSource;
    public ContactViewModel() {
        msgDataSource= MsgDataSource.getInstance();
    }
    public MutableLiveData getGroupsData() {
        return msgDataSource.getGroupsData();
    }
    public MutableLiveData getUsersData() {
        return msgDataSource.getUsersData();
    }
    public void createGroup(ChatMessage.MyMsg msg){
        msgDataSource.sendMsg(msg);
    }
}