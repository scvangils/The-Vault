package com.example.thevault.handelingen;

import com.example.thevault.controller.rest_api_controller.Gebruiker;

import java.util.List;

public interface TriggerDAO {

    Trigger slaTriggerOp(Trigger trigger);

    int verwijderTrigger(Trigger trigger);

    List<Trigger> vindTriggersByGebruiker(Gebruiker gebruiker, String type);

    List<Trigger> vindAlleTriggers(String koperOfVerkoper);

    Trigger vindMatch(Trigger trigger);

    Trigger vindTriggerById(int triggerId, String koperOfVerkoper);

}
