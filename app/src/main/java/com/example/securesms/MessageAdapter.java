package com.example.securesms;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    Context context;
    public ArrayList<Message> messages = new ArrayList<>();

    public MessageAdapter (Context context,ArrayList <Message> messages)
    {
        this.context = context;
        this.messages = messages;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View messageRowView = inflater.inflate(R.layout.row_message,parent,false);
        MessageAdapter.MessageRow messageRow = new MessageAdapter.MessageRow(messageRowView);
        return messageRow;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (messages.get(position).me){
            ((MessageRow)holder).right.setText(messages.get(position).message);
            ((MessageRow)holder).left.setVisibility(View.GONE);
            return;
        }
        ((MessageRow)holder).left.setText(messages.get(position).message);
        ((MessageRow)holder).right.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
    public class MessageRow extends RecyclerView.ViewHolder {

        TextView left,right;
        public MessageRow(@NonNull View itemView) {
            super(itemView);
            left = (TextView) itemView.findViewById(R.id.left);
            right = (TextView) itemView.findViewById(R.id.right);
        }
    }
}
