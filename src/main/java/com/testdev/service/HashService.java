package com.testdev.service;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

import org.bouncycastle.jcajce.provider.digest.SHA3.DigestSHA3;
import org.bouncycastle.util.encoders.Hex;

public class HashService {
private static Size DEFAULT = Size.S224;
    
    public static String digest(String string) {
        return digest(string, DEFAULT, true);
    }
    
    public static String digest(String string, Size s) {
        return digest(string, s, true);
    }
    
    public static String digest(String string, Size s, boolean bouncyencoder) {
        Size size = s == null ? DEFAULT : s;
        
        DigestSHA3 md = new DigestSHA3(size.getValue());
        String text = string != null ? string : "null";
        try {
            md.update(text.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            // most unlikely
            md.update(text.getBytes());
        }
        byte[] digest = md.digest();
        return encode(digest, bouncyencoder);
    }
    
    public static String encode(byte [] bytes, boolean bouncyencoder) {
        if (bouncyencoder)
            return Hex.toHexString(bytes);
        else {
            BigInteger bigInt = new BigInteger(1, bytes);
            return bigInt.toString(16);
        }
    }
    
    public enum Size {
        
        S224(224),
        S256(256),
        S384(384),
        S512(512);
        
        int bits = 0;
        
        Size(int bits) {
            this.bits = bits;
        }
        
        public int getValue() {
            return this.bits;
        }
    }
}
