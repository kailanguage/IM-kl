package com.kailang.bubblechat.bean;

public class GroupChatCreat {
    private int groupID;
    private String groupName;
    private int creatorID;

    public GroupChatCreat(int groupID, String groupName, int creatorID) {
        this.groupID = groupID;
        this.groupName = groupName;
        this.creatorID = creatorID;
    }

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(int creatorID) {
        this.creatorID = creatorID;
    }
}
