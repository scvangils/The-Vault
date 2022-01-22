// Created by E.S. Olthof
// Creation date 15-12-2020

package com.example.thevault.handelingen;

import com.example.thevault.financieel.Cryptomunt;
import com.example.thevault.klant.Gebruiker;
import com.example.thevault.klant.KlantService;
import com.example.thevault.financieel.AssetService;
import com.example.thevault.financieel.RekeningService;
import com.example.thevault.financieel.Asset;
import net.minidev.json.annotate.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TransactieService {

    private final RootRepository rootRepository;
    private final KlantService klantService;
    private final RekeningService rekeningService;
    private final AssetService assetService;
    public final double TRANSACTION_FEE = Bank.getInstance().getFee();
    public final double DEEL_TRANSACTION_FEE_KOPER = 0.5;
    public final double DEEL_TRANSACTION_FEE_VERKOPER = 1 - DEEL_TRANSACTION_FEE_KOPER;

    @JsonIgnore
    private final Logger logger = LoggerFactory.getLogger(TransactieService.class);

    //TODO JavaDoc
    @Autowired
    public TransactieService(RootRepository rootRepository, KlantService klantService, RekeningService rekeningService,
                             AssetService assetService) {
        super();
        this.rootRepository = rootRepository;
        this.klantService = klantService;
        this.rekeningService = rekeningService;

        this.assetService = assetService;
        logger.info("Nieuwe TransactieService.");
    }

    /**
     * * Maakt een transactie op basis van het bod en de vraagprijs van twee partijen.
     * Als de koper niet genoeg saldo heeft of wanneer de verkoper niet het aantal cryptomunten
     * heeft dat deze wilt verkopen geeft de methode een foutmelding
     *
     * @param datumEnTijd het moment van de transactie
     * @param koopTrigger de gewenste transactie van de koper
     * @param verkoopTrigger de gewenste transactie van de verkoper
     * @return De uiteindelijke transactie
     */
            public Transactie sluitTransactie (LocalDateTime datumEnTijd, Trigger koopTrigger, Trigger verkoopTrigger ){
            Gebruiker koper = koopTrigger.getGebruiker();
            Gebruiker verkoper = verkoopTrigger.getGebruiker();
            double aantal = koopTrigger.getAantal();
                boolean bankIsKoper = (koper instanceof Bank);
            boolean bankIsVerkoper = (verkoper instanceof Bank);

            double prijs = Transactie.getPrijsViaTrigger(koopTrigger, verkoopTrigger);
            double transactieBedragKoper = getTransactieBedragKoper(aantal, bankIsKoper, bankIsVerkoper, prijs);
            double transactieBedragVerkoper = getTransactieBedragVerkoper(aantal, bankIsKoper, bankIsVerkoper, prijs);

            checkTransactionExceptions(verkoopTrigger, koopTrigger, transactieBedragKoper);
        Transactie transactie = new Transactie(datumEnTijd, koopTrigger, verkoopTrigger);
        slaAlleAspectenVanTransactieOp(transactie, transactieBedragKoper, transactieBedragVerkoper);

        return transactie;
    }

    /**
     * Deze methode bepaalt het bedrag dat de verkoper krijgt voor de transactie
     *
     * @param aantal hoeveel er van de betreffende cryptomunt verhandeld wordt
     * @param bankIsKoper of de bank de koper is
     * @param bankIsVerkoper of de bank de verkoper is
     * @param prijs de prijs per eenheid die voor deze cryptomunt in deze transactie wordt betaald
     * @return
     */
    private double getTransactieBedragVerkoper(double aantal, boolean bankIsKoper, boolean bankIsVerkoper, double prijs) {
        double transactieBedragVerkoper;
        transactieBedragVerkoper = (bankIsKoper || bankIsVerkoper) ? getBedragVerkoperMetBankAlsKoper(aantal, bankIsVerkoper, prijs):
        getBedragVerkoperBijKlantTransactie(aantal, prijs);
        return transactieBedragVerkoper;
    }

    /**
     * Deze methode bepaalt het bedrag dat de koper moet betalen voor de transactie
     *
     * @param aantal hoeveel er van de betreffende cryptomunt verhandeld wordt
     * @param bankIsKoper of de bank de koper is
     * @param bankIsVerkoper of de bank de verkoper is
     * @param prijs de prijs per eenheid die voor deze cryptomunt in deze transactie wordt betaald
     * @return
     */
    private double getTransactieBedragKoper(double aantal, boolean bankIsKoper, boolean bankIsVerkoper, double prijs) {
        double transactieBedragKoper;
        transactieBedragKoper = (bankIsKoper || bankIsVerkoper) ? getBedragKoperMetBankAlsVerkoper(aantal, bankIsKoper, prijs):
                getBedragKoperBijKlantTransactie(aantal, prijs);
        return transactieBedragKoper;
    }

    /**
     * Deze methode bepaalt het bedrag dat de verkoper krijgt voor de transactie als de bank geen partij is
     *
     * @param aantal hoeveel er van de betreffende cryptomunt verhandeld wordt
     * @param prijs de prijs per eenheid die voor deze cryptomunt in deze transactie wordt betaald
     * @return
     */
    private double getBedragVerkoperBijKlantTransactie(double aantal, double prijs) {
        double transactieBedragVerkoper;
        transactieBedragVerkoper = aantal * prijs - TRANSACTION_FEE * DEEL_TRANSACTION_FEE_VERKOPER;
        return transactieBedragVerkoper;
    }

    /**
     * Deze methode bepaalt het bedrag dat de koper moet betalen voor de transactie als de bank geen partij is
     *
     * @param aantal hoeveel er van de betreffende cryptomunt verhandeld wordt
     * @param prijs de prijs per eenheid die voor deze cryptomunt in deze transactie wordt betaald
     * @return
     */
    private double getBedragKoperBijKlantTransactie(double aantal, double prijs) {
        double transactieBedragKoper;
        transactieBedragKoper = aantal * prijs + TRANSACTION_FEE * DEEL_TRANSACTION_FEE_KOPER;
        return transactieBedragKoper;
    }

    /**
     * Deze methode bepaalt het bedrag dat de verkoper krijgt voor de transactie als de bank de koper is
     *
     * @param aantal hoeveel er van de betreffende cryptomunt verhandeld wordt
     * @param prijs de prijs per eenheid die voor deze cryptomunt in deze transactie wordt betaald
     * @return
     */
    private double getBedragVerkoperMetBankAlsKoper(double aantal, boolean bankIsVerkoper, double prijs) {
        double transactieBedragVerkoper;
        transactieBedragVerkoper = (bankIsVerkoper) ? aantal * prijs: aantal * prijs - TRANSACTION_FEE;
        return transactieBedragVerkoper;
    }

    /**
     * Deze methode bepaalt het bedrag dat de verkoper krijgt voor de transactie als de bank de verkoper is
     *
     * @param aantal hoeveel er van de betreffende cryptomunt verhandeld wordt
     * @param prijs de prijs per eenheid die voor deze cryptomunt in deze transactie wordt betaald
     * @return
     */
    private double getBedragKoperMetBankAlsVerkoper(double aantal, boolean bankIsKoper, double prijs) {
        double transactieBedragKoper;
        transactieBedragKoper = (bankIsKoper) ? aantal * prijs: aantal * prijs + TRANSACTION_FEE;
        return transactieBedragKoper;
    }

    //TODO JavaDoc?
    private void checkTransactionExceptions(Trigger verkoopTrigger, Trigger koopTrigger, double transactieBedragKoper) {
        Gebruiker koper = koopTrigger.getGebruiker();
        Gebruiker verkoper = verkoopTrigger.getGebruiker();
        Cryptomunt cryptomunt = koopTrigger.getCryptomunt();
        double aantal = koopTrigger.getAantal();
        saldoTooLowExceptionHandler(koper, transactieBedragKoper);
        notEnoughCryptoExceptionHandler(verkoper, cryptomunt, aantal);
    }

    /**
     * Deze methode zorgt ervoor dat de transactie opgeslagen wordt en alle relevante rekeningen en
     * portefeuilles worden bijgewerkt.
     * Door de annotatie @Transactional zorgt Spring Boot ervoor dat alle aspecten van deze transactie
     * moeten slagen, anders wordt niets uitgevoerd (Atomisch)
     * @param transactie De betreffende transactie
     * @param bedragKoper Het bedrag dat de koper moet betalen
     * @param bedragVerkoper Het bedrag dat de verkoper krijgt
     */
    @Transactional
    public void slaAlleAspectenVanTransactieOp(Transactie transactie,
                                        double bedragKoper, double bedragVerkoper) {
        slaTransactieOp(transactie);
        transactie.getKoper().setRekening(rekeningService.wijzigSaldo(transactie.getKoper(), -bedragKoper));
        transactie.getVerkoper().setRekening(rekeningService.wijzigSaldo(transactie.getVerkoper(), bedragVerkoper));
        rekeningService.wijzigSaldo(Bank.getInstance(), transactie.getBankFee());
        assetService.wijzigAssetGebruiker(transactie.getKoper(), transactie.getCryptomunt(), transactie.getAantal());
        assetService.wijzigAssetGebruiker(transactie.getVerkoper(), transactie.getCryptomunt(), -transactie.getAantal());

    }

    //TODO Wordt deze methode later gebruikt? Anders verwijderen?
    /**
     * Berekent de prijs wanneer direct met de bank gehandeld wordt door het aantal te verkopen of te kopen
     * cryptomunten met de huidige koerswaarde van de cryptomunt te vermenigvuldigen
     *
     * @param cryptomunt waarmee gehandeld zal worden
     * @return de prijs
    * */
    public double berekenPrijsTransactieMetBank(Cryptomunt cryptomunt, LocalDateTime datumEnTijd){
        return rootRepository.haalCryptoWaardeOpDatum(cryptomunt, datumEnTijd.toLocalDate()).getWaarde();
    }

    /**
     * Methode slaat transactie op
    *
     * @param transactie
     * @retrun transactie die zojuist is opgeslagen
    * */
    public Transactie slaTransactieOp(Transactie transactie){
        return rootRepository.slaTransactieOp(transactie);
    }

    /**
     * Checkt of de verkoper genoeg cryptomunten heeft om de transactie
     * te kunnen sluiten
     *
     * @param verkoper klant-object verkoper
     * @param cryptomunt Cryptomunt-object
     * @param aantal double hoeveelheid cryptomunt
     * @throws NotEnoughCryptoException exception die een NOT_ACCEPTABLE http status teruggeeft
     * */
    public void notEnoughCryptoExceptionHandler (Gebruiker verkoper, Cryptomunt cryptomunt, double aantal) throws NotEnoughCryptoException {
        Asset assetVerkoper = klantService.geefAssetMetCryptoMuntVanGebruiker(verkoper, cryptomunt);
        if(assetVerkoper == null || assetVerkoper.getAantal() - aantal  < 0) {
            logger.info("Te weinig cryptomunten in bezit van verkoper voor deze transactie");
            throw new NotEnoughCryptoException();
        }
    }

    /**
     * Controleert of het saldo van de koper toereikend is voor de
     * transactie
     *
     * @param koper van de cryptomunt
     * @param bedrag verkoopbedrag van cryptomunt
     * @throws BalanceTooLowException exception die een NOT_ACCEPTABLE hhtp status teruggeeft
    * */
    public void saldoTooLowExceptionHandler(Gebruiker koper, double bedrag) throws BalanceTooLowException  {
        double saldoGebruiker = rootRepository.vindRekeningVanGebruiker(koper).getSaldo();
        System.out.println("**** Het saldo van de koper is: " + saldoGebruiker);
        System.out.println("**** Het bedrag is: " + bedrag);
        if((saldoGebruiker - bedrag) < 0 ) {
            logger.info("Saldo koper te laag voor deze transactie.");
            throw new BalanceTooLowException();
        }
    }

    //TODO JavaDoc
    public Trigger maakBankTrigger(Trigger klantTrigger){
        double prijs = rootRepository.haalMeestRecenteCryptoWaarde(klantTrigger.getCryptomunt()).getWaarde();
        double aantal = klantTrigger.getAantal();
        Cryptomunt cryptomunt = klantTrigger.getCryptomunt();
        if(TriggerService.isKoper(klantTrigger)){
            return new TriggerVerkoper(Bank.getInstance(), cryptomunt, prijs, aantal);
        }
        return new TriggerKoper(Bank.getInstance(), cryptomunt, prijs, aantal);
    }

/**
 * Author: Carmen
 * Vraag informatie op voor het transactiescherm op basis van gebruikersnaam en cryptoid (transactieStartDto)
 * @param transactieStartDto Gebruikersnaam en cryptoid
 * @return TransactiePaginaDto benodigde gegevens voor het transactiescherm
 */
    public TransactiePaginaDto openTransactiescherm(TransactieStartDto transactieStartDto){
        return rootRepository.openTransactieScherm(transactieStartDto);
    }
}


