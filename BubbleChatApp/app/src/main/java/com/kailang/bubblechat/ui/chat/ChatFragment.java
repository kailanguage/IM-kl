package com.kailang.bubblechat.ui.chat;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kailang.bubblechat.R;
import com.kailang.bubblechat.adapter.ChatAdapter;
import com.kailang.bubblechat.network.codec.ChatMessage;


import java.util.List;
import java.util.Map;

import static com.kailang.bubblechat.network.client.MsgClientHandler.currentUserID;
import static com.kailang.bubblechat.network.codec.ChatMessage.MyMsg.DataType.GroupChat;
import static com.kailang.bubblechat.network.codec.ChatMessage.MyMsg.DataType.PrivateChat;

public class ChatFragment extends Fragment {

    private ChatViewModel chatViewModel;
    private ChatAdapter adapter;
    private RecyclerView recyclerView;
    private Integer receiverID;
    private String chatName;
    private EditText sendMsg;
    private Button sendBt;
    private boolean isPrivateChat=false;
    private RelativeLayout backBt;
    private InputMethodManager mInputManager;
    private SwipeRefreshLayout swipeRefreshLayout;

    public ChatFragment() {
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getActivity().findViewById(R.id.nav_view).setVisibility(View.GONE);
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String userName = getArguments().getString("userName");
        String groupName = getArguments().getString("groupName");
        if (userName != null) {
            isPrivateChat=true;
            receiverID = getArguments().getInt("userID");
            chatName = userName;
        } else {
            receiverID = getArguments().getInt("groupID");
            chatName = groupName;
        }
        sendMsg = getActivity().findViewById(R.id.et_content);
        sendBt = getActivity().findViewById(R.id.btn_send);
        backBt = getActivity().findViewById(R.id.common_toolbar_back);

        swipeRefreshLayout=getActivity().findViewById(R.id.swipe_chat);
        mInputManager= (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        swipeRefreshLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击空白隐藏输入法
                mInputManager.hideSoftInputFromWindow(sendMsg.getWindowToken(), 0);
            }
        });

        backBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击空白隐藏输入法
                mInputManager.hideSoftInputFromWindow(sendMsg.getWindowToken(), 0);

                Navigation.findNavController(getView()).navigate(R.id.action_chatFragment_to_navigation_home);
                getActivity().findViewById(R.id.nav_view).setVisibility(View.VISIBLE);
            }
        });

        if (chatName != null && receiverID != null) {
            Log.e("ChatFragment", chatName + " " + receiverID);
            TextView tvChatName = getActivity().findViewById(R.id.common_toolbar_title);
            tvChatName.setText(chatName);
        }


        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        recyclerView = getView().findViewById(R.id.rv_chat_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChatAdapter(getContext());
        recyclerView.setAdapter(adapter);

        if(isPrivateChat) {
            chatViewModel.getPrivateChatMsg().observe(getViewLifecycleOwner(), new Observer() {
                @Override
                public void onChanged(Object o) {
                    adapter.updatePrivateChat(chatViewModel.getPrivateChatMsg(currentUserID, receiverID));
                }
            });
        }else {
            chatViewModel.getGroupChatMsg().observe(getViewLifecycleOwner(), new Observer() {
                @Override
                public void onChanged(Object o) {
                    adapter.updateGroupChat(chatViewModel.getGroupChatMsg(receiverID));
                }
            });
        }
        sendBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = sendMsg.getText().toString();
                if (msg != null&&!msg.isEmpty()) {
                    if(isPrivateChat) {
                        ChatMessage.MyMsg toMsg = ChatMessage.MyMsg.newBuilder()
                                .setDataType(PrivateChat)
                                .setPrivateChat(ChatMessage.PrivateChat.newBuilder().setSenderID(currentUserID).setReceiverID(receiverID).setMsg(msg).build())
                                .build();
                        chatViewModel.sendMsg(toMsg);
                    }else {
                        ChatMessage.MyMsg toMsg = ChatMessage.MyMsg.newBuilder()
                                .setDataType(GroupChat)
                                .setGroupChat(ChatMessage.GroupChat.newBuilder().setSenderID(currentUserID).setGroupID(receiverID).setMsg(msg).build())
                                .build();
                        chatViewModel.sendMsg(toMsg);
                    }
                    sendMsg.setText("");
                }
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().findViewById(R.id.nav_view).setVisibility(View.VISIBLE);
    }
}