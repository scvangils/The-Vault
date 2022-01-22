package com.example.thevault.database.mapping.dao;

import com.example.thevault.domain.model.Gebruiker;
import com.example.thevault.domain.model.Trigger;

import java.util.List;

public interface TriggerDAO {

    Trigger slaTriggerOp(Trigger trigger);

    int verwijderTrigger(Trigger trigger);

    List<Trigger> vindTriggersByGebruiker(Gebruiker gebruiker, String type);

    List<Trigger> vindAlleTriggers(String koperOfVerkoper);

    Trigger vindMatch(Trigger trigger);

    Trigger vindTriggerById(int triggerId, String koperOfVerkoper);

}
