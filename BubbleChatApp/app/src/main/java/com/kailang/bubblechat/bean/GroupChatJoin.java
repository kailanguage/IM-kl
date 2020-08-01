package com.kailang.bubblechat.bean;

public class GroupChatJoin {
    private int userID;
    private int groupID;

    public GroupChatJoin(int userID, int groupID) {
        this.userID = userID;
        this.groupID = groupID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }
}
