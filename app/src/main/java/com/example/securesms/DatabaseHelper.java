package com.example.securesms;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.NoSuchPaddingException;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "messages.db";
    public static final String TABLE_NAME = "messages_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "SENDER";
    public static final String COL_3 = "MESSAGE";
    public static final String COL_4 = "ME";

    AdvanceEncryptionAlgorithm advanceEncryptionAlgorithm;
    String key1 = "lv39eptlvuhaqqsr",key2 = "lv39xptlvyha3qsr",key3 = "hv39ept9vuhaq4sr";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME,null,1);
        try {
            advanceEncryptionAlgorithm = new AdvanceEncryptionAlgorithm();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "SENDER TEXT, MESSAGE TEXT, ME BOOLEAN)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public long insertData (String sender, String message, Boolean me){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,sender);
        contentValues.put(COL_3,message);
        contentValues.put(COL_4,me);
        return db.insert(TABLE_NAME,null,contentValues);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList <Message> getMessages (String sender){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList <Message> messages = new ArrayList<>();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME,null);
        if(res.getCount() !=0){
            while (res.moveToNext()){
                if(sender.equals(res.getString(1))) {
                    String decryptedMessage = advanceEncryptionAlgorithm.decrypt(res.getString(2),key3);
                    decryptedMessage = advanceEncryptionAlgorithm.decrypt(decryptedMessage,key2);
                    decryptedMessage = advanceEncryptionAlgorithm.decrypt(decryptedMessage,key1);
                    messages.add(new Message(
                            res.getString(1),
                            decryptedMessage,
                            res.getInt(3) > 0
                    ));
                }
            }
        }
        return messages;
    }
}
