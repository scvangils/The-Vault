// Created by S.C. van Gils
// Creation date 15-12-2021

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
import java.util.Objects;

@Repository
public class JDBCAdresDAO implements AdresDAO{

    private final JdbcTemplate jdbcTemplate;

    private final Logger logger = LoggerFactory.getLogger(JDBCAdresDAO.class);

    //TODO JavaDoc
    public JDBCAdresDAO(JdbcTemplate jdbcTemplate) {
        super();
        this.jdbcTemplate = jdbcTemplate;
        logger.info("New JDBCAdresDAO");
    }

    private PreparedStatement slaAdresOpStatement(Adres adres, Connection connection) throws SQLException {
        String sql = "INSERT INTO adres (straatnaam, huisnummer, toevoeging, postcode, plaatsnaam) values (?, ?, ?, ?, ?);";
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, adres.getStraatnaam());
        ps.setInt(2, adres.getHuisnummer());
        ps.setString(3, adres.getToevoeging());
        ps.setString(4, adres.getPostcode());
        ps.setString(5, adres.getPlaatsnaam());
        return ps;
    }

    /**
     * Deze methode kijkt of het adres al in de database staat en, zo niet,
     * slaat het adres op en zet de adresId op de door de database gegenereerde waarde
     *
     * @param adres Het adres dat moet worden opgeslagen
     * @return Het opgeslagen adres of het adres dat al werd gevonden in de database
     */
    @Override
    public Adres slaAdresOp(Adres adres) {
        Adres anderAdres = vindAdresByPostcodeHuisnummerEnToevoeging(adres);
        if(anderAdres == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> slaAdresOpStatement(adres, connection), keyHolder);
            int adresId = Objects.requireNonNull(keyHolder.getKey()).intValue();
            adres.setAdresId(adresId);
        }
        else {
            return anderAdres;
        }
        return adres;
    }

    /**
     * Deze methode zoekt een adres op basis van postcode, huisnummer en toevoeging in de database op.
     * Deze methode wordt gebruikt om te zien of een adres al in de database aanwezig is.
     *
     * @param adres het adres waarmee vergeleken wordt
     * @return het adres of null als het niet gevonden wordt.
     */

    public Adres vindAdresByPostcodeHuisnummerEnToevoeging(Adres adres){
        String sql = "SELECT * FROM adres WHERE postcode = ? AND huisnummer = ? AND toevoeging = ?";
        try {
        adres = jdbcTemplate.queryForObject(sql, new AdresRowMapper(),
                    adres.getPostcode(), adres.getHuisnummer(), adres.getToevoeging());
        }
        catch(EmptyResultDataAccessException noResult){
            adres = null;
        }
        return adres;

    }

// TODO verwijderen? ...
    //TODO ... of JavaDoc schrijven
    @Override
    public Adres wijzigAdres(Adres adres) {
        String sql = "UPDATE adres SET straatnaam = ?, huisnummer = ?, toevoeging = ?, postcode = ?, plaatsnaam = ?" +
                " WHERE adresId = ?;";
        jdbcTemplate.update(sql, adres.getStraatnaam(), adres.getHuisnummer(), adres.getToevoeging(),
                adres.getPostcode(), adres.getPlaatsnaam(), adres.getAdresId());
        return adres;
    }

    /**
     * Deze methode zoekt een adres in de database op met de gegeven id.
     *
     * @param adresId het id dat bij een adres moet horen
     * @return het adres of null als het niet gevonden wordt.
     */

    @Override
    public Adres getAdresById(int adresId){
        String sql = "SELECT * FROM adres WHERE adresId = ?;";
        Adres adres;
        try{
        adres = jdbcTemplate.queryForObject(sql, new AdresRowMapper(), adresId);
        }
        catch(EmptyResultDataAccessException noResult){
            adres = null;
        }
        return adres;
    }

    /**
     * Deze methode is nodig om een adres aan een klant-object toe te kunnen voegen
     * binnen het Spring framework. Dit adres wordt in de rootrepository gekoppeld aan een klant.
     *
     * @param klant Het object met de overige gegevens van de betreffende klant
     * @return het adres-object dat aan het klant-object kan worden toegevoegd
     */
    @Override
    public Adres getAdresByKlant(Klant klant){
        String sql = "SELECT * FROM adres WHERE adresId = (SELECT adresId FROM klant WHERE gebruikerId = ?);";
        Adres adres;
        try{
            adres = jdbcTemplate.queryForObject(sql, new AdresRowMapper(), klant.getGebruikerId());
        }
        catch(EmptyResultDataAccessException noResult){
            adres = null;
        }
        return adres;
    }


    //TODO JavaDoc
    @Override
    public int verwijderAdres(Adres adres) {
        int affectedRows = 0;
        String sql = "DELETE FROM adres WHERE adresId = ?;";
        try {
            affectedRows = jdbcTemplate.update(sql, adres.getAdresId());
        }
        catch(DataAccessException noData){
            logger.warn("het is niet goed gegaan: " + noData.getMessage());
        }
        return affectedRows;
    }

    private static class AdresRowMapper implements RowMapper<Adres>{

        @Override
        public Adres mapRow(ResultSet rs, int rowNum) throws SQLException {
            Adres adres = new Adres(rs.getString("straatnaam"), rs.getInt("huisnummer"), rs.getString("toevoeging"),
                    rs.getString("postcode"), rs.getString("plaatsnaam"));
            int adresId = rs.getInt("adresId");
            adres.setAdresId(adresId);
            return adres;
        }
    }
}
