// Created by S.C. van Gils
// Creation date 20-12-2021

package com.example.thevault.database.mapping.dao;

import com.example.thevault.domain.model.Adres;
import com.example.thevault.domain.model.Gebruiker;
import com.example.thevault.domain.model.Klant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class JDBCKlantDAOTest {

    private final JDBCKlantDAO jdbcKlantDAOTest;
    private static Gebruiker testKlant1;
    private static Gebruiker testKlant2;
    private static Gebruiker testKlant3;
    private Gebruiker testKlant4;
    private static Adres testAdres1;
    private static Adres testAdres2;
    private static Klant nieuweKlant;

    @Autowired
    public JDBCKlantDAOTest(JDBCKlantDAO jdbcKlantDAO) {
        super();
        this.jdbcKlantDAOTest = jdbcKlantDAO;
    }


 @Test
 void testTheTest(){
       assertThat(jdbcKlantDAOTest).isNotNull();
 }
 @BeforeEach
 void setup(){
        testKlant1 = new Klant("Carmen", "GoedWachtwoord",
                "Carmen", 123456789, LocalDate.parse("1985-12-30"));
        testKlant2 = new Klant("Jolien", "BeterWachtwoord",
                "Jolien", 987654321, LocalDate.parse("1985-10-14"));
        testKlant3 = new Klant("Steven", "mijnWachtwoord",
                "Steven", 123456789, LocalDate.parse("1975-12-30"));
        testKlant4 = new Klant("Pimmeh", "BesteWachtwoord",
                "Pim", 246813579, LocalDate.parse("1987-02-26"));
        testAdres1 = new Adres("Hoofdstraat", 4, null, "1234AB", "Hellevoetsluis");
        testAdres2 = new Adres("Zijstraat", 6, "a", "9876CD", "Groessen");
        testAdres1.setAdresId(1);
        testAdres2.setAdresId(2);
        nieuweKlant = new Klant("StevenVG", "StevenPW", "Steven van Gils", 1010101010, LocalDate.parse("1975-12-30"));
 }


 @Test
 void vindKlantById(){
     Gebruiker actual = jdbcKlantDAOTest.vindKlantById(1);
     Gebruiker expected = testKlant1;
     assertThat(actual).as("GebruikerId wordt niet meegenomen in de equals methode, ze moeten hetzelfde zijn").isNotNull().isEqualTo(expected);
     ((Klant) expected).setBsn(0);
     assertThat(actual).as("BSN aangepast, horen niet meer gelijk te zijn").isNotNull().isNotEqualTo(expected);
 }

 @Test
 void slaNieuweKlantOp(){
        nieuweKlant.setAdres(testAdres1);
        Gebruiker actual = jdbcKlantDAOTest.slaKlantOp(nieuweKlant);
        nieuweKlant.setGebruikerId(3);
        Gebruiker expected = nieuweKlant;
        assertThat(actual).as("gebruikerId moet zijn aangepast").isNotNull().isEqualTo(expected);
 }
    @Test
    void slaKlantMetBestaandeUsernameOp() {
        Klant expected = (Klant) testKlant1;
        ((Klant) testKlant1).setAdres(testAdres1);
        assertThatThrownBy(() -> jdbcKlantDAOTest.slaKlantOp(expected)).isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    void vindAlleKlanten() {
        List<Klant> actual = jdbcKlantDAOTest.vindAlleKlanten();
        List<Klant> expected = new ArrayList<>();
        expected.add((Klant) testKlant1);
        expected.add((Klant) testKlant2);
        expected.add((Klant) testKlant4);
        assertThat(actual).as("Alle klanten in de database").isNotNull().isEqualTo(expected);
    }

    @Test
    void vindKlantByGebruikersnaam() {
        Gebruiker actual = jdbcKlantDAOTest.vindKlantByGebruikersnaam("Jolien");
        Gebruiker expected = testKlant2;
        assertThat(actual).as("Ze moeten hetzelfde zijn").isNotNull().isEqualTo(expected);
        expected.setGebruikersnaam("Jolie");
        assertThat(actual).as("Gebruikersnaam aangepast, horen niet meer gelijk te zijn").isNotNull().isNotEqualTo(expected);
    }

    @Test
    void vindNietBestaandeKlantByGebruikersnaam(){
        Gebruiker actual = jdbcKlantDAOTest.vindKlantByGebruikersnaam("Steven");
        Gebruiker expected = null;
        assertThat(actual).as("Deze klant is niet geregistreerd.").isNull();
    }

    @Test
    void verwijderKlant() {
        testKlant1.setGebruikerId(1);
       int actual = jdbcKlantDAOTest.verwijderKlant((Klant) testKlant1);
       int expected = 0;
        assertThat(actual).as("Deze klant mag niet verwijderd worden vanwege Foreign Key restricties.").isNotNull().isEqualTo(expected);
        Gebruiker actualKlant = jdbcKlantDAOTest.vindKlantByGebruikersnaam(testKlant1.getGebruikersnaam());
        Gebruiker expectedKlant = testKlant1;
        assertThat(actualKlant).as("Deze klant is niet verwijderd worden vanwege Foreign Key restricties.").isNotNull().isEqualTo(expectedKlant);
    }
    @Test
    void verwijderKlantCatchException() {
        try {
            jdbcKlantDAOTest.verwijderKlant((Klant) testKlant1);
        }
        catch(Exception exception){
            fail("Er is toch een exception niet opgevangen door het catch-blok van de methode");
        }
    }


    @Test
    void updateKlantGebruikersnaam() {
        Gebruiker before = jdbcKlantDAOTest.vindKlantByGebruikersnaam("Carmen");
        Gebruiker beforeExpected = testKlant1;
        ((Klant) before).setAdres(testAdres1);
        assertThat(before).as("Zeker weten dat de base case klopt").isNotNull().isEqualTo(beforeExpected);
        before.setGebruikersnaam("nieuweGebruikersnaam");
        assertThat(before).as("Ze moeten niet meer gelijk zijn").isNotNull().isNotEqualTo(beforeExpected);
        int actual = jdbcKlantDAOTest.updateKlant((Klant) before);
        int expected = 1;
        assertThat(actual).as("Er moet een rij aangepast zijn").isEqualTo(expected);
        assertThat(jdbcKlantDAOTest.vindKlantByGebruikersnaam("Carmen")).as("Deze gebruikersnaam hoort niet meer te vinden te zijn").isNull();
        Gebruiker after = jdbcKlantDAOTest.vindKlantByGebruikersnaam("nieuweGebruikersnaam");
        assertThat(after).as("De nieuwe gebruikersnaam moet zijn opgeslagen").isEqualTo(before);
    }

    @Test
    void updateKlantNaam() {
        Gebruiker before = jdbcKlantDAOTest.vindKlantByGebruikersnaam("Carmen");
        Gebruiker beforeExpected = testKlant1;
        ((Klant) before).setAdres(testAdres1);
        assertThat(before).as("Zeker weten dat de base case klopt").isNotNull().isEqualTo(beforeExpected);
        ((Klant) before).setNaam("nieuweNaam");
        assertThat(before).as("Ze moeten niet meer gelijk zijn").isNotNull().isNotEqualTo(beforeExpected);
        int actual = jdbcKlantDAOTest.updateKlant((Klant) before);
        int expected = 1;
        assertThat(actual).as("Er moet een rij aangepast zijn").isEqualTo(expected);
        Gebruiker after = jdbcKlantDAOTest.vindKlantByGebruikersnaam("Carmen");
        assertThat(((Klant) after).getNaam()).as("De nieuwe naam moet zijn opgeslagen").isEqualTo(((Klant) before).getNaam());
    }
    @Test
    void updateKlantAdres() {
        Gebruiker before = jdbcKlantDAOTest.vindKlantByGebruikersnaam("Carmen");
        Gebruiker beforeExpected = testKlant1;
        ((Klant)beforeExpected).setAdres(testAdres1);
        assertThat(before).as("Zeker weten dat de base case klopt").isNotNull().isEqualTo(beforeExpected);
        assertThat(((Klant) before).getAdres().getAdresId()).as("Zeker weten dat adres hetzelfde is").isNotNull().isEqualTo(((Klant) beforeExpected).getAdres().getAdresId());
        ((Klant) before).setAdres(testAdres2);
        assertThat(((Klant) before).getAdres().getAdresId()).as("AdresId moet nu niet dezelfde zijn")
                .isNotNull().isNotEqualTo(((Klant) beforeExpected).getAdres().getAdresId());
        int actual = jdbcKlantDAOTest.updateKlant((Klant) before);
        int expected = 1;
        assertThat(actual).as("Er moet een rij aangepast zijn").isEqualTo(expected);
        Gebruiker after = jdbcKlantDAOTest.vindKlantByGebruikersnaam("Carmen");
        assertThat(((Klant) before).getAdres().getAdresId()).as("AdresId moet nu aangepast zijn")
                .isNotNull().isEqualTo(((Klant) after).getAdres().getAdresId());
    }
}

