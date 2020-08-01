package com.kailang.bubblechat.ui.home;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kailang.bubblechat.network.client.MsgDataSource;

public class HomeViewModel extends ViewModel {

    private MsgDataSource msgDataSource;

    public HomeViewModel() {
       msgDataSource= MsgDataSource.getInstance();
    }
    public MutableLiveData getUsersData() {
        return msgDataSource.getUsersData();
    }
}