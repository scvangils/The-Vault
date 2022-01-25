// Created by carme
// Creation date 01/12/2021

package com.example.thevault.klant;


import com.example.thevault.financieel.Cryptomunt;
import com.example.thevault.handelingen.RootRepositoryHandelingen;

import com.example.thevault.financieel.CryptoDto;
import com.example.thevault.financieel.Asset;
import com.example.thevault.financieel.RootRepositoryFinancieel;
import com.example.thevault.support.BSNvalidator;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Deze class handelt zaken af die met klanten te maken hebben
 * en zorgt dat deze in de database opgeslagen worden
 */

@Service
public class KlantService{

    private final RootRepositoryHandelingen rootRepositoryHandelingen;
    private final RootRepositoryKlant rootRepositoryKlant;
    private final RootRepositoryFinancieel rootRepositoryFinancieel;

    private final Logger logger = LoggerFactory.getLogger(KlantService.class);
    public final static int VOLWASSEN_LEEFTIJD = 18;
    public final static int MINIMALE_WACHTWOORDLENGTE = 8;

    /**
     * De constructor van de KlantService class voor Spring Boot
     * met dependency injection
     *
     * @param rootRepositoryHandelingen De class die als facade voor de database dient
     */
    @Autowired
    public KlantService(RootRepositoryHandelingen rootRepositoryHandelingen,
                        RootRepositoryKlant rootRepositoryKlant, RootRepositoryFinancieel rootRepositoryFinancieel) {
        super();
        this.rootRepositoryHandelingen = rootRepositoryHandelingen;
        this.rootRepositoryKlant = rootRepositoryKlant;
        this.rootRepositoryFinancieel = rootRepositoryFinancieel;
        logger.info("New KlantService.");
    }

    /**
     * Deze methode zoekt of er in de database al een klant bestaat met deze gebruikersnaam
     * en maakt eventueel een klant-object aan op nasis van de teruggestuurde gegevens
     *
     * @param gebruikersnaam gebruikersnaam van een (mogelijke) klant die uniek moet zijn
     * @return klant-object op basis van gegevens uit de database of null indien gebruikersnaam niet gevonden is
     */
    public Klant vindKlantByGebruikersnaam(String gebruikersnaam){
        return rootRepositoryKlant.vindKlantByGebruikersnaam(gebruikersnaam);
        //TODO de methode 'geefNuttigePortefeuille' aanroepen om de asset objecten voor de klant op te schonen (zie onder)
    }

    /**
     * Deze methode zoekt of er in de database al een klant bestaat met deze gebruikerId
     * en maakt eventueel een klant-object aan op basis van de teruggestuurde gegevens
     *
     * @param gebruikerId gebruikerId van een (mogelijke) klant die uniek moet zijn
     * @return klant-object op basis van gegevens uit de database of null indien gebruikerId niet gevonden is
     */
    public Klant vindKlantById(int gebruikerId){
        return rootRepositoryKlant.vindKlantById(gebruikerId);
    }

    /**
     * Author: Carmen
     * In de portefeuille van de klant worden de assets vervangen door AssetDTO objecten, waarbij alleen de
     * voor de klant nuttige informatie wordt doorgegeven (deze methode stond eerst in AssetService)
     * @param klant de klant die de portefeuille oproept
     * @return List</AssetDto> een lijst met alle assets van de klant, zijnde de portefeuille, in de vorm die voor de
     * klant meerwaarde heeft
     */

    public List<CryptoDto> geefNuttigePortefeuille(Klant klant){
        List<CryptoDto> portefeuilleVoorKlant = new ArrayList<>();
        for (Cryptomunt cryptomunt : rootRepositoryFinancieel.geefAlleCryptomunten()) {
            String naam = cryptomunt.getName();
            String afkorting = cryptomunt.getName();
            double prijs = rootRepositoryHandelingen.haalMeestRecenteCryptoWaarde(cryptomunt).getWaarde();
            double aantal = rootRepositoryHandelingen.geefAssetVanGebruikerOrElseNull(klant, cryptomunt);
            portefeuilleVoorKlant.add(new CryptoDto(naam, afkorting, prijs,aantal));
        }
        return portefeuilleVoorKlant;
    }

    /**
     * Methode die zoekt naar een cryptomunt in de portefeuille van een klant
     * en de asset daarvan teruggeeft.
     *
     * @param gebruiker van de portefeuille
     * @param cryptomunt waarnaar gezocht wordt
     *
     * @return asset met cryptomunt + aantal van gebruiker of null
     */
    public Asset geefAssetMetCryptoMuntVanGebruiker(Gebruiker gebruiker, Cryptomunt cryptomunt){
        List<Asset> portefeuille = rootRepositoryKlant.vulPortefeuilleKlant(gebruiker);
        if(portefeuille != null){
            for (Asset asset: portefeuille) {
                if(asset.getCryptomunt().equals(cryptomunt)){
                    return asset;
                }
            }
        }
        return null;
    }

    /**
     * Deze methode probeert een nieuwe klant te registreren.
     * Als de gegevens correct zijn ingevuld en de gebruikersnaam nog niet bestaat,
     * wordt het wachtwoord eerst gehasht en daarna gecodeerd.
     * Vervolgens wordt de klant opgeslagen in de database.
     *
     * @param klant een Klant-object is wordt aangemaakt op basis van ingevoerde gegevens
     * @return het klant-object met het gealtereerde wachtwoord
     */
    public Klant registreerKlant(Klant klant){
        bsnExceptionHandler(klant);
        wachtWoordExceptionHandler(klant);
        minimumLeeftijdExceptionHandler(klant);
        gebruikersnaamExceptionHandler(klant);
        String teHashenWachtwoord = klant.getWachtwoord();
        String gehashtWachtwoord = BCryptWachtwoordHash.hashWachtwoord(teHashenWachtwoord); // hash wachtwoord
        gehashtWachtwoord = Base64.encodeBase64String(gehashtWachtwoord.getBytes(StandardCharsets.UTF_8)); // versleutel gehasht wachtwoord
        klant.setWachtwoord(gehashtWachtwoord);
        rootRepositoryKlant.slaKlantOp(klant);
        return klant;
    }

    /**
     * Deze methode zorgt ervoor dat bij een al bestaande gebruikersnaam de juiste exception wordt aangeroepen
     *
     * @param klant de ingevoerde klantgegevens in objectvorm
     */
    private void gebruikersnaamExceptionHandler(Klant klant) {
        if(vindKlantByGebruikersnaam(klant.getGebruikersnaam()) != null){
            throw new RegistrationFailedException();
        }
    }

    /**
     * Deze methode zorgt ervoor dat bij een te lage leeftijd de juiste exception wordt aangeroepen
     *
     * @param klant de ingevoerde klantgegevens in objectvorm
     */
    private void minimumLeeftijdExceptionHandler(Klant klant) {
        if(!checkVolwassen(klant)){
            String message = String.format("Je moet %d zijn om een rekening te openen.", VOLWASSEN_LEEFTIJD);
            throw new AgeTooLowException(message);
        }
    }

    /**
     * Deze methode zorgt ervoor dat wanneer het wachtwoord niet aan de eisen voldoet,
     * de juiste exception wordt aangeroepen
     *
     * @param klant de ingevoerde klantgegevens in objectvorm
     */
    private void wachtWoordExceptionHandler(Klant klant) {
        if(!checkWachtwoordLengte(klant) || !checkWachtwoordFormat(klant)){
            String message = String.format("Het wachtwoord moet minimaal %d karakters lang zijn" +
                    " en mag geen spaties bevatten", MINIMALE_WACHTWOORDLENGTE);
            throw new PasswordNotSuitableException(message);
        }
    }

    /**
     * Deze methode zorgt ervoor dat bij een niet correct ingevoerde BSN de juiste exception wordt aangeroepen
     *
     * @param klant de ingevoerde klantgegevens in objectvorm
     */
    private void bsnExceptionHandler(Klant klant) {
        if(!BSNvalidator.bsnValideren(klant.getBsn())){
            throw new IncorrectBSNException();
        }
    }

    /**
     * Deze methode kijkt of de ingevulde geboortedatum van de klant
     * minimaal 18 jaar in het verleden ligt.
     *
     * @param klant de ingevoerde klantgegevens in objectvorm
     * @return een boolean die aangeeft of de klant volwassen is
     */
    public boolean checkVolwassen(Klant klant){ // 1 dag correctie nodig voor groter / gelijk
         return LocalDate.now().minusYears(VOLWASSEN_LEEFTIJD).plusDays(1).isAfter(klant.getGeboortedatum());
    }

    /**
     * Deze methode gaat na of het wachtwoord aan de minimumeis qua lengte voldoet
     *
     * @param klant de ingevoerde klantgegevens in objectvorm
     * @return een boolean die aangeeft of het wachtwoord lang genoeg is
     */
    public boolean checkWachtwoordLengte(Klant klant){
        return klant.getWachtwoord().length() >= MINIMALE_WACHTWOORDLENGTE;
    }

    /**
     * Deze methode gaat na of het wachtwoord geen spaties bevat
     *
     * @param klant de ingevoerde klantgegevens in objectvorm
     * @return een boolean die aangeeft of het wachtwoord geen spaties bevat
     */
    public boolean checkWachtwoordFormat(Klant klant){
       return !klant.getWachtwoord().contains(" ");
    }
}
