package com.kailang.bubblechat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.kailang.bubblechat.R;
import com.kailang.bubblechat.network.client.MsgDataSource;
import com.kailang.bubblechat.network.codec.ChatMessage;

import java.util.ArrayList;
import java.util.List;

import static com.kailang.bubblechat.network.client.MsgClientHandler.currentUserID;
import static com.kailang.bubblechat.network.client.MsgClientHandler.currentUserIcon;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_SEND_TEXT = 1;
    private static final int TYPE_RECEIVE_TEXT = 2;
    private boolean chatType = false;
    private final Context mContext;
    private List<ChatMessage.PrivateChat> privateChats;
    private List<ChatMessage.GroupChat> groupChats;
    private boolean isPrivateChat = false;

    public ChatAdapter(Context mContext) {
        this.mContext = mContext;
        this.privateChats = new ArrayList<>();
        groupChats = new ArrayList<>();
    }

    public void updatePrivateChat(List<ChatMessage.PrivateChat> privateChats) {
        this.isPrivateChat = true;
        this.privateChats = privateChats;
        notifyDataSetChanged();
    }

    public void updateGroupChat(List<ChatMessage.GroupChat> groupChats) {
        this.groupChats = groupChats;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_RECEIVE_TEXT:
                return new TextMsgViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_text_receive, parent, false));
            case TYPE_SEND_TEXT:
                return new TextMsgViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_text_send, parent, false));
            default:
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (isPrivateChat) {
            ChatMessage.PrivateChat privateChat = privateChats.get(position);
            int itemViewType = getItemViewType(position);
            if (itemViewType == TYPE_SEND_TEXT) {
                Glide.with(mContext).load(R.mipmap.ic_image)
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(((TextMsgViewHolder)holder).icon);
                ((TextMsgViewHolder) holder).msg.setText(privateChat.getMsg().trim());
            } else {
                ChatMessage.User user = MsgDataSource.getUserByID(privateChat.getSenderID());
                byte[] b=user.getUserIcon().toByteArray();
                Bitmap bitmap=BitmapFactory.decodeByteArray(b, 0, b.length);
                Glide.with(mContext).load(R.mipmap.ic_image)
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(((TextMsgViewHolder)holder).icon);
                ((TextMsgViewHolder) holder).msg.setText(privateChat.getMsg().trim());
            }
        } else {
            ChatMessage.GroupChat groupChat = groupChats.get(position);
            int itemViewType = getItemViewType(position);
            if (itemViewType == TYPE_SEND_TEXT) {
                ((TextMsgViewHolder) holder).msg.setText(groupChat.getMsg().trim());
            } else {
                ((TextMsgViewHolder) holder).msg.setText(groupChat.getMsg().trim());
            }
        }
    }

    @Override
    public int getItemCount() {
        return privateChats == null ? 0 : privateChats.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (privateChats.get(position).getSenderID() != currentUserID)
            return TYPE_RECEIVE_TEXT;
        else return TYPE_SEND_TEXT;
    }

    static class TextMsgViewHolder extends RecyclerView.ViewHolder {
        TextView time;
        ImageView icon;
        TextView msg;

        public TextMsgViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.item_tv_time);
            icon = itemView.findViewById(R.id.chat_item_header);
            msg = itemView.findViewById(R.id.chat_item_content_text);
        }
    }

//    static class TextMsgSendViewHolder extends RecyclerView.ViewHolder{
//        TextView time;
//        ImageView icon;
//        TextView msg;
//        public TextMsgSendViewHolder(@NonNull View itemView,int msgType) {
//            super(itemView);
//            time=itemView.findViewById(R.id.item_tv_time);
//            icon=itemView.findViewById(R.id.chat_item_header);//??send 和 receive重名？？有问题吗？
//            msg=itemView.findViewById(R.id.chat_item_content_text);
//        }
//    }
//    static class TextMsgReceiveViewHolder extends RecyclerView.ViewHolder{
//        TextView time;
//        ImageView icon;
//        TextView msg;
//        public TextMsgReceiveViewHolder(@NonNull View itemView,int msgType) {
//            super(itemView);
//            time=itemView.findViewById(R.id.item_tv_time);
//            icon=itemView.findViewById(R.id.chat_item_header);//??send 和 receive重名？？有问题吗？
//            msg=itemView.findViewById(R.id.chat_item_content_text);
//        }
//    }
}
