package com.example.thevault.service;

import com.example.thevault.klant.*;
import com.example.thevault.financieel.Rekening;
import com.example.thevault.financieel.RegistrationDto;
import com.example.thevault.financieel.RekeningService;
import com.example.thevault.support.hashing.BCryptWachtwoordHash;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

class RegistrationServiceTest {

    private static KlantService mockKlantService;
    private static AdresService mockAdresService;
    private static RekeningService mockRekeningService;
    private RegistrationService registrationService;
    private Klant testKlant;
    private Rekening testRekening;
    private Adres adres;

    @BeforeEach
    void setUp() {
    mockKlantService = Mockito.mock(KlantService.class);
    mockAdresService = Mockito.mock(AdresService.class);
    mockRekeningService = Mockito.mock(RekeningService.class);
    registrationService = new RegistrationService(mockKlantService, mockAdresService, mockRekeningService);
        String gehashtWachtwoord = BCryptWachtwoordHash.hashWachtwoord("testWWWWWWWWW");
        adres = new Adres("straat", 357, "C", "1000AA", "Amsterdam");
        testKlant = new Klant("testKlant", gehashtWachtwoord,
                "Jan", 145801354, LocalDate.now().minusYears(18));
        testKlant.setAdres(adres);
        testRekening = new Rekening("NL20INGB0006582287", 0.0);
    }



    @Test
    void registreerKlant() {
    Mockito.when(mockKlantService.registreerKlant(testKlant)).thenReturn(testKlant);
    Mockito.when(mockRekeningService.creeerRekening(testKlant)).thenReturn(testRekening);
    Mockito.when(mockAdresService.postcodeOpmaak(adres.getPostcode())).thenReturn(true);
    RegistrationDto actual = registrationService.registreerKlant(testKlant);
    RegistrationDto expected = new RegistrationDto(testKlant);
    assertThat(actual).isNotNull().isEqualTo(expected);

    }
}