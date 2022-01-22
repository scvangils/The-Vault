package com.example.thevault.klant;

import java.util.List;

public interface KlantDAO {
    Klant slaKlantOp(Klant klant);

    Klant vindKlantById(int gebruikerId);

    List<Klant> vindAlleKlanten();

    Klant vindKlantByGebruikersnaam(String gebruikersnaam);

    int verwijderKlant(Klant klant);

    public int updateKlant(Klant klant);

}
