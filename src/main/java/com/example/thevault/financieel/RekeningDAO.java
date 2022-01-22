package com.example.thevault.financieel;

import com.example.thevault.klant.Gebruiker;

/**
 * Author: Ju-Sen Cheung
 * Beschrijving: De DAO voor Rekening, waar enkele methodes instaan voor de CRUD-functionaliteiten van Rekening en het
 * vinden van een rekening en het opvragen van het saldo op een rekening.
 */

public interface RekeningDAO {

    /**
     * Met deze methode wordt er een rekening opgeslagen.
     * @param rekening is de rekening die is aangemaakt bij het registreren van de gebruiker.
     * @return rekening die is opgeslagen na registratie van een gebruiker.
     */
    Rekening slaRekeningOp(Rekening rekening);

    /**
     * Met deze methode kan je een rekening terugvinden als je de gebruiker meegeeft.
     * @param gebruiker is de gerbuiker van wie de rekening wordt opgevraagd.
     * @return rekening waarvan je de iban en het saldo kunt zien van de gebruiker.
     */
    Rekening vindRekeningVanGebruiker(Gebruiker gebruiker);

    /**
     * Met deze methode kan je het saldo van de rekening opvragen als je de gebruiker meegeeft.
     * @param gebruiker is de gebruiker van wie het rekeningsaldo wordt opgevraagd.
     * @return het saldo van de rekening.
     */
    double vraagSaldoOpVanGebruiker(Gebruiker gebruiker);

    /**
     * Met deze methode kan je het saldo van de rekening updaten als je de gebruiker en het transactiebedrag meegeeft.
     * Het wijzigen van het saldo gebeurt doordat je een cryptomunt koopt of verkoopt via een transactie.
     * @param gebruiker is de gebruiker bij wie een transactie plaatsvindt.
     * @param transactiebedrag is het bedrag dat bij het rekeningsaldo opgeteld of afgetrokken wordt.
     * @return Als er voldoende saldo is voor de transactie, dan wordt het saldo geüpdatet. Zo niet, dan komt er een
     * bericht dat het saldo niet toereikend is.
     */
    double updateSaldo(Gebruiker gebruiker, double transactiebedrag);

     /**
     * Met deze methode kan je het nieuwe saldo na een transactie in de rekening opslaan.
     * @param gebruiker is de gebruiker van wie het saldo geüpdatet moet worden.
     * @param transactiebedrag is het bedrag waarmee het saldo van de rekening verhoogd of verlaagd moet worden.
     * @return geüpdatete saldo.
     */
    Rekening wijzigSaldoVanGebruiker(Gebruiker gebruiker, double transactiebedrag);


}
