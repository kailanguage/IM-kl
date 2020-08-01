package com.kailang.bubblechat.bean;

public class GroupChatMsg {
    private int senderID;
    private int groupID;
    private String msg;

    public GroupChatMsg(int senderID, int groupID, String msg) {
        this.senderID = senderID;
        this.groupID = groupID;
        this.msg = msg;
    }

    public int getSenderID() {
        return senderID;
    }

    public void setSenderID(int senderID) {
        this.senderID = senderID;
    }

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
