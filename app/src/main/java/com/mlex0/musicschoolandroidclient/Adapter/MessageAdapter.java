package com.mlex0.musicschoolandroidclient.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.mlex0.musicschoolandroidclient.Classes.Constants;
import com.mlex0.musicschoolandroidclient.Model.Chat;
import com.mlex0.musicschoolandroidclient.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT=0;
    public static final int MSG_TYPE_RIGHT=1;
    private Context mContext;
    private List<Chat> mChat;
    private String imageurl;


    public MessageAdapter(Context mContext, List<Chat> mChat,String imageurl){
        this.mContext=mContext;
        this.mChat=mChat;
        this.imageurl=imageurl;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Chat chat= mChat.get(position);

        holder.show_message.setText(chat.getMessage());

        if(imageurl.equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }
        else {
            String rightPath = imageurl.toString().replace("uploads\\", "");
            String lastPath = Constants.ApiUrl.replace("api/", "") + "uploads/" + rightPath;;

            GlideUrl glideUrl = new GlideUrl(lastPath, new LazyHeaders.Builder()
                    .addHeader("Authorization", "Bearer " + Constants.UserToken)
                    .build());

            Glide.with(mContext).load(glideUrl).into(holder.profile_image);
        }

        if(position==mChat.size()-1){
            if(chat.isIsseen()){
                holder.txt_seen.setText("Seen");
            }
            else{
                holder.txt_seen.setText("Delivered");
            }
        }
        else {
            holder.txt_seen.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView show_message;
        public ImageView profile_image;
        public TextView txt_seen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show_message=itemView.findViewById(R.id.show_message);
            profile_image=itemView.findViewById(R.id.profile_image);
            txt_seen=itemView.findViewById(R.id.txt_seen);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(mChat.get(position).getSender().equals(Constants.UserId)){
            return MSG_TYPE_RIGHT;
        }
        else
            return MSG_TYPE_LEFT;
    }
}
