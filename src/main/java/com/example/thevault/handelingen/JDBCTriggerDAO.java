// Created by S.C. van Gils
// Creation date 17-1-2022

package com.example.thevault.handelingen;

import com.example.thevault.controller.rest_api_controller.Cryptomunt;
import com.example.thevault.controller.rest_api_controller.Gebruiker;
import com.example.thevault.klant.Klant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static com.example.thevault.handelingen.TriggerService.isKoper;

/**
 * Deze klasse zorgt ervoor dat de backend kan communiceren met de database,
 * in dit geval met de triggerKoper en TriggerVerkoper-tabel
 */
@Repository
public class JDBCTriggerDAO implements TriggerDAO {

    private final JdbcTemplate jdbcTemplate;

    private final Logger logger = LoggerFactory.getLogger(JDBCTriggerDAO.class);

    /**
     * Een constructor voor dependency injection
     *
     * @param jdbcTemplate De Spring Boot class die java-naar-sql makkelijk maakt
     */
    public JDBCTriggerDAO(JdbcTemplate jdbcTemplate) {
        super();
        this.jdbcTemplate = jdbcTemplate;
        logger.info("New JDBCTriggerDAO");
    }

    private String toonJuisteTabel(Trigger trigger){
        if(isKoper(trigger)){
            return "triggerKoper";
        }
        return "triggerVerkoper";
    }


    private PreparedStatement slaTriggerOpStatement(Trigger trigger, Connection connection) throws SQLException {
        String sql = String.format("INSERT INTO %s (gebruikerId, cryptoMuntId," +
                " triggerPrijs, aantal, datum) values (?, ?, ?, ?, ?);", toonJuisteTabel(trigger));
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, trigger.getGebruiker().getGebruikerId());
        ps.setInt(2, trigger.getCryptomunt().getId());
        ps.setDouble(3, trigger.getTriggerPrijs());
        ps.setDouble(4, trigger.getAantal());
        ps.setDate(5, Date.valueOf(LocalDate.now()));
        return ps;
    }

    /**
     * Deze methode slaat een trigger op in de database met de huidige datum
     * en voegt de door de database gegenereerde id toe aan de trigger
     * Afhankelijk van het type trigger wordt hij in de triggerKoper- of
     * in de triggerVerkopertabel opgeslagen.
     *
     * @param trigger  de betreffende trigger
     * @return de trigger met de gegenereerde id
     */
    @Override
    public Trigger slaTriggerOp(Trigger trigger) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> slaTriggerOpStatement(trigger, connection), keyHolder);
        int triggerId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        trigger.setTriggerId(triggerId);
        return trigger;
    }

    /**
     * Deze methode verwijdert een trigger op basis van zijn id.
     *
     * @param trigger de te verwijderen trigger
     * @return een 0 indien gefaald of niet gevonden, een 1 indien geslaagd
     */
    @Override
    public int verwijderTrigger(Trigger trigger) {
        String sql = String.format("DELETE FROM %s WHERE triggerId = ?;", toonJuisteTabel(trigger));
        int affectedRows = 0;
        try{
            affectedRows = jdbcTemplate.update(sql, trigger.getTriggerId());
        }
        catch(DataAccessException noData){
            logger.warn("het is niet goed gegaan: " + noData.getMessage());
        }
        return affectedRows;
    }

    /** Deze methode zoekt voor een triggerKoper in de triggerVerkoperTabel een match
     * om een transactie mee aan te gaan.
     * Gegeven meerdere matches, eerste het grootste verschil tussen vraag en aanbod,
     * dan de langst staande trigger.
     *
     * @param trigger de betreffende trigger
     * @return de meest geschikte match of null indien geen match
     */
    @Override
    public Trigger vindMatch(Trigger trigger){
        String sql = getMatchSql(trigger);
        try {
            return jdbcTemplate.queryForObject(sql, maakOmgekeerdeRowMapper(trigger),
                    trigger.getAantal(), trigger.getCryptomunt().getId(), trigger.getTriggerPrijs(), trigger.getGebruiker().getGebruikerId());
        }
        catch (
                EmptyResultDataAccessException exception){
            logger.warn("Geen data gevonden, exceptie: " + exception);
        }
        return null;
    }

    @Override
    public Trigger vindTriggerById(int triggerId, String koperOfVerkoper) {
        String tabel = "trigger" + koperOfVerkoper;
        String sql = String.format("SELECT * FROM %s WHERE triggerId = ?;", tabel);
        try{
            return jdbcTemplate.queryForObject(sql, maakJuisteRowMapper(koperOfVerkoper), triggerId);
        }
        catch (
                EmptyResultDataAccessException exception){
            logger.warn("Geen data gevonden, exceptie: " + exception);
        }
        return null;
    }


    private String getMatchSql(Trigger trigger) {
        String koperSql = "SELECT * FROM triggerKoper WHERE aantal = ? AND cryptomuntId = ? AND triggerPrijs >= ? " +
                "AND NOT gebruikerId = ? ORDER BY triggerPrijs DESC, datum ASC LIMIT 1;";
        String verkoperSql = "SELECT * FROM triggerVerkoper WHERE aantal = ? AND cryptomuntId = ? AND triggerPrijs <= ? " +
                "AND NOT gebruikerId = ? ORDER BY triggerPrijs ASC, datum ASC LIMIT 1;";
        return (isKoper(trigger)) ? verkoperSql: koperSql;
    }

    /**
     * author: Steven van Gils
     * Geeft alle triggers van een bepaald type aanwezig in de database van een bepaalde gebruiker
     *
     * @param gebruiker De betreffende gebruiker
     * @param koperOfVerkoper Geeft aan welke tabel gebruikt moet worden
     * @return een List van Triggers, geheel bestaand uit een enkele subklasse
     */
    @Override
    public List<Trigger> vindTriggersByGebruiker(Gebruiker gebruiker, String koperOfVerkoper) {
        String tabel = "trigger" + koperOfVerkoper;
        String sql = String.format("SELECT * FROM %s WHERE gebruikerId = ?;", tabel);
        try{
            return jdbcTemplate.query(sql, maakJuisteRowMapper(koperOfVerkoper), gebruiker.getGebruikerId());
        }
        catch (
                EmptyResultDataAccessException exception){
            logger.warn("Geen data gevonden, exceptie: " + exception);
        }
        return null;
    }

    /**
     * Geeft alle triggers van een bepaald type aanwezig in de database
     *
     * @param koperOfVerkoper Geeft aan welke tabel gebruikt moet worden
     * @return een List van Triggers, geheel bestaand uit een enkele subklasse
     */
    @Override
    public List<Trigger> vindAlleTriggers(String koperOfVerkoper) {
        String tabel = "trigger" + koperOfVerkoper;
        String sql = String.format("SELECT * FROM %s;", tabel);
        try {
            return jdbcTemplate.query(sql, maakJuisteRowMapper(koperOfVerkoper));
        }
        catch (
                EmptyResultDataAccessException exception){
            logger.warn("Geen data gevonden, exceptie: " + exception);
        }
        return null;
    }


    /**
     * Deze methode maakt een Trigger-object aan op basis van een resultSet
     * Hij is protected static zodat hij binnen de package gebruikt kan worden,
     * maar binnen meerdere klassen, aangezien hij in twee RowMapper-klassen nodig is.
     *
     * @param resultSet De database-informatie over de aan te passen trigger
     * @param trigger de aan te passen trigger
     * @return het trigger-object met de gevulde fields
     * @throws SQLException Als er een sql-fout optreedt
     */
    protected static Trigger getTrigger(ResultSet resultSet, Trigger trigger) throws SQLException {
        trigger.setTriggerId(resultSet.getInt("triggerId"));
        trigger.setTriggerPrijs(resultSet.getDouble("triggerPrijs"));
        trigger.setAantal(resultSet.getDouble("aantal"));
        trigger.setDatum(resultSet.getDate("datum").toLocalDate());
        Gebruiker gebruiker = new Klant();
        gebruiker.setGebruikerId(resultSet.getInt("gebruikerId"));
        trigger.setGebruiker(gebruiker);
        Cryptomunt cryptomunt = new Cryptomunt(resultSet.getInt("cryptomuntId"));
        trigger.setCryptomunt(cryptomunt);
        return trigger;
    }

    /**
     * Deze methode zorgt ervoor dat afhankelijk van de mogelijke rol in de transactie
     * de juiste tabel wordt geraadpleegd
     *
     * @param rolGebruiker Of de gebruiker koper of verkoper is
     * @return een RowMapper
     */
    private RowMapper<Trigger> maakJuisteRowMapper(String rolGebruiker){
        if(rolGebruiker.equals("Koper")){
            return new TriggerKoperRowMapper();
        }
        else return new TriggerVerkoperRowMapper();
    }

    /**
     * Deze methode zorgt ervoor dat bij een triggerKoper een TriggerVerkoperRowMapper geïnstantieerd wordt
     * en vice versa, toepasbaar in de vindMatch-methode;
     *
     * @param trigger De betreffende trigger
     * @return een RowMapper
     */
    private RowMapper<Trigger> maakOmgekeerdeRowMapper(Trigger trigger) {
        if (!isKoper(trigger)) {
            return new TriggerKoperRowMapper();
        } else return new TriggerVerkoperRowMapper();
    }

    private static class TriggerVerkoperRowMapper implements RowMapper<Trigger> {

        @Override
        public Trigger mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            Trigger trigger = new TriggerVerkoper();
            return getTrigger(resultSet, trigger);
        }
    }

    private static class TriggerKoperRowMapper implements RowMapper<Trigger> {

        @Override
        public Trigger mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            Trigger trigger = new TriggerKoper();
            return getTrigger(resultSet, trigger);
        }
    }

}
