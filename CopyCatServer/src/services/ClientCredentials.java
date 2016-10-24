package services;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ClientCredentials implements Serializable{
    private String username;
    private byte[] hashPassword;

    ClientCredentials(String username, String password){
        this.username = username;
        try {
            this.hashPassword = MessageDigest.getInstance("MD5").digest(password.getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    public byte[] getHashPassword() {
        return hashPassword;
    }
}
