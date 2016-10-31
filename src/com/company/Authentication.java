package com.company;

import org.apache.commons.codec.binary.Base32;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import static java.lang.Math.floor;

/**
 * Created by noboud_n on 31/10/2016.
 */

public class Authentication {

    Authentication() {};

    public String GoogleAuthenticatorCode(String secret) {
        Base32 base = new Base32();

        byte[] key = base.decode(secret.getBytes());

        Date date = new Date();
        long unixTime = date.getTime() / 1000;
        Double message = floor(unixTime / 30);

        SecretKey secretKey = null;
        secretKey = new SecretKeySpec(key, "HmacSHA1");
        Mac mac = null;
        try {
            mac = Mac.getInstance("HmacSHA1");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        try {
            mac.init(secretKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            return "";
        }
        return "";
    }

}
