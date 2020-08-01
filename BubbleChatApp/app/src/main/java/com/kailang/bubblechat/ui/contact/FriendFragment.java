package com.kailang.bubblechat.ui.contact;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kailang.bubblechat.R;
import com.kailang.bubblechat.adapter.ContactAdapter;
import com.kailang.bubblechat.network.codec.ChatMessage;

import java.util.List;

public class FriendFragment extends Fragment {

    private ContactViewModel contactViewModel;
    private ContactAdapter adapter;
    private RecyclerView recyclerView;
    private LiveData<List<ChatMessage.User>> usersData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        contactViewModel=new ViewModelProvider(this).get(ContactViewModel.class);

        return inflater.inflate(R.layout.fragment_friend, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerView=getView().findViewById(R.id.rv_friend_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter=new ContactAdapter(getContext());
        recyclerView.setAdapter(adapter);

        usersData=contactViewModel.getUsersData();
        usersData.observe(getViewLifecycleOwner(), new Observer<List<ChatMessage.User>>() {
            @Override
            public void onChanged(List<ChatMessage.User> users) {
                adapter.updateUser(users);
//                for (ChatMessage.User u:users) {
//                    Log.e("HomeFragment user",u.getUserName());
//                }
            }
        });
    }
}