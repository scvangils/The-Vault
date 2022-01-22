package com.example.thevault.klant;

public interface AdresDAO {
    public Adres slaAdresOp(Adres adres);

    public Adres wijzigAdres(Adres adres);

    public Adres getAdresById(int adresId);

    public Adres getAdresByKlant(Klant klant);

    public int verwijderAdres(Adres adres);


}
