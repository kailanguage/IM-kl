package com.kailang.bubblechat.ui.person;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kailang.bubblechat.network.client.MsgDataSource;

public class PersonViewModel extends ViewModel {

    private MsgDataSource msgDataSource;

    public PersonViewModel() {
        msgDataSource=MsgDataSource.getInstance();
    }
}