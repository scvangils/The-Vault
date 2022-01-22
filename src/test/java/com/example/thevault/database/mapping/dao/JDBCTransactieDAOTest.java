// Created by E.S. Olthof
// Creation date 21/12/2021


package com.example.thevault.database.mapping.dao;

import com.example.thevault.financieel.Cryptomunt;
import com.example.thevault.financieel.Asset;
import com.example.thevault.handelingen.JDBCTransactieDAO;
import com.example.thevault.handelingen.Transactie;
import com.example.thevault.klant.Klant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class JDBCTransactieDAOTest {


    private JDBCTransactieDAO jdbcTransactieDAO;
    private Klant testKlant1;
    private Klant testKlant2;
    private Transactie testTransactie1;
    private Transactie  testTransactie2;
    private Transactie testTransactie3;
    private Transactie testTransactie4;
    private Asset testAsset1;
    private Asset testAsset2;
    private Asset testAsset3;
    private static Cryptomunt testCryptomunt1;
    private static Cryptomunt testCryptomunt2;


    @Autowired
    public JDBCTransactieDAOTest (JDBCTransactieDAO jdbcTransactieDAO){
        super();
        this.jdbcTransactieDAO = jdbcTransactieDAO;
    }

    @BeforeEach
    void setup(){


        testKlant1 = new Klant("Carmen", "GoedWachtwoord",
                "Carmen", 123456789, LocalDate.parse("1985-12-30"));
        testKlant2 = new Klant("Jolien", "BeterWachtwoord",
                "Jolien", 987654321, LocalDate.parse("1985-10-14"));
        testKlant1.setGebruikerId(1);
        testKlant2.setGebruikerId(2);

        testCryptomunt1 = new Cryptomunt(1, "BITCOIN", "BCN" );
        testCryptomunt2 = new Cryptomunt(2, "ETHERIUM", "ETH");


        testTransactie1 = new Transactie(LocalDateTime.of(2021, 12, 15, 12, 43, 21), testKlant2, testCryptomunt1, 43.5
                , 1.3, testKlant1);
        testTransactie1.setTransactieId(1);
        testTransactie2 = new Transactie(LocalDateTime.of(2021, 12, 21, 10, 43, 21), testKlant1, testCryptomunt1, 10.5
                , 1.7, testKlant2);
        testTransactie2.setTransactieId(2);
        testTransactie3 = new Transactie(LocalDateTime.of(2021,11,10, 22,22,22), testKlant1, testCryptomunt1, 9.5
                , 0.5, testKlant2);
        testTransactie3.setTransactieId(3);
        testTransactie4 = new Transactie(LocalDateTime.of(2018, 7, 1, 12, 43, 21), testKlant2, testCryptomunt2, 43.5
                , 1.5, testKlant1);
        testTransactie1.setTransactieId(1);

    }


    @Test
    void slaTransactieOp() {
        Transactie actual = jdbcTransactieDAO.slaTransactieOp(testTransactie4);
        Transactie expected = testTransactie4;
        assertThat(actual).isNotNull().isEqualTo(expected);
    }

    @Test
    void geefAlleTransacties() {
        List<Transactie> actualTransactieLijst = jdbcTransactieDAO.geefAlleTransacties();

        assertThat(actualTransactieLijst.get(0)).isInstanceOf(Transactie.class).isNotNull();
        assertThat(actualTransactieLijst.get(0).getMomentTransactie()).isEqualTo(testTransactie1.getMomentTransactie());
        assertThat(actualTransactieLijst.get(0).getTransactieId()).isEqualTo(testTransactie1.getTransactieId());
        assertThat(actualTransactieLijst.get(0).getAantal()).isEqualTo(testTransactie1.getAantal());
        assertThat(actualTransactieLijst.get(0).getKoper().getGebruikerId()).isEqualTo(testTransactie1.getKoper().getGebruikerId());
        assertThat(actualTransactieLijst.get(1).getMomentTransactie()).isEqualTo(testTransactie2.getMomentTransactie());
        assertThat(actualTransactieLijst.get(1).getTransactieId()).isEqualTo(testTransactie2.getTransactieId());
        assertThat(actualTransactieLijst.get(1).getAantal()).isEqualTo(testTransactie2.getAantal());
        assertThat(actualTransactieLijst.get(1).getKoper().getGebruikerId()).isEqualTo(testTransactie2.getKoper().getGebruikerId());
        assertThat(actualTransactieLijst.get(2).getMomentTransactie()).isEqualTo(testTransactie3.getMomentTransactie());
        assertThat(actualTransactieLijst.get(2).getTransactieId()).isEqualTo(testTransactie3.getTransactieId());
        assertThat(actualTransactieLijst.get(2).getAantal()).isEqualTo(testTransactie3.getAantal());
        assertThat(actualTransactieLijst.get(2).getKoper().getGebruikerId()).isEqualTo(testTransactie3.getKoper().getGebruikerId());
    }


    @Test
    void geefTransactiesVanGebruiker() {
        List <Transactie> actualTransactiesGebruiker = jdbcTransactieDAO.geefTransactiesVanGebruiker(testKlant1);

        assertThat(actualTransactiesGebruiker).hasSize(3); // 1 transactie is tijdens de eerste unit test extra opgeslagen
        assertThat(actualTransactiesGebruiker.get(0).getAantal()).isEqualTo(testTransactie1.getAantal());
        assertThat(actualTransactiesGebruiker.get(1).getTransactieId()).isEqualTo(testTransactie2.getTransactieId());
        assertThat(actualTransactiesGebruiker.get(2).getTransactieId()).isEqualTo(testTransactie3.getTransactieId());

    }

    /* TODO methode werkend maken. Geeft nu alles transacties van gebruiker ongeacht de periode */
    @Test
    void geefTransactiesVanGebruikerInPeriode() {
        List<Transactie> actualLijst = jdbcTransactieDAO.geefTransactiesVanGebruikerInPeriode(testKlant1, Timestamp.valueOf(LocalDateTime.of(2021, 12, 10, 1, 1)), Timestamp.valueOf(LocalDateTime.of(2021, 12, 25, 1, 1)));

        assertThat(actualLijst).hasSize(2);
    }

    @Test
    void geefAlleTransactiesInPeriode() {
        // geeft alle transacties in de periode van 10-12-2021 en 25-12-2021 = 2 transacties
        List<Transactie> actualLijst = jdbcTransactieDAO.geefAlleTransactiesInPeriode( Timestamp.valueOf(LocalDateTime.of(2021, 12, 10, 1, 1)), Timestamp.valueOf(LocalDateTime.of(2021, 12, 25, 1, 1)));
        assertThat(actualLijst).hasSize(2); //inclusief extra transacie van eerste
    }

    @Test
    void geefTransactiesVanGebruikerMetCryptomunt() {
        List<Transactie> actualLijst = jdbcTransactieDAO.geefTransactiesVanGebruikerMetCryptomunt(testKlant1, testCryptomunt1);

        assertThat(actualLijst).filteredOn(transactie -> transactie.getCryptomunt().getId() == 1);
        assertThat(actualLijst).hasSize(3);
    }

    @Test
    void verwijderTransactie() {
        Transactie actualTransactie = jdbcTransactieDAO.verwijderTransactie(testTransactie1);
        Transactie expectedTransactie = testTransactie1;

        assertThat(actualTransactie.getTransactieId()).isEqualTo(expectedTransactie.getTransactieId());
        assertThat(jdbcTransactieDAO.geefAlleTransacties()).hasSizeLessThan(3);
    }
}
