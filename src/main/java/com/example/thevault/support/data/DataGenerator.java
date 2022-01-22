// Created by S.C. van Gils
// Creation date 16-12-2021

package com.example.thevault.support.data;


import com.example.thevault.klant.Adres;
import com.example.thevault.klant.Klant;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DataGenerator {

    //TODO Verwijderen?


    //TODO JavaDoc
    public static void main(String[] args) throws IOException {
    List<Klant> list = maakLijstKlantenVanCSV("Sprint2/datacsv.csv", 20);
        for(Klant klant: list){
            System.out.println(klant);
        }
    }


    private final Logger logger = LoggerFactory.getLogger(DataGenerator.class);

    public static int[] LANGE_MAANDEN = {1,3,5,7,8,10,12};
    public static int FEBRUARI = 2;
    public static int AANTAL_DAGEN_IN_LANGE_MAAND = 31;
    public static int AANTAL_DAGEN_IN_KORTE_MAAND = 30;
    public static int AANTAL_DAGEN_IN_FEBRUARI_NORMAAL = 28;
    public static int AANTAL_DAGEN_IN_FEBRUARI_SCHRIKKELJAAR = 29;
    public static int SCHRIKKELJAAR_PER_ZOVEEL_JAAR = 4;
    public static int VROEGSTE_GEBOORTEJAAR = 1946;
    public static int LAATSTE_GEBOORTEJAAR = 2001;
    public static int EERSTE_MAAND_VAN_JAAR = 1;
    public static int LAATSTE_MAAND_VAN_JAAR = 12;
    public static int AANTAL_TOEVOEGINGEN = 5;


    /**
     * No-args constructor voor DataGenerator class
     */
    public DataGenerator(){
        super();
        logger.info("new Datagenerator");
    }

    /**
     * Deze methode maakt van een csv met een voor dit project specifieke gegevensset een List van Klant-objecten
     *
     * @param filename de naam van het bestand
     * @param hoeveelKlanten Biedt de mogelijkheid om niet alle gegevens in objecten om te zetten
     * @return een List van Klant-objecten
     * @throws IOException als het bestand niet gevonden wordt of niet uitgelezen kan worden
     */
    public static List<Klant> maakLijstKlantenVanCSV(String filename, int hoeveelKlanten) throws IOException {
        Resource resource = new ClassPathResource(filename);
        File file = resource.getFile();
        List<Klant> klantList = new ArrayList<>();
        Scanner klantenLezer;
        int counter = 0;
        try {
            klantenLezer = new Scanner(file);
            createKlantList(hoeveelKlanten, klantList, klantenLezer, counter);
        }
        catch (FileNotFoundException exception){
            System.out.println("geen bestand kunnen uitlezen");
        }
        return klantList;
    }

    private static void createKlantList(int hoeveelKlanten, List<Klant> klantList, Scanner klantenLezer, int counter) {
        while (klantenLezer.hasNextLine() & counter < hoeveelKlanten) {
            String[] regelArray = klantenLezer.nextLine().split(",");
            String adresStraatEnHuisnummer = regelArray[4];
            String[] adresArray = adresStraatEnHuisnummer.split(" ");
            Adres adres = new Adres(getStraatnaam(adresArray), Integer.parseInt(adresArray[adresArray.length - 1]),
                    genereerRandomToevoeging(AANTAL_TOEVOEGINGEN), regelArray[5].replace(" ", ""),
                    regelArray[6]);
            addKlantToList(klantList, regelArray[0], regelArray[1], regelArray[2], Long.parseLong(regelArray[3])
            );
            counter++;
        }
    }

    private static void addKlantToList(List<Klant> klantList, String gebruikersnaam, String wachtwoord,
                                       String naam, long bsn) {
        Klant klant = new Klant(gebruikersnaam, wachtwoord,
                naam, bsn, genereerRandomGeboortedatum());
        klantList.add(klant);
    }

    private static String getStraatnaam(String[] adresArray) {
        String straatnaam = adresArray[0];
        for (int i = 1; i < adresArray.length - 1; i++) {
            straatnaam += " " + adresArray[i];
        }
        return straatnaam;
    }

    private static LocalDate genereerRandomGeboortedatum() {
        int geboortejaar = genereerRandomGetal(VROEGSTE_GEBOORTEJAAR, LAATSTE_GEBOORTEJAAR);
        int geboortemaand = genereerRandomGetal(EERSTE_MAAND_VAN_JAAR, LAATSTE_MAAND_VAN_JAAR);
        int laatsteDagVanDeMaand = AANTAL_DAGEN_IN_KORTE_MAAND;
        laatsteDagVanDeMaand = getLaatsteDagVanDeMaand(geboortejaar, geboortemaand, laatsteDagVanDeMaand);
        int geboortedag = genereerRandomGetal(1, laatsteDagVanDeMaand);
        return LocalDate.of(geboortejaar, geboortemaand, geboortedag);
    }

    private static int getLaatsteDagVanDeMaand(int geboortejaar, int geboortemaand, int laatsteDagVanDeMaand) {
        if(ArrayUtils.contains(LANGE_MAANDEN, geboortemaand)){
            laatsteDagVanDeMaand = AANTAL_DAGEN_IN_LANGE_MAAND;
        }
        else if(geboortemaand == FEBRUARI){
            laatsteDagVanDeMaand = (isSchrikkeljaar(geboortejaar)) ?
                    AANTAL_DAGEN_IN_FEBRUARI_SCHRIKKELJAAR : AANTAL_DAGEN_IN_FEBRUARI_NORMAAL;
        }
        return laatsteDagVanDeMaand;
    }

    /**
     * Deze methode geeft aan of een jaartal een schrikkeljaar is
     *
     * @param jaar het jaartal
     * @return true voor een schrikkeljaar, anders false
     */
    public static boolean isSchrikkeljaar(int jaar){
        return jaar%SCHRIKKELJAAR_PER_ZOVEEL_JAAR == 0;
    }

    /**
     * deze methode genereert een random getal tussen aangegeven waarden
     *
     * @param minimum de minimumwaarde
     * @param maximum de maximumwaarde
     * @return een random getal tussen aangegeven waarden inclusief
     */
    public static int genereerRandomGetal(int minimum, int maximum){
        return
                ((int)(Math.random() * ((maximum - minimum + 1)))) + minimum;
    }

    /**
     * Deze methode genereert een random letter die als toevoeging kan dienen voor een huisnummer.
     * Kan ook een lege String teruggeven omdat toevoegingen optioneel zijn.
     *
     * @param aantalVerschillende Het aantal verschillende toevoegingen waa uit gekozen kan worden
     * @return een letter of een lege String
     */
    private static String genereerRandomToevoeging(int aantalVerschillende){
        String letter = "";
       int getal = genereerRandomGetal(0, aantalVerschillende);
       if(getal != 0) {
        letter = Character.toString((char)(getal + 64)).toString();
       }
       return letter;
    }
}
