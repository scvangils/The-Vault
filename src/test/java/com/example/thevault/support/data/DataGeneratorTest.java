package com.example.thevault.support.data;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

class DataGeneratorTest {

    @Test
    void isSchrikkeljaar() {
        int geenSchrikkeljaar1 = 1999;
        int geenSchrikkeljaar2 = 1998;
        int geenSchrikkeljaar3 = 1997;
        int schrikkeljaar1 = 2000;
        int schrikkeljaar2 = 1996;
        assertThat(DataGenerator.isSchrikkeljaar(geenSchrikkeljaar1)).as("1999 was geen schrikkeljaar").isFalse();
        assertThat(DataGenerator.isSchrikkeljaar(geenSchrikkeljaar2)).as("1998 was geen schrikkeljaar").isFalse();
        assertThat(DataGenerator.isSchrikkeljaar(geenSchrikkeljaar3)).as("1997 was geen schrikkeljaar").isFalse();
        assertThat(DataGenerator.isSchrikkeljaar(schrikkeljaar1)).as("2000 was een schrikkeljaar").isTrue();
        assertThat(DataGenerator.isSchrikkeljaar(schrikkeljaar2)).as("1996 was een schrikkeljaar").isTrue();

    }

    @Test
    void testUniekeGebruikersnaam() throws IOException {
        int hoeveelKlanten = 3000;
        Set<String> gebruikersnaamLijst = DataGenerator.maakLijstKlantenVanCSV("Sprint2/datacsv.csv", hoeveelKlanten).
                stream().map(klant -> klant.getGebruikersnaam()).collect(Collectors.toSet());
        assertThat(gebruikersnaamLijst.size()).isEqualTo(hoeveelKlanten);
    }
}