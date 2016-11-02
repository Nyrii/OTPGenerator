package com.company;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.HmacUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.text.Utilities;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.floor;

/**
 * Created by noboud_n on 31/10/2016.
 */

public class Authentication {

    Authentication() {};

	private String truncateHash(byte[] hash) {
		int offset = hash[hash.length - 1] & 0xF;

		long truncatedHash = 0;
		for (int i = 0; i < 4; ++i) {
			truncatedHash <<= 8;
			truncatedHash |= (hash[offset + i] & 0xFF);
		}

		truncatedHash &= 0x7FFFFFFF;
		truncatedHash %= 1000000;

		int code = (int) truncatedHash;
		String result = Integer.toString(code);
		for (int i = result.length(); i < 6; i++) {
			result = "0" + result;
		}
		return result;
	}

	private byte[] hmacSha1(byte[] value, byte[] keyBytes) {
		SecretKeySpec signKey = new SecretKeySpec(keyBytes, "HmacSHA1");
		try {
			Mac mac = Mac.getInstance("HmacSHA1");

			mac.init(signKey);

			byte[] rawHmac = mac.doFinal(value);

			return new Hex().encode(rawHmac);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String GoogleAuthenticatorCode(String secret) throws Exception {
        Base32 base = new Base32();
        byte[] key = base.decode(secret);

		long value = new Date().getTime() / TimeUnit.SECONDS.toMillis(30);

//		byte[] data = new byte[8];
//		for (int i = 8; i-- > 0; value >>>= 8) {
//			data[i] = (byte) value;
//		}

		System.out.println("Time remaining : " + new Date().getTime() / 1000 % 30);

//		byte[] hash = hmacSha1(data, key);
//        return truncateHash(hash);
		return String.valueOf(verifyCode(key, value));
    }

	int verifyCode(byte[] key, long t) throws Exception {
		byte[] data = new byte[8];
		long value = t;
		for (int i = 8; i-- > 0; value >>>= 8) {
			data[i] = (byte) value;
		}

		SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(signKey);
		byte[] hash = mac.doFinal(data);

		int offset = hash[20 - 1] & 0xF;

		// We're using a long because Java hasn't got unsigned int.
		long truncatedHash = 0;
		for (int i = 0; i < 4; ++i) {
			truncatedHash <<= 8;
			// We are dealing with signed bytes:
			// we just keep the first byte.
			truncatedHash |= (hash[offset + i] & 0xFF);
		}

		truncatedHash &= 0x7FFFFFFF;
		truncatedHash %= 1000000;

		return (int) truncatedHash;
	}

	public static void main(String[] args) {
		Authentication auth = new Authentication();

		try {
			System.out.println(auth.GoogleAuthenticatorCode("uhshrl33v7xrc6wxuclkvdtmqagk3fkp"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}