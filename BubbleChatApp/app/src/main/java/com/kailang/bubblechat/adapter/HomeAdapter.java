package com.kailang.bubblechat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.hb.dialog.myDialog.MyAlertDialog;
import com.kailang.bubblechat.R;
import com.kailang.bubblechat.network.codec.ChatMessage;

import java.util.List;

import static com.kailang.bubblechat.network.client.MsgClientHandler.currentUserID;

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_GROUP = 1;
    private static final int TYPE_FRIEND = 2;
    private Context mContext;
    private List<ChatMessage.User> users;
    private List<ChatMessage.Group> groups;

    public HomeAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void updateUser(List<ChatMessage.User> users) {

        this.users=users;
        notifyDataSetChanged();
    }
    public void updateGroup(List<ChatMessage.Group> groups) {
        this.groups=groups;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_GROUP) {
            return new GroupItemHolder(LayoutInflater.from(mContext).inflate(R.layout.group_item, parent, false));
        } else if (viewType == TYPE_FRIEND) {
            return new FriendItemHolder(LayoutInflater.from(mContext).inflate(R.layout.friend_item, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        int type = holder.getItemViewType();
        if (type == TYPE_FRIEND) {
            final ChatMessage.User user = users.get(position);
            byte[] b=user.getUserIcon().toByteArray();
            Bitmap bitmap= BitmapFactory.decodeByteArray(b, 0, b.length);
            Glide.with(mContext).load(R.mipmap.ic_image)
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(((FriendItemHolder)holder).headIcon);
            if (user.getUserName() != null)
                ((FriendItemHolder)holder).name.setText(user.getUserName());
            //点击
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("userName", user.getUserName());
                    bundle.putInt("userID", user.getUserID());
                    Navigation.findNavController(v).navigate(R.id.action_navigation_home_to_chatFragment, bundle);
                }
            });
        } else if (type == TYPE_GROUP) {
            final ChatMessage.Group group = groups.get(position);
            Glide.with(mContext).load(R.mipmap.ic_image)
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(((GroupItemHolder)holder).headIcon);
            if (group.getGroupName() != null)
                ((GroupItemHolder)holder).name.setText(group.getGroupName());
            //点击
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(group.getCreatorID()!=currentUserID) {
                        MyAlertDialog myAlertDialog = new MyAlertDialog(mContext).builder()
                                .setTitle("确认加入该群聊吗？")
                                .setPositiveButton("确认", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //showMsg("确认");
                                        Bundle bundle = new Bundle();
                                        bundle.putString("groupName", group.getGroupName());
                                        bundle.putInt("groupID", group.getGroupID());
                                        Navigation.findNavController(holder.itemView).navigate(R.id.action_navigation_contact_to_chatFragment, bundle);
                                    }
                                }).setNegativeButton("取消", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //showMsg("取消");
                                    }
                                });
                        myAlertDialog.show();
                    }else {
                        Bundle bundle = new Bundle();
                        bundle.putString("groupName", group.getGroupName());
                        bundle.putInt("groupID", group.getGroupID());
                        Navigation.findNavController(holder.itemView).navigate(R.id.action_navigation_contact_to_chatFragment, bundle);
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (groups != null) return TYPE_GROUP;
        else if (users != null) return TYPE_FRIEND;
        return 0;
    }

    @Override
    public int getItemCount() {
        if (users != null) return users.size();
        else if (groups != null) return groups.size();
        else return 0;
    }

    static class FriendItemHolder extends RecyclerView.ViewHolder {
        ImageView headIcon;
        TextView name, ipv6;

        public FriendItemHolder(View itemView) {
            super(itemView);
            headIcon = itemView.findViewById(R.id.friend_iv_head);
            name = itemView.findViewById(R.id.friend_name);
            ipv6 = itemView.findViewById(R.id.friend_is_ipv6);
        }
    }

    static class GroupItemHolder extends RecyclerView.ViewHolder {
        ImageView headIcon;
        TextView name, number;

        public GroupItemHolder(@NonNull View itemView) {
            super(itemView);
            headIcon = itemView.findViewById(R.id.group_iv_head);
            name = itemView.findViewById(R.id.group_name);
            number = itemView.findViewById(R.id.group_number);
        }
    }
}
