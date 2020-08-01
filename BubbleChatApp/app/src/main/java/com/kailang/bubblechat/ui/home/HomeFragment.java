package com.kailang.bubblechat.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kailang.bubblechat.R;
import com.kailang.bubblechat.adapter.ContactAdapter;
import com.kailang.bubblechat.adapter.HomeAdapter;
import com.kailang.bubblechat.network.codec.ChatMessage;

import java.util.ArrayList;
import java.util.List;

import static com.kailang.bubblechat.network.client.MsgClientHandler.currentUserID;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private HomeAdapter adapter;
    private RecyclerView recyclerView;
    private LiveData<List<ChatMessage.User>> usersData;
    private List<ChatMessage.User> userList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel=new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerView=getView().findViewById(R.id.rv_home_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        adapter=new HomeAdapter(getContext());
        recyclerView.setAdapter(adapter);

        usersData=homeViewModel.getUsersData();
        usersData.observe(getViewLifecycleOwner(), new Observer<List<ChatMessage.User>>() {
            @Override
            public void onChanged(List<ChatMessage.User> users) {
                //将自己排除
                userList=new ArrayList<>(users);
                for (int i = 0; i <userList.size() ; i++) {
                    if(userList.get(i).getUserID()==currentUserID){
                        userList.remove(i);
                        break;
                    }
                }
                adapter.updateUser(userList);
//                for (ChatMessage.User u:users) {
//                    Log.e("HomeFragment user",u.getUserName());
//                }
            }
        });
    }
}