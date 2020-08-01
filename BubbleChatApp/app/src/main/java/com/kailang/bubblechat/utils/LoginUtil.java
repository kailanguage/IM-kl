package com.kailang.bubblechat.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class LoginUtil {
    public static int getUUID(String userName){
        Long timeStamp = System.currentTimeMillis();
        return Math.abs(timeStamp.hashCode()<<8^userName.hashCode());
    }
}
