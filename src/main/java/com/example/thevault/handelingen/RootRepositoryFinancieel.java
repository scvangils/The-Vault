// Created by carme
// Creation date 01/12/2021

package com.example.thevault.handelingen;

import com.example.thevault.financieel.*;
import com.example.thevault.klant.Gebruiker;
import net.minidev.json.annotate.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Deze klasse zorgt ervoor dat de incomplete objecten uit de DAO's volledig gemaakt kunnen worden
 * door de DAO's hier met elkaar te combineren
 */

@Repository
public class RootRepositoryFinancieel implements ApplicationListener<ContextRefreshedEvent> {

    @JsonIgnore
    private final Logger logger = LoggerFactory.getLogger(RootRepositoryFinancieel.class);
    public final RootRepositoryKlant rootRepositoryKlant;


    private final RekeningDAO rekeningDAO;
    private final AssetDAO assetDAO;
    private final CryptomuntDAO cryptomuntDAO;
    private final String KOPER = "Koper";
    private final String VERKOPER = "Verkoper";

    /**
     * Constructor voor RootRepositoryHandelingen
     * In de constructor worden alle DAOs geinjecteerd die de RootRepositoryHandelingen nodig heeft
     * @param rekeningDAO
     * @param assetDAO
     * @param cryptomuntDAO
     */
    @Autowired
    public RootRepositoryFinancieel(RekeningDAO rekeningDAO, AssetDAO assetDAO, CryptomuntDAO cryptomuntDAO,
                                    RootRepositoryKlant rootRepositoryKlant) {
        super();
        this.rekeningDAO = rekeningDAO;

        this.assetDAO = assetDAO;
        this.cryptomuntDAO = cryptomuntDAO;




        this.rootRepositoryKlant = rootRepositoryKlant;
        logger.info("New RootRepositoryHandelingen");
    }




    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

    }

    /**
    * Author: Ju-Sen Cheung
    * Methode die op gebruiker zoekt en de rekening teruggeeft
    * @param gebruiker kan zowel een klant als een bank zijn
    * @return rekening geeft een rekening object terug
    * */
    public Rekening vindRekeningVanGebruiker(Gebruiker gebruiker){
        return rekeningDAO.vindRekeningVanGebruiker(gebruiker);
    }

    /**
     * Author: Ju-Sen Cheung
     * Deze methode geeft het rekeningsaldo op van de gebruiker. Het saldo wordt via de methode in de rekeningDAO uit
     * de database gehaald.
     * @param gebruiker is de gebruiker van wie het rekeningsaldo wordt opgevraagd.
     * @return het rekeningsaldo behorende bij de gebruiker.
     */
    public double vraagSaldoOpVanGebruiker(Gebruiker gebruiker){
        return rekeningDAO.vraagSaldoOpVanGebruiker(gebruiker);
    }

    /**
     * Author: Ju-Sen Cheung
     * Deze methode wijzigt het rekeningsaldo van de klant in de database via de methode in de rekeningDAO.
     * @param gebruiker is de gebruiker van wie het rekeningsaldo wordt opgevraagd.
     * @param transactiebedrag is het bedrag waarmee het saldo van de rekening verhoogd of verlaagd moet worden.
     * @return rekening met geüpdatete saldo.
     */
    public Rekening wijzigSaldoVanGebruiker(Gebruiker gebruiker, double transactiebedrag){
        return rekeningDAO.wijzigSaldoVanGebruiker(gebruiker, transactiebedrag);
    }

    /**
     * Author: Carmen
     * Dit betreft het toevoegen van een cryptomunt die nog niet in de portefeuille zit
     * Dit gebeurt via een 'transactie', waarbij een klant crypto's koopt
     * @param asset de cryptomunt en het aantal dat de klant aanschaft
     * @return Asset de asset die de klant heeft toegevoegd
     */
    public Asset slaNieuwAssetVanKlantOp(Asset asset){
        return assetDAO.voegNieuwAssetToeAanPortefeuille(asset);
    }

    /**
     * Author: Carmen
     * Dit betreft het wijzigen van een cryptomunt die al in de portefeuille zit
     * Dit gebeurt via een 'transactie', waarbij een klant crypto's koopt of verkoopt
     * @param gebruiker de handelende partij
     * @param cryptomunt de munt waarin gehandeld wordt
     * @param aantal de hoeveelheid die verhandeld wordt
     * @return Asset de asset na de update, waarbij het nieuwe aantal wordt meegegeven
     */
    public Asset wijzigAssetVanKlant(Gebruiker gebruiker, Cryptomunt cryptomunt, double aantal){
        return assetDAO.updateAsset(gebruiker, cryptomunt , aantal);
    }

    //TODO JavaDoc
    public Cryptomunt geefCryptomuntByNaam(String cryptoNaam){
        return cryptomuntDAO.geefCryptomuntByNaam(cryptoNaam);
    }

    //TODO JavaDoc
    public Cryptomunt geefCryptomunt(int cryptomuntId){
        return cryptomuntDAO.geefCryptomunt(cryptomuntId);
    }

    //TODO JavaDoc
    public List<Cryptomunt> geefAlleCryptomunten(){
        return cryptomuntDAO.geefAlleCryptomunten();
    }
}
