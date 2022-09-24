package com.example.securesms;

public class Message {
    String sender,message;
    boolean me;

    public Message (String sender, String message,boolean me){
        this.sender = sender;
        this.message = message;
        this.me = me;
    }
}
