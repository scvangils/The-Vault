package com.example.thevault.klant.repository;

import com.example.thevault.klant.model.Adres;
import com.example.thevault.klant.model.Klant;

public interface AdresDAO {
    public Adres slaAdresOp(Adres adres);

    public Adres wijzigAdres(Adres adres);

    public Adres getAdresById(int adresId);

    public Adres getAdresByKlant(Klant klant);

    public int verwijderAdres(Adres adres);


}
