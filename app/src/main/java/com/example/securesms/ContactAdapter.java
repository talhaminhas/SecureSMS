package com.example.securesms;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {



    Context context;
    private OnContactListner onContactListner;
    ArrayList <Contact> contacts = new ArrayList<>();
    @RequiresApi(api = Build.VERSION_CODES.O)
    public ContactAdapter (Context context, OnContactListner onContactListner, ArrayList <Contact> contacts)
    {
        this.onContactListner = onContactListner;
        this.context = context;
        this.contacts = contacts;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactRowView = inflater.inflate(R.layout.row_contact,parent,false);
        ContactRow contactRow = new ContactRow(contactRowView,onContactListner);
        return contactRow;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ContactRow)holder).name.setText(contacts.get(position).name);
        ((ContactRow)holder).number.setText(contacts.get(position).number);
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }
    public class ContactRow extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView name,number;
        OnContactListner onContactListner ;
        public ContactRow(@NonNull View itemView, OnContactListner onContactListner) {
            super(itemView);
            this.onContactListner = onContactListner;
            name = (TextView) itemView.findViewById(R.id.name);
            number = (TextView) itemView.findViewById(R.id.number);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onContactListner.onContactClick(getAdapterPosition());
        }
    }
    public interface OnContactListner{
            void onContactClick(int position);
    }

}
