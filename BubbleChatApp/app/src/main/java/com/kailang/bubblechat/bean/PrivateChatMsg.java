package com.kailang.bubblechat.bean;

public class PrivateChatMsg {
    int senderID;
    int receiverID;
    String msg;

    public PrivateChatMsg(int senderID, int receiverID, String msg) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.msg = msg;
    }

    public int getSenderID() {
        return senderID;
    }

    public void setSenderID(int senderID) {
        this.senderID = senderID;
    }

    public int getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(int receiverID) {
        this.receiverID = receiverID;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
