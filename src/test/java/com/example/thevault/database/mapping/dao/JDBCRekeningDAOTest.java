package com.example.thevault.database.mapping.dao;

import com.example.thevault.klant.Klant;
import com.example.thevault.financieel.JDBCRekeningDAO;
import com.example.thevault.financieel.Rekening;
import com.example.thevault.support.BSNvalidator;
import com.example.thevault.support.exceptions.BalanceTooLowException;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class JDBCRekeningDAOTest {
    private final JDBCRekeningDAO jdbcRekeningDAO;
    public Klant bestaandeKlant1;
    public Klant bestaandeKlant2;
    public Klant nietBestaandeKlant;
    public Rekening bestaandeRekening1;
    public Rekening bestaandeRekening2;
    public Rekening nieuweRekening;

    @Autowired
    public JDBCRekeningDAOTest(JDBCRekeningDAO jdbcRekeningDAO) {
        super();
        this.jdbcRekeningDAO = jdbcRekeningDAO;
    }

    @Test
    void testTheTest(){
        assertThat(jdbcRekeningDAO).isNotNull();
    }

    @BeforeEach
    void setUp() {
        bestaandeKlant1 = new Klant("Carmen", "GoedWachtwoord",
                "Carmen", 123456789, LocalDate.parse("1985-12-30"));
        bestaandeKlant2 = new Klant("Jolien", "BeterWachtwoord",
                "Jolien", 987654321, LocalDate.parse("1985-10-14"));
        nietBestaandeKlant = new Klant("Charlotte", "SlechtWW",
                "Charlotte", BSNvalidator.TESTBSN_VAN_RIVG, LocalDate.parse("1997-08-01"));
        bestaandeRekening1 = new Rekening("NL01INGB0056210575", 41908);
        bestaandeRekening2 = new Rekening("NL05RABO0957285205", 57313);
        nieuweRekening = new Rekening("NL20RABO9876543", 1000.0);
        bestaandeKlant1.setGebruikerId(1);
        bestaandeKlant2.setGebruikerId(2);
        bestaandeRekening1.setGebruiker(bestaandeKlant1);
        bestaandeRekening2.setGebruiker(bestaandeKlant2);
    }

    /**
     * Bij de registratie wordt eerst de nieuwe klant opgeslagen en dan pas wordt er een rekening gecreëerd en opgeslagen
     * Er moet dus een al geregistreerde klant uit de database meegegeven worden om een nieuwe rekening aan mee te kunnen
     * geven voor deze methode.
     */
    @Test
    void slaRekeningOp() {
        nieuweRekening.setGebruiker(bestaandeKlant1);
        Rekening actual = jdbcRekeningDAO.slaRekeningOp(nieuweRekening);
        Rekening expected = nieuweRekening;

        assertThat(actual).as("Test van slaRekeningOp()")
                .isNotNull()
                .isEqualTo(expected)
                .isSameAs(nieuweRekening);
    }

    /**
     * In plaats van de hele rekening (inclusief Gebruiker) worden alleen iban en saldo vergeleken, omdat de
     * RekeningRowMapper alleen deze twee waarden teruggeeft vanuit de database.
     */
    @Test
    void vindRekeningVanGebruiker() {
        Rekening actual = jdbcRekeningDAO.vindRekeningVanGebruiker(bestaandeKlant2);
        Rekening expected = bestaandeRekening2;

        assertThat(actual.getIban()).isEqualTo(expected.getIban());
        assertThat(actual.getSaldo()).isEqualTo(expected.getSaldo());
    }

    @Test
    void vindRekeningVanGebruiker2() {
        Rekening actual = jdbcRekeningDAO.vindRekeningVanGebruiker(nietBestaandeKlant);
        Rekening expected = null;

        assertThat(actual).isEqualTo(expected).isNull();
    }

    @Test
    void vraagSaldoOpVanGebruiker() {
        double actualSaldo = jdbcRekeningDAO.vraagSaldoOpVanGebruiker(bestaandeKlant1);
        double expectedSaldo = bestaandeRekening1.getSaldo();

        assertThat(actualSaldo).as("Test van vraagSaldoOpVanGebruiker()")
                .isNotNull()
                .isBetween(41905.0, 41910.0)
                .isEqualTo(expectedSaldo)
                .isNotEqualTo(bestaandeRekening2.getSaldo())
                .isNotNegative()
                .isCloseTo(41908, Percentage.withPercentage(0.01))
                .isNotCloseTo(41915, Percentage.withPercentage(0.01));
    }

    @Test
    void vraagSaldoOpVanGebruiker2() {
        double actual = jdbcRekeningDAO.vraagSaldoOpVanGebruiker(nietBestaandeKlant);
        System.out.println(actual);
        double expected = -1;
        System.out.println(expected);

        assertThat(actual).as("Deze klant bestaat niet en daarom kan er geen saldo teruggegeven worden.")
                .isEqualTo(expected)
                .isNegative();
    }

    @Test
    void updateSaldo() {
        double actualNieuwSaldo = jdbcRekeningDAO.updateSaldo(bestaandeKlant2, 5000.00);
        double expectedNieuwSaldo = bestaandeRekening2.getSaldo() + 5000.00;

        assertThat(actualNieuwSaldo).as("Test van updateSaldo() met plusbedrag.")
                .isEqualTo(expectedNieuwSaldo)
                .isEqualTo(62313.00)
                .isBetween(62312.00, 62314.00);
    }

    @Test
    void updateSaldo2() {
        double actualNieuwSaldo = jdbcRekeningDAO.updateSaldo(bestaandeKlant1, -20000.00);
        double expectedNieuwSaldo = bestaandeRekening1.getSaldo() - 20000.00;

        assertThat(actualNieuwSaldo).as("Test van updateSaldo2() met minbedrag.")
                .isEqualTo(expectedNieuwSaldo)
                .isEqualTo(21908.00);
    }

    @Test
    void updateSaldo3() {
        try{
            jdbcRekeningDAO.updateSaldo(bestaandeKlant1, -50000.00);
            fail("Moet een BalanceTooLowException gooien");
        } catch (BalanceTooLowException expected){
            System.out.println("Test geslaagd!");
        }
    }

    @Test
    void wijzigSaldoVanGebruiker() {
        Rekening actual = jdbcRekeningDAO.wijzigSaldoVanGebruiker(bestaandeKlant2,30000.00);
        Rekening expected = bestaandeRekening2.getGebruiker().getRekening();

        assertThat(actual).as("Test van wijzigSaldoVanGebruiker() met plusbedrag")
                .isEqualTo(expected)
                .hasToString("Rekening{, iban='NL05RABO0957285205', saldo=87313.0}");
    }

    @Test
    void wijzigSaldoVanGebruiker2() {
        Rekening actual = jdbcRekeningDAO.wijzigSaldoVanGebruiker(bestaandeKlant1,-11908.00);
        Rekening expected = bestaandeRekening1.getGebruiker().getRekening();

        assertThat(actual).as("Test van wijzigSaldoVanGebruiker2() met minbedrag")
                .isEqualTo(expected)
                .hasToString("Rekening{, iban='NL01INGB0056210575', saldo=30000.0}");
    }
}
