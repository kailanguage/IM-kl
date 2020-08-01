package com.kailang.bubblechat.util;

import com.kailang.bubblechat.codec.ChatMessage;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class SessionUtil {

    private static final Map<Integer, ChatMessage.User> userIdMap = new ConcurrentHashMap<>();
    private static final Map<Integer, ChatMessage.Group> groupIdMap = new ConcurrentHashMap<>();
    private static final Map<Integer, Channel> userChannelMap = new ConcurrentHashMap<>();
    private static final Map<Integer, ChannelGroup> groupChannelGroupMap = new ConcurrentHashMap<>();
    private static final Set<ChatMessage.User> userSet=new CopyOnWriteArraySet<>();

    /*

    维护用户映射关系
     */
    public static void userAdd(int userID, ChatMessage.User user) {
        userSet.add(user);
        userIdMap.put(userID, user);
    }

    public static boolean hasUser(int userID) {
        return userIdMap.get(userID) != null;
    }
    public static String getUserName(int userID){
        return userIdMap.get(userID).getUserName();
    }
    public static void userRemove(int userID) {
        if (hasUser(userID))
            userIdMap.remove(userID);
    }
    public static List<ChatMessage.User> getAllUser(){
        List userList=new ArrayList();
        for(int k:userIdMap.keySet()){
            userList.add(userIdMap.get(k));
           // System.out.println("userListAdd"+k);
        }
        return userList;
    }
    /*
    维护群聊映射关系
     */
    public static void groupAdd(int groupID, ChatMessage.Group group) {
        groupIdMap.put(groupID, group);
    }
    public static boolean hasGroup(int groupID) {
        return groupIdMap.get(groupID) != null;
    }
    public static String getGroupName(int groupID){
        return groupIdMap.get(groupID).getGroupName();
    }
    public static void groupRemove(int groupID) {
        if (hasGroup(groupID))
            groupIdMap.remove(groupID);
    }
    public static List<ChatMessage.Group> getAllGroup(){
        List groupList=new ArrayList();
        for(int k: groupIdMap.keySet()){
            groupList.add(groupIdMap.get(k));
        }
        return groupList;
    }

    /*
    维护用户与channel的关系
     */
    public static void userBindChannel(int userID, Channel channel) {
        userChannelMap.put(userID, channel);
    }

    public static boolean hasUserChannel(int userID) {
        return userChannelMap.get(userID) != null;
    }

    public static Channel getUserChannel(int userID) {
        return userChannelMap.get(userID);
    }

    public static void userUnbindChannel(int userID) {
        if (hasUserChannel(userID))
            userChannelMap.remove(userID);
    }
    public static ChatMessage.User userUnbindChannel(Channel channel) {
        ChatMessage.User user = null;
        for(int k:userChannelMap.keySet())
            if(userChannelMap.get(k)==channel){
                user=userIdMap.get(k);
                userChannelMap.remove(k);
                userIdMap.remove(k);
                break;
            }
        return user;
    }


    /*
    维护群聊与channel的关系
     */
    public static boolean hasGroupChannelGroup(int groupID) {
        return groupChannelGroupMap.get(groupID) != null;
    }

    public static void groupUnbindChannelGroup(int groupID) {
        if (hasGroupChannelGroup(groupID)) {
            groupChannelGroupMap.remove(groupID);
            groupIdMap.remove(groupID);
        }
    }

    public static void groupBindChannelGroup(int groupID, ChannelGroup channelGroup) {
        groupChannelGroupMap.put(groupID, channelGroup);
    }

    public static ChannelGroup getGroupChannelGroup(int groupID) {
        return groupChannelGroupMap.get(groupID);
    }
}
