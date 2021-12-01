/*20211201 WB hashed password
 */

package com.example.thevault.support;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashHelper {
    public static final String SHA_256 = "SHA-256";
    public static final String ALGORITME_BESTAAT_NIET = "Het opgegeven algoritme bestaat niet";

public static String hashHelper(String wachtwoord){
    try {
        MessageDigest sha = MessageDigest.getInstance(SHA_256);
        sha.update(wachtwoord.getBytes(StandardCharsets.UTF_8));
        byte[] digest = sha.digest();
        return ByteArrayToHexHelper.codeerHexString(digest);
    }catch (NoSuchAlgorithmException e){
        e.printStackTrace();
        throw new RuntimeException(ALGORITME_BESTAAT_NIET);
    }
}
}
