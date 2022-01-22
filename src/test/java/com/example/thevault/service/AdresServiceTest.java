package com.example.thevault.service;

import com.example.thevault.klant.AdresService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class AdresServiceTest {

    AdresService adresService;
    String postcode1;
    String postcode2;

    @BeforeEach
    void setUp() {
        postcode1 = "1000AA";
        postcode2 = "100 AA";
        adresService = new AdresService();
    }

    @Test
    void postcodeOpmaak() {
        boolean validatie = adresService.postcodeOpmaak(postcode1);
        assertThat(validatie).isTrue();
    }

    @Test
    void postcodeOpmaak2() {
        boolean validatie = adresService.postcodeOpmaak(postcode2);
        assertThat(validatie).isFalse();
    }
}