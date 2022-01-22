package com.example.thevault.support.hashing;

import com.example.thevault.klant.BCryptWachtwoordHash;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;



class BCryptWachtwoordHashTest {

    String wachtwoord1;
    String wachtwoord2;

    @BeforeEach
    void setUp() {
        wachtwoord1 = "welkom1";
        wachtwoord2 = "welk0m1";
    }

    @Test
    void hashWachtwoord() {
        String wachtwoordGehasht = BCryptWachtwoordHash.hashWachtwoord(wachtwoord1);
        assertThat(BCryptWachtwoordHash.verifyHash(wachtwoord1, wachtwoordGehasht)).isTrue();

    }
}