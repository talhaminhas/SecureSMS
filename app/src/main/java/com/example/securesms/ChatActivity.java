package com.example.securesms;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.NoSuchPaddingException;

public class ChatActivity extends AppCompatActivity {

    RecyclerView messagesRecyclerview;
    ArrayList <Message> messages = new ArrayList<>();
    TextView title;
    ImageView send;
    EditText textField;
    DatabaseHelper myDb;
    String number;

    final int SEND_SMS_PERMISSION_REQUEST_CODE = 1;
    BroadcastReceiver receiver;
    IntentFilter filter;

    AdvanceEncryptionAlgorithm advanceEncryptionAlgorithm;
    String key1 = "lv39eptlvuhaqqsr",key2 = "lv39xptlvyha3qsr",key3 = "hv39ept9vuhaq4sr";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        try {
            advanceEncryptionAlgorithm = new AdvanceEncryptionAlgorithm();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        receiver = new BroadcastReceiver() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onReceive(Context context, Intent intent) {
                DatabaseHelper myDb = new DatabaseHelper(context);
                //Toast.makeText(context, "sms received", Toast.LENGTH_SHORT).show();
                if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
                    Bundle bundle = intent.getExtras();
                    SmsMessage[] msgs = null;
                    String msg_from;
                    if(bundle != null){
                        try{
                            Object[] pdus = (Object[]) bundle.get("pdus");
                            msgs = new SmsMessage[pdus.length];
                            for(int i=0 ; i< msgs.length ; i++)
                            {
                                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                                msg_from = msgs[i].getOriginatingAddress();
                                String msgBody = msgs[i].getMessageBody();
                                myDb.insertData(msg_from, msgBody, false);
                                messages = myDb.getMessages(number);
                                messagesRecyclerview.setAdapter(new MessageAdapter(ChatActivity.this, messages));
                                //Toast.makeText(context, context.getClass().getSimpleName(), Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(receiver,filter);

        formatNumber();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS},1000);
        }
        if(!checkPermission(Manifest.permission.SEND_SMS)){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQUEST_CODE);
        }

        myDb = new DatabaseHelper(this);
        textField = findViewById(R.id.text_field);
        send = findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if(!textField.getText().toString().equals("")) {
                    String encryptedMessage = advanceEncryptionAlgorithm.encrypt(textField.getText().toString(),key1);
                    encryptedMessage = advanceEncryptionAlgorithm.encrypt(encryptedMessage,key2);
                    encryptedMessage = advanceEncryptionAlgorithm.encrypt(encryptedMessage,key3);
                    myDb.insertData(number, encryptedMessage, true);
                    if(checkPermission(Manifest.permission.SEND_SMS)){
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(number, null, encryptedMessage, null, null);
                    }
                    textField.setText("");
                    messages = myDb.getMessages(number);
                    messagesRecyclerview.setAdapter(new MessageAdapter(ChatActivity.this, messages));
                }
            }
        });

        title = findViewById(R.id.title);
        title.setText(getIntent().getStringExtra("name"));

        messages = myDb.getMessages(number);
        messagesRecyclerview = (RecyclerView) findViewById(R.id.messages_recyclerview);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setStackFromEnd(true);
        messagesRecyclerview.setLayoutManager(lm);
        messagesRecyclerview.setAdapter(new MessageAdapter(this, messages));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    public boolean checkPermission(String permission){
        int check = ContextCompat.checkSelfPermission(this, permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }
    public void formatNumber(){
        this.number = "";
        String number = getIntent().getStringExtra("number");
        for(int i=0;i<number.length();i++){
            if(i==0 && number.charAt(i) == '0'){
                this.number += "+92";
            }
            else if(number.charAt(i) != ' '){
                this.number += number.charAt(i);
            }
        }
    }
}
