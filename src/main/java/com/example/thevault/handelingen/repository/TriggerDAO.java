package com.example.thevault.handelingen.repository;

import com.example.thevault.handelingen.model.Trigger;
import com.example.thevault.klant.model.Gebruiker;

import java.util.List;

public interface TriggerDAO {

    Trigger slaTriggerOp(Trigger trigger);

    int verwijderTrigger(Trigger trigger);

    List<Trigger> vindTriggersByGebruiker(Gebruiker gebruiker, String type);

    List<Trigger> vindAlleTriggers(String koperOfVerkoper);

    Trigger vindMatch(Trigger trigger);

    Trigger vindTriggerById(int triggerId, String koperOfVerkoper);

}
