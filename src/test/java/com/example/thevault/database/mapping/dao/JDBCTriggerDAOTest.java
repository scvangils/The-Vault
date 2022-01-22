package com.example.thevault.database.mapping.dao;

import com.example.thevault.financieel.Cryptomunt;
import com.example.thevault.handelingen.JDBCTriggerDAO;
import com.example.thevault.handelingen.Trigger;
import com.example.thevault.handelingen.TriggerKoper;
import com.example.thevault.handelingen.TriggerVerkoper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;


import com.example.thevault.klant.Gebruiker;
import com.example.thevault.klant.Klant;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class JDBCTriggerDAOTest {

    private final JDBCTriggerDAO jdbcTriggerDAOtest;
    private static Gebruiker testKlant1;
    private static Gebruiker testKlant2;
    private static Cryptomunt testCryptomunt1;
    private static Cryptomunt testCryptomunt2;
    private static Cryptomunt testCryptomunt3;
    private static LocalDate testDatum1;
    private static LocalDate testDatum2;
        
    private static Trigger testTriggerKoper1;
    private static Trigger testTriggerKoper2;
    private static Trigger testTriggerKoper3;
    private static Trigger testTriggerKoper4;
    private static Trigger testTriggerKoperNietAanwezig;
    private static Trigger testTriggerVerkoper1;
    private static Trigger testTriggerVerkoper2;
    private static Trigger testTriggerVerkoper3;
    private static Trigger testTriggerVerkoper4;
    private static Trigger testTriggerVerkoperNietAanwezig;

    @Autowired
    public JDBCTriggerDAOTest(JDBCTriggerDAO jdbcTriggerDAO) {
            super();
            this.jdbcTriggerDAOtest = jdbcTriggerDAO;
        }
    @Test
    void testTheTest(){
        assertThat(jdbcTriggerDAOtest).isNotNull();
    }
    @BeforeEach
    void setup(){
        testKlant1 = new Klant("Carmen", "GoedWachtwoord",
                "Carmen", 123456789, LocalDate.parse("1985-12-30"));
        testKlant2 = new Klant("Jolien", "BeterWachtwoord",
                "Jolien", 987654321, LocalDate.parse("1985-10-14"));
        testKlant1.setGebruikerId(1);
        testKlant2.setGebruikerId(2);
        testDatum1 = LocalDate.of(2022,1,1);
        testDatum2 = LocalDate.of(2022,1,2);
        testCryptomunt1 = new Cryptomunt(1, "BITCOIN", "BCN");
        testCryptomunt2 = new Cryptomunt(2, "ETHERIUM", "ETH");
        testCryptomunt3 = new Cryptomunt(2, null, null);
        testTriggerKoper1 = new TriggerKoper(testKlant1, testCryptomunt1, 50.0, 2.0);
        testTriggerKoper1.setTriggerId(1);
        testTriggerKoper1.setDatum(testDatum1);
        testTriggerKoper2 = new TriggerKoper(testKlant2, testCryptomunt2, 20.0, 3.5);
        testTriggerKoper2.setTriggerId(2);
        testTriggerKoper2.setDatum(testDatum1);
        testTriggerKoper3 = new TriggerKoper(testKlant1, testCryptomunt1, 50.0, 2.0);
        testTriggerKoper3.setTriggerId(3);
        testTriggerKoper3.setDatum(testDatum2);
        testTriggerKoper4 = new TriggerKoper(testKlant2, testCryptomunt2, 20.0, 3.5);
        testTriggerKoper4.setTriggerId(4);
        testTriggerKoper4.setDatum(testDatum2);
        testTriggerKoperNietAanwezig = new TriggerKoper(testKlant2, testCryptomunt2, 100.0, 3.5);
        testTriggerKoperNietAanwezig.setTriggerId(20);
        testTriggerKoperNietAanwezig.setDatum(testDatum2);
        testTriggerVerkoper1 = new TriggerVerkoper(testKlant2, testCryptomunt1, 40.0, 2.0);
        testTriggerVerkoper1.setTriggerId(1);
        testTriggerVerkoper1.setDatum(testDatum1);
        testTriggerVerkoper2 = new TriggerVerkoper(testKlant1, testCryptomunt2, 30.0, 3.5);
        testTriggerVerkoper2.setTriggerId(2);
        testTriggerVerkoper2.setDatum(testDatum1);
        testTriggerVerkoper3 = new TriggerVerkoper(testKlant2, testCryptomunt1, 40.0, 2.0);
        testTriggerVerkoper3.setTriggerId(3);
        testTriggerVerkoper3.setDatum(testDatum2);
        testTriggerVerkoper4 = new TriggerVerkoper(testKlant1, testCryptomunt2, 30.0, 3.5);
        testTriggerVerkoper4.setTriggerId(4);
        testTriggerVerkoper4.setDatum(testDatum2);
        testTriggerVerkoperNietAanwezig =  new TriggerVerkoper(testKlant1, testCryptomunt2, 100.0, 3.5);
        testTriggerVerkoperNietAanwezig.setTriggerId(20);
        testTriggerVerkoperNietAanwezig.setDatum(testDatum2);

    }


    @Test
    void vindTriggerByIdKoper(){
        Trigger expected = testTriggerKoper1;
        Trigger actual = jdbcTriggerDAOtest.vindTriggerById(1, "Koper");
        assertThat(actual).isEqualTo(expected);
    }
    @Test
    void vindTriggerByIdVerkoper(){
        Trigger expected = testTriggerVerkoper1;
        Trigger actual = jdbcTriggerDAOtest.vindTriggerById(1, "Verkoper");
        assertThat(actual).isEqualTo(expected);
    }
    @Test
    void vindTriggerByIdKoperNull(){
        Trigger actual = jdbcTriggerDAOtest.vindTriggerById(10, "Koper");
        assertThat(actual).isNull();
    }
    @Test
    void vindTriggerByIdVerkoperNull(){
        Trigger actual = jdbcTriggerDAOtest.vindTriggerById(15, "Verkoper");
        assertThat(actual).isNull();
    }


    @Test
    void slaTriggerOp() {
        Trigger expected = new TriggerKoper(testKlant1, testCryptomunt2, 30.0, 3.5);
        Trigger actual = jdbcTriggerDAOtest.slaTriggerOp(expected);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void verwijderTrigger() {
        int actual = jdbcTriggerDAOtest.verwijderTrigger(testTriggerKoper1);
        int expected = 1;
        assertThat(actual).as("verwijder bestaande TriggerKoper").isEqualTo(expected);
        actual = jdbcTriggerDAOtest.verwijderTrigger(testTriggerVerkoper1);
        assertThat(actual).as("verwijder bestaande TriggerVerkoper").isEqualTo(expected);
    }
    @Test
    void verwijderTriggerNietBestaand() {
        int actual = jdbcTriggerDAOtest.verwijderTrigger(testTriggerKoperNietAanwezig);
        int expected = 0;
        assertThat(actual).as("verwijder niet bestaande TriggerKoper").isEqualTo(expected);
        actual = jdbcTriggerDAOtest.verwijderTrigger(testTriggerVerkoperNietAanwezig);
        assertThat(actual).as("verwijder niet bestaande TriggerVerkoper").isEqualTo(expected);
    }

    @Test
    void vindMatch() {
        Trigger actual = jdbcTriggerDAOtest.vindMatch(testTriggerKoper3);
        Trigger expected = testTriggerVerkoper1;
        assertThat(actual).as("koper vindt verkoper, voldoet beste aan voorwaarden").isEqualTo(expected);
        actual = jdbcTriggerDAOtest.vindMatch(testTriggerVerkoper1);
        expected = testTriggerKoper1;
        assertThat(actual).as("verkoper vindt koper, niet symmetrisch vanwege voorwaarden").isEqualTo(expected);
    }
    @Test
    void vindMatchNull() {
        Trigger actual = jdbcTriggerDAOtest.vindMatch(testTriggerKoper2);
        assertThat(actual).as("koper vindt geen verkoper, voldoet niet aan voorwaarden").isNull();
        actual = jdbcTriggerDAOtest.vindMatch(testTriggerVerkoper2);
        assertThat(actual).as("verkoper vindt geen koper, voldoet niet aan voorwaarden").isNull();
    }

    @Test
    void vindTriggersByGebruikerKoper() {
        List<Trigger> alleTriggersKoperTestKlant1 = jdbcTriggerDAOtest.vindTriggersByGebruiker(testKlant1, "Koper");
        assertThat(alleTriggersKoperTestKlant1).as("Twee triggerKopers voor TestKlant1").contains(testTriggerKoper1,testTriggerKoper3 ).hasSize(2);
        List<Trigger> alleTriggersKoperTestKlant2 = jdbcTriggerDAOtest.vindTriggersByGebruiker(testKlant2, "Koper");
        assertThat(alleTriggersKoperTestKlant2).as("Twee triggerKopers voor TestKlant2").contains(testTriggerKoper2,testTriggerKoper4).hasSize(2);
    }
    @Test
    void vindTriggersByGebruikerVerkoper() {
        List<Trigger> alleTriggersVerkoperTestKlant2 = jdbcTriggerDAOtest.vindTriggersByGebruiker(testKlant2, "Verkoper");
        assertThat(alleTriggersVerkoperTestKlant2).as("Vijf triggerVerkopers voor TestKlant2").contains(testTriggerVerkoper1,testTriggerVerkoper3 ).hasSize(5);
        List<Trigger> alleTriggersVerkoperTestKlant1 = jdbcTriggerDAOtest.vindTriggersByGebruiker(testKlant1, "Verkoper");
        assertThat(alleTriggersVerkoperTestKlant1).as("Twee triggerVerkopers voor TestKlant1").contains(testTriggerVerkoper2,testTriggerVerkoper4).hasSize(2);
    }

    @Test
    void vindAlleTriggersKoper() {
        List<Trigger> alleTriggerKopers = jdbcTriggerDAOtest.vindAlleTriggers("Koper");
        assertThat(alleTriggerKopers).contains(testTriggerKoper1, testTriggerKoper2, testTriggerKoper3, testTriggerKoper4).hasSize(4);
    }
    @Test
    void vindAlleTriggersVerkoper() {
        List<Trigger> alleTriggerVerkopers = jdbcTriggerDAOtest.vindAlleTriggers("Verkoper");
        assertThat(alleTriggerVerkopers).contains(testTriggerVerkoper1, testTriggerVerkoper2, testTriggerVerkoper3, testTriggerVerkoper4).hasSize(7);
    }

}