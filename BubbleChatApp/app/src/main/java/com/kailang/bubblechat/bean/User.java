package com.kailang.bubblechat.bean;

public class User {
    int userID;
    String userName;
    String userIcon;
    String listenAddrIPv6;
    int listenPortIPv6;

    public User(int userID, String userName, String userIcon, String listenAddrIPv6, int listenPortIPv6) {
        this.userID = userID;
        this.userName = userName;
        this.userIcon = userIcon;
        this.listenAddrIPv6 = listenAddrIPv6;
        this.listenPortIPv6 = listenPortIPv6;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public String getListenAddrIPv6() {
        return listenAddrIPv6;
    }

    public void setListenAddrIPv6(String listenAddrIPv6) {
        this.listenAddrIPv6 = listenAddrIPv6;
    }

    public int getListenPortIPv6() {
        return listenPortIPv6;
    }

    public void setListenPortIPv6(int listenPortIPv6) {
        this.listenPortIPv6 = listenPortIPv6;
    }
}
