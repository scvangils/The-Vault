package com.example.thevault.klant.support;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class BCryptWachtwoordHash {
    private final static int RONDES = 12;

    //TODO JavaDoc
    public static String hashWachtwoord(String wachtwoord){
        return BCrypt.hashpw(wachtwoord, BCrypt.gensalt(RONDES));
    }

    //TODO JavaDoc
    public static boolean verifyHash(String wachtwoord, String hash) {
        return BCrypt.checkpw(wachtwoord, hash);
    }
}
