package com.kailang.bubblechat.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.google.protobuf.ByteString;
import com.kailang.bubblechat.network.client.MsgDataSource;
import com.kailang.bubblechat.network.client.NettyClient;
import com.kailang.bubblechat.network.codec.ChatMessage;

public class ClientService extends Service {
    private MsgDataSource msgDataSource;

    public ClientService() {
        msgDataSource=MsgDataSource.getInstance();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        NettyClient.getInstance().startClient();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        String userName = null;
        byte[] icon;
        int userID;
        if (bundle != null) {
            userName = bundle.getString("userName");
            userID = bundle.getInt("userID");
            icon=bundle.getByteArray("userIcon");
            runClient(userID,userName,icon);
            Log.e("ClientService",userID+" "+userName+" "+icon.length);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void runClient(final int userID, final String userName, final byte[] icon) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ChatMessage.MyMsg loginMsg = ChatMessage.MyMsg.newBuilder()
                        .setDataType(ChatMessage.MyMsg.DataType.User)
                        .setUser(ChatMessage.User.newBuilder()
                                .setUserID(userID).setUserName(userName)
                                //.setUserIcon(ByteString.copyFrom(icon))
                                .build())
                        .build();
                msgDataSource.sendMsg(loginMsg);
                //NettyClient.getInstance().sendMsg(loginMsg);

                //runClient(times + 1);
            }
        }, 4000);
    }
}
