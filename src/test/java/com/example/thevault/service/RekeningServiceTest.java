package com.example.thevault.service;

import com.example.thevault.handelingen.RootRepository;
import com.example.thevault.klant.Klant;
import com.example.thevault.financieel.Rekening;
import com.example.thevault.financieel.RekeningService;
import com.example.thevault.support.exceptions.UserNotExistsException;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class RekeningServiceTest {
    public static RootRepository mockRepo;
    public static RekeningService rekeningServiceTest;
    public static Klant bestaandeKlant;
    public static Klant nietBestaandeKlant;
    public static Klant nieuweKlant;
    public static Klant klantMetPlusbedragTransactie;
    public static Klant klantMetMinbedragTransactie;
    public static Rekening rekeningBestaandeKlant;
    public static Rekening nieuweRekening;
    public static Rekening rekeningVoorklantMetPlusbedragwijziging;
    public static Rekening rekeningVoorklantMetMinbedragwijziging;


    @BeforeAll
    static void setUp() {
        mockRepo = Mockito.mock(RootRepository.class);
        rekeningServiceTest = new RekeningService(mockRepo);
        bestaandeKlant = new Klant( "Henknr1", "fdsaljkl", "Hello", 1890393, LocalDate.of(1991, 1, 12));
        nietBestaandeKlant = new Klant( "HarryBeste", "210jklf", "", 101212, LocalDate.of(1991, 1, 12));
        nieuweKlant = new Klant( "ThomasBeste", "831hgtr", "", 1528719, LocalDate.of(1990, 5, 10));
        klantMetPlusbedragTransactie = new Klant("MarkSlegte", "456jesv", "", 5248136, LocalDate.of(1987, 2, 16));
        klantMetMinbedragTransactie = new Klant("Jo", "184tyj", "", 123698745, LocalDate.of(1990, 6, 18));
        rekeningBestaandeKlant = new Rekening("INGB0001234567NL", 1000.0);
        bestaandeKlant.setRekening(rekeningBestaandeKlant);
        rekeningBestaandeKlant.setGebruiker(bestaandeKlant);
        nieuweRekening = new Rekening("NL20RABO9876543", 1000.0);
        rekeningVoorklantMetPlusbedragwijziging = new Rekening("INGB0001234567NL", 3000.0);
        klantMetPlusbedragTransactie.setRekening(rekeningVoorklantMetPlusbedragwijziging);
        rekeningVoorklantMetPlusbedragwijziging.setGebruiker(klantMetPlusbedragTransactie);
        rekeningVoorklantMetMinbedragwijziging = new Rekening("INGB0007654321NL", 800.0);
        klantMetMinbedragTransactie.setRekening(rekeningVoorklantMetMinbedragwijziging);
        rekeningVoorklantMetMinbedragwijziging.setGebruiker(klantMetMinbedragTransactie);
    }

    /**
     * Author Wim Bultman
     */
    @Test
    void createIban() {
        Iban iban = new Iban.Builder().countryCode(CountryCode.NL).bankCode("TVLT").buildRandom();
        assertThat(iban.toString()).contains("NL");
    }

    /**
     * Author Ju-Sen Cheung
     * Er kan geen nieuweRekening object(regel 39) aan deze test gekoppeld worden, omdat er iedere keer een nieuwe iban
     * wordt gegenereerd. Het startsaldo van een nieuwe rekening is € 1000,-. De klant wordt in de methode creeerRekening
     * meegegeven/vastgezet.
     */
    @Test
    void creeerRekening() {
        Rekening actual = rekeningServiceTest.creeerRekening(nieuweKlant);
        System.out.println(actual);
        assertThat(actual.getIban()).isNotNull();
        assertThat(actual.getSaldo()).isEqualTo(1000.0);
        Rekening expected = nieuweKlant.getRekening();
        System.out.println(expected);
        assertThat(actual.getGebruiker().getGebruikersnaam()).isEqualTo("ThomasBeste");
        assertThat(actual.getGebruiker()).isEqualTo(nieuweKlant);
        assertThat(actual.getGebruiker()).isNotEqualTo(nietBestaandeKlant);
        assertThat(actual.getGebruiker()).isNotEqualTo(bestaandeKlant);
        assertThat(actual.getGebruiker().getGebruikersnaam()).isNotEqualTo("AlsDezeNaamWordtGoedGekeurdDanKloptDeTestNiet");
    }

    /**
     * Author Ju-Sen Cheung
     */
    @Test
    void slaRekeningOp() {
        Mockito.when(mockRepo.slaRekeningOp(nieuweRekening)).thenReturn(nieuweRekening);

        Rekening actual = rekeningServiceTest.slaRekeningOp(nieuweRekening);
        System.out.println(actual);
        Rekening expected = nieuweRekening;
        assertThat(actual).isNotNull().isEqualTo(expected);
    }

    /**
     * Author Ju-Sen Cheung
     */
    @Test
    void vindRekeningVanKlant() {
        Mockito.when(mockRepo.vindKlantByGebruikersnaam(bestaandeKlant.getGebruikersnaam())).thenReturn(bestaandeKlant);
        Mockito.when(mockRepo.vindRekeningVanGebruiker(bestaandeKlant)).thenReturn(rekeningBestaandeKlant);

        Rekening actual = rekeningServiceTest.vindRekening(bestaandeKlant);
        System.out.println(actual);
        Rekening expected = rekeningBestaandeKlant;
        assertThat(actual).isNotNull().isEqualTo(expected);
    }

    /**
     * Author Ju-Sen Cheung
     */
    @Test
    void vindRekeningVanNietBestaandeKlant() {
        Mockito.when(mockRepo.vindKlantByGebruikersnaam(bestaandeKlant.getGebruikersnaam())).thenReturn(bestaandeKlant);
        Mockito.when(mockRepo.vindRekeningVanGebruiker(bestaandeKlant)).thenReturn(rekeningBestaandeKlant);

        try{
            rekeningServiceTest.vraagSaldoOp(nietBestaandeKlant);
            fail("Moet een UserNotExistsException gooien");
        } catch (UserNotExistsException expected){
            System.out.println("Test geslaagd!");
        }
    }

    /**
     * Author Elise Olthof
     */
    @Test
    void vraagSaldoOp() {
        Mockito.when(mockRepo.vindKlantByGebruikersnaam(bestaandeKlant.getGebruikersnaam())).thenReturn(bestaandeKlant);
        Mockito.when(mockRepo.vindRekeningVanGebruiker(bestaandeKlant)).thenReturn(rekeningBestaandeKlant);
        Mockito.when(mockRepo.vraagSaldoOpVanGebruiker(bestaandeKlant)).thenReturn(bestaandeKlant.getRekening().getSaldo());

        double saldoActual = rekeningServiceTest.vraagSaldoOp(bestaandeKlant);
        System.out.println(saldoActual);
        double expected = 1000.0;
        assertThat(saldoActual).isEqualTo(expected);
    }

    /**
     * Author Elise Olthof
     */
    @Test
    void vraagSaldoOpVanNietBestaandeKlant() {
        Mockito.when(mockRepo.vindKlantByGebruikersnaam(bestaandeKlant.getGebruikersnaam())).thenReturn(bestaandeKlant);
        Mockito.when(mockRepo.vindRekeningVanGebruiker(bestaandeKlant)).thenReturn(rekeningBestaandeKlant);
        Mockito.when(mockRepo.vraagSaldoOpVanGebruiker(bestaandeKlant)).thenReturn(bestaandeKlant.getRekening().getSaldo());

        try{
            rekeningServiceTest.vraagSaldoOp(nietBestaandeKlant);
            fail("Moet een UserNotExistsException gooien");
        } catch (UserNotExistsException expected){
            System.out.println("Test geslaagd!");
        }
    }

    /**
     * Author Ju-Sen Cheung
     */
    @Test
    void wijzigSaldo() {
        Mockito.when(mockRepo.wijzigSaldoVanGebruiker(bestaandeKlant, 2000.0)).thenReturn(rekeningVoorklantMetPlusbedragwijziging);

        Rekening rekeningActual = rekeningServiceTest.wijzigSaldo(bestaandeKlant, 2000.0);
        double expected = 3000.0;
        double actual = rekeningActual.getSaldo();
        assertThat(actual).isEqualTo(expected);
    }

    /**
     * Author Ju-Sen Cheung
     */
    @Test
    void wijzigSaldo2() {
        Mockito.when(mockRepo.wijzigSaldoVanGebruiker(bestaandeKlant, -200.0)).thenReturn(rekeningVoorklantMetMinbedragwijziging);

        Rekening rekeningVoorklantMetMinbedragwijziging = rekeningServiceTest.wijzigSaldo(bestaandeKlant, -200.0);
        double expected = 800.0;
        double actual = rekeningVoorklantMetMinbedragwijziging.getSaldo();
        assertThat(actual).isEqualTo(expected);
    }

    // TODO author toevoegen

    @Test
    void wijzigSaldoInGebruikerRekeningJava(){
        Mockito.when(mockRepo.wijzigSaldoVanGebruiker(bestaandeKlant, 2000.0)).thenReturn(rekeningVoorklantMetPlusbedragwijziging);

        Rekening rekening = rekeningServiceTest.wijzigSaldo(bestaandeKlant, 2000.0);
        double expected = 3000.0;
        bestaandeKlant.setRekening(rekening);
        double actual = bestaandeKlant.getRekening().getSaldo();
        assertThat(actual).isEqualTo(expected);
    }
}
