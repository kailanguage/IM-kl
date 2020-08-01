package com.kailang.bubblechat.ui.contact;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.hb.dialog.myDialog.MyAlertInputDialog;
import com.kailang.bubblechat.R;
import com.kailang.bubblechat.adapter.ContactAdapter;
import com.kailang.bubblechat.network.codec.ChatMessage;

import java.util.List;
import java.util.Random;

import static com.kailang.bubblechat.network.client.MsgClientHandler.currentUserID;
import static com.kailang.bubblechat.network.codec.ChatMessage.MyMsg.DataType.GroupChatCreate;
import static com.kailang.bubblechat.network.codec.ChatMessage.MyMsg.DataType.PrivateChat;

public class GroupFragment extends Fragment {

    private ContactViewModel contactViewModel;
    private ContactAdapter adapter;
    private RecyclerView recyclerView;
    private LiveData<List<ChatMessage.Group>> groupsData;
    private FloatingActionButton groupCreateFab;
    private FloatingActionButton groupJoinFab;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        contactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);
        View root = inflater.inflate(R.layout.fragment_group, container, false);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        groupCreateFab = getActivity().findViewById(R.id.group_create_fab);
        groupJoinFab = getActivity().findViewById(R.id.group_join_fab);
    }

    private void initData() {
        recyclerView = getView().findViewById(R.id.rv_group_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        adapter = new ContactAdapter(getContext());
        recyclerView.setAdapter(adapter);

        groupsData = contactViewModel.getGroupsData();
        groupsData.observe(getViewLifecycleOwner(), new Observer<List<ChatMessage.Group>>() {
            @Override
            public void onChanged(List<ChatMessage.Group> groups) {
                adapter.updateGroup(groups);
            }
        });
        if(groupCreateFab!=null)
        groupCreateFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MyAlertInputDialog myAlertInputDialog = new MyAlertInputDialog(getContext()).builder()
                        .setTitle("请输入群名称")
                        .setEditText("");
                myAlertInputDialog.setPositiveButton("确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //showMsg(myAlertInputDialog.getResult());
                        String name = myAlertInputDialog.getResult().trim();
                        if (name.length() != 0) {
                            Random random = new Random();
                            ChatMessage.MyMsg toMsg = ChatMessage.MyMsg.newBuilder()
                                    .setDataType(GroupChatCreate)
                                    .setGroupCreate(ChatMessage.GroupChatCreate.newBuilder()
                                            .setCreatorID(currentUserID).setGroupName(name).setGroupID(random.nextInt(100000)).build())
                                    .build();
                            contactViewModel.createGroup(toMsg);
                            myAlertInputDialog.dismiss();
                        } else {
                            Toast.makeText(getContext(), "群名称不能为空!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // showMsg("取消");
                        myAlertInputDialog.dismiss();
                    }
                });
                myAlertInputDialog.show();
            }
        });
        if(groupJoinFab!=null)
        groupJoinFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "点击群聊即可加入群聊", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}