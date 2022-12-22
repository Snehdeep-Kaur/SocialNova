package com.example.socialnova;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ChatAdapter2 extends RecyclerView.Adapter{

    ArrayList<MessageModel> messageModels;
    Context context;

    int SENDER_VIEW_TYPE=1;
    int RECIEVER_VIEW_TYPE=2;
    private SharedPreferences pref;
    private static final String FILE_NAME = "LoginActivity";
    private String PARAM_ONE = "userLogState";
    private String PARAM_TWO = "profileId";
    String log,uid;

    public ChatAdapter2(ArrayList<MessageModel> messageModels, Context context) {
        this.messageModels = messageModels;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==SENDER_VIEW_TYPE){
            View view = LayoutInflater.from(context).inflate(R.layout.sender_message_layout, parent, false);
            return new senderViewHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.greciever_message_layout, parent, false);
            return new recieverViewHolder(view);
        }
        //return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel messageModel = messageModels.get(position);
        if(holder.getClass()==senderViewHolder.class){
            ((senderViewHolder)holder).senderMessage.setText(messageModel.getMessage());
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            Long timestamp = messageModel.getTimestamp();
            String time = sdf.format(timestamp);
            ((senderViewHolder)holder).senderTime.setText(time);
        }else{
            ((recieverViewHolder)holder).recieverMessage.setText(messageModel.getMessage());
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            Long timestamp = messageModel.getTimestamp();
            String time = sdf.format(timestamp);
            ((recieverViewHolder)holder).recieverTime.setText(time);
            String id = messageModel.getUid();
            ((recieverViewHolder)holder).recieverId.setText(id);
        }

    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        pref = context.getSharedPreferences(FILE_NAME,MODE_PRIVATE);
        log = pref.getString(PARAM_ONE,null);
        uid = pref.getString(PARAM_TWO,null);
        if(messageModels.get(position).getUid().equals(uid)){
            return SENDER_VIEW_TYPE;
        }else{
            return RECIEVER_VIEW_TYPE;
        }
        //return super.getItemViewType(position);

    }

    public class recieverViewHolder extends RecyclerView.ViewHolder {

        TextView recieverMessage, recieverTime, recieverId;
        public recieverViewHolder(@NonNull View itemView) {
            super(itemView);
            recieverMessage = itemView.findViewById(R.id.textViewChatRecieverG);
            recieverTime = itemView.findViewById(R.id.timeViewRecieverG);
            recieverId = itemView.findViewById(R.id.idViewReciever);

        }
    }

    public class senderViewHolder extends RecyclerView.ViewHolder {

        TextView senderMessage, senderTime;
        public senderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMessage = itemView.findViewById(R.id.textViewChatSender);
            senderTime = itemView.findViewById(R.id.timeViewSender);

        }
    }

}
