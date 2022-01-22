// Created by S.C. van Gils
// Creation date 1-12-2021

package com.example.thevault.database.mapping.dao;

import com.example.thevault.domain.model.Adres;
import com.example.thevault.domain.model.Klant;
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
import java.util.List;

@Repository
public class JDBCKlantDAO implements KlantDAO{

    private final Logger logger = LoggerFactory.getLogger(JDBCKlantDAO.class);

    private final JdbcTemplate jdbcTemplate;

    //TODO JavaDoc
    public JDBCKlantDAO(JdbcTemplate jdbcTemplate) {
        super();
        this.jdbcTemplate = jdbcTemplate;
        logger.info("New JDBCKlantDAO");
    }

    /**
     * Deze methode is nodig om een klant met een auto-increment id op te slaan
     * in de database.
     *
     * @param klant het klant object met de gegevens die moeten worden opgeslagen
     * @param connection het Connection-object nodig om de Prepared Statement aan te maken
     * @return Prepared Statement-object dat nodig is om Klant op te slaan in de database
     * @throws SQLException
     */
    private PreparedStatement slaKlantOpStatement(Klant klant, Connection connection) throws SQLException {
        String sql = "INSERT INTO klant (gebruikersnaam, wachtwoord, naam, bsn, geboortedatum, adresId) values (?, ?, ?, ?, ?, ?);";
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, klant.getGebruikersnaam());
        ps.setString(2, klant.getWachtwoord());
        ps.setString(3, klant.getNaam());
        ps.setLong(4, klant.getBsn());
        ps.setDate(5, Date.valueOf(klant.getGeboortedatum()));
        ps.setInt(6, klant.getAdres().getAdresId());
        return ps;
    }


    /**
     * Deze methode slaat de gegevens van een klant op in de database
     * op basis van een klant-object en zet de door auto-increment gegenereerd
     * gebruikerId in het klant-object
     *
     * @param klant het klant-object op basis van bij registratie ingevoerde gegevens
     * @return het klant-object met de juiste gebruikerId
     */
    @Override
    public Klant slaKlantOp(Klant klant) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> slaKlantOpStatement(klant, connection), keyHolder);
        int gebruikerId = keyHolder.getKey().intValue();
        klant.setGebruikerId(gebruikerId);
        return klant;
    }

    /**
     * Deze methode zoekt alle klanten in de database op en maakt een List van Klant-objecten aan op basis
     * van de teruggestuurde gegevens
     *
     * @return een List van klant-objecten
     */
    @Override
    public List<Klant> vindAlleKlanten() {
        String sql = "SELECT * FROM klant;";
        return jdbcTemplate.query(sql, new KlantRowMapper());
    }

    /**
     * Deze methode zoekt of er in de database al een klant bestaat met deze gebruikerId
     * en maakt eventueel een klant-object aan op nasis van de teruggestuurde gegevens
     *
     * @param gebruikerId gebruikerId van een (mogelijke) klant die uniek moet zijn
     * @return klant-object op basis van gegevens uit de database of null indien gebruikerId niet gevonden is
     */
    @Override
    public Klant vindKlantById(int gebruikerId) {
        String sql = "SELECT * FROM klant WHERE gebruikerId = ?;";
        Klant klant;
        try {
            klant = jdbcTemplate.queryForObject(sql, new KlantRowMapper(), gebruikerId);
        } catch (EmptyResultDataAccessException noResult) {
            klant = null;
        }
        return klant;
    }


    /**
     * Deze methode zoekt of er in de database al een klant bestaat met deze gebruikersnaam
     * en maakt eventueel een klant-object aan op nasis van de teruggestuurde gegevens
     *
     * @param gebruikersnaam gebruikersnaam van een (mogelijke) klant die uniek moet zijn
     * @return klant-object op basis van gegevens uit de database of null indien gebruikersnaam niet gevonden is
     */
    @Override
        public Klant vindKlantByGebruikersnaam(String gebruikersnaam) {
        String sql = "SELECT * FROM klant WHERE gebruikersnaam = ?;";
        Klant klant;
        try {
            klant = jdbcTemplate.queryForObject(sql, new KlantRowMapper(), gebruikersnaam);
        } catch (EmptyResultDataAccessException noResult) {
            klant = null;
        }
        return klant;
    }

    /**
     * Deze functie zorgt ervoor dat een klant uit de database verwijderd kan worden.
     *
     * @param klant De te verwijderen klant
     * @return het aantal veranderde rijen, in dit geval 0 of 1
     */

    @Override
    public int verwijderKlant(Klant klant) {
        String sql = "DELETE FROM klant WHERE gebruikerId = ?;";
        int affectedRows = 0;
        try{
        affectedRows = jdbcTemplate.update(sql, klant.getGebruikerId());
        }
        catch(DataAccessException noData){
            logger.warn("het is niet goed gegaan: " + noData.getMessage());
        }

        return affectedRows;
    }

    //TODO JavaDoc
    @Override
    public int updateKlant(Klant klant){
        int affectedRows = 0;
        String sql = "UPDATE klant SET gebruikersnaam = ?, naam = ?, adresId = ? WHERE gebruikerId = ?;";
        try{
         affectedRows = jdbcTemplate.update(sql, klant.getGebruikersnaam(), klant.getNaam(), klant.getAdres().getAdresId(),
                    klant.getGebruikerId());
        }
        catch(DataAccessException noData){
            logger.warn(noData.getMessage());
        }
        return affectedRows;
    }

    private static class KlantRowMapper implements RowMapper<Klant>{
        /**
         * Deze methode is, samen met een sql-query, nodig om in
         * het Spring framework eenvoudig een of meerdere Klant-objecten op basis van
         * de database aan te maken. De gebruikerId wordt via de setter gezet.
         *
         * @param resultSet resultaat van query in Klant-tabel database
         * @param rowNum getal nodig voor iteratie over ResultSet
         * @return Klant-object met informatie uit de database
         * @throws SQLException
         */
        @Override
        public Klant mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            Klant klant = new Klant(resultSet.getString("gebruikersnaam"),
                    resultSet.getString("wachtwoord"), resultSet.getString("naam"),
                    resultSet.getLong("bsn"),resultSet.getDate("geboortedatum").toLocalDate());
            klant.setGebruikerId(resultSet.getInt("gebruikerId"));
            Adres adres = new Adres();
            adres.setAdresId(resultSet.getInt("adresId"));
            klant.setAdres(adres);
            return klant;
        }
    }


}
