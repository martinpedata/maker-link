package com.example.makerlink.access;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


/// Database contains a column for the salt, and a column for the hashed salted password. The user at login will input the
/// password, and the program will retrieve the salt of that specific user, add it to the password, and hash it again. If
/// the password inputted is the same, then the hashed salted pw produced at login and the one in the database will be the same as well
public class HashCredentials {
    public static String generateSalt() {
        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[8]; // 64-bit salt
        sr.nextBytes(salt);
        return Base64.encodeToString(salt, Base64.NO_WRAP); // More or less 22 characters (bcs Base64 encoding). Store this with the hash
    }

    public static String hashPassWord(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String saltedPW = password + salt;
            byte[] hash = digest.digest(saltedPW.getBytes(StandardCharsets.UTF_8));

            // Convert to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
