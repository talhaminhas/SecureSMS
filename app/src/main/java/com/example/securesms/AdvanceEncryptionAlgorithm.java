package com.example.securesms;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class AdvanceEncryptionAlgorithm {
    private Cipher cipher;
    public AdvanceEncryptionAlgorithm() throws NoSuchPaddingException, NoSuchAlgorithmException {
        cipher = Cipher.getInstance("AES");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String encrypt (String Message ,String Key){
        Key key = new SecretKeySpec(Key.getBytes(),"AES") ;
        String encryptedMessage = "";
        try {
            cipher.init(Cipher.ENCRYPT_MODE,key);
            byte[] encryptedMessageBytes = cipher.doFinal(Message.getBytes());
            encryptedMessage = Base64.getEncoder().encodeToString(encryptedMessageBytes);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return encryptedMessage;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String decrypt (String Message ,String Key){
        Key key = new SecretKeySpec(Key.getBytes(),"AES") ;
        String decryptedMessage = "";
        try {
            cipher.init(Cipher.DECRYPT_MODE,key);
            byte[] decryptedMessageBytes = cipher.doFinal(Base64.getMimeDecoder().decode(Message));
            decryptedMessage = new String(decryptedMessageBytes);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return decryptedMessage;
    }

}
