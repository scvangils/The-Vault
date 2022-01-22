// Created by S.C. van Gils
// Creation date 14-12-2021

package com.example.thevault.database.mapping.dao;

import com.example.thevault.domain.model.CryptoWaarde;
import com.example.thevault.domain.model.Cryptomunt;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

@Repository
public class JDBCCryptoWaardeDAO implements CryptoWaardeDAO {

    private final JdbcTemplate jdbcTemplate;

    @JsonIgnore
    private final Logger logger = LoggerFactory.getLogger(JDBCCryptoWaardeDAO.class);

    //TODO JavaDoc
    public JDBCCryptoWaardeDAO(JdbcTemplate jdbcTemplate) {
        super();
        this.jdbcTemplate = jdbcTemplate;
        logger.info("New JDBCCryptoWaardeDAO");
    }

    //TODO JavaDoc
    //voor historische lijst
    //TODO CryptoWaardeDto
    @Override
    public List<CryptoWaarde> getCryptoWaardeByCryptomunt(Cryptomunt cryptomunt) {
        String sql = "SELECT * FROM dagkoersCrypto WHERE cryptomuntId = ?;";
    return jdbcTemplate.query(sql, new CryptoWaardeRowMapper(), cryptomunt.getId());
    }

    //TODO JavaDoc
    //vooral handig voor laatste waarde
    @Override
    public CryptoWaarde getCryptoWaardeByCryptomuntAndDate(Cryptomunt cryptomunt, LocalDate datum) {
        CryptoWaarde cryptoWaarde;
        String sql = "SELECT * FROM dagkoersCrypto WHERE cryptomuntId = ? AND datum = ?;";
        try {
            cryptoWaarde = jdbcTemplate.queryForObject(sql, new CryptoWaardeRowMapper(), cryptomunt.getId(), Date.valueOf(datum));
        }
        catch (EmptyResultDataAccessException geenResultaat){
            cryptoWaarde = null;
        }
        return cryptoWaarde;
    }

    /**
     * Deze methode slaat de waarde van een cryptomunt en de datum waarop hij deze waarde heeft op
     * in de database. Eerst wordt nog een correcte Id aangemaakt.
     *
     * @param cryptoWaarde het object zonder correcte Id
     * @return cryptowaarde Het object met correcte Id
     */
    @Override
    public CryptoWaarde slaCryptoWaardeOp(CryptoWaarde cryptoWaarde) {
        String sql = "INSERT INTO dagkoersCrypto (cryptowaardeId, cryptomuntId, waardeCrypto, datum) VALUES (?, ?, ?, ?);";
        cryptoWaarde.setCryptoWaardeId(generateCryptoWaardeId(cryptoWaarde.getDatum(), cryptoWaarde.getCryptomunt()));
        jdbcTemplate.update(sql, cryptoWaarde.getCryptoWaardeId(), cryptoWaarde.getCryptomunt().getId(),
                cryptoWaarde.getWaarde(), Date.valueOf(cryptoWaarde.getDatum()));
        return cryptoWaarde;
    }

    /**
     * Deze methode zorgt ervoor dat de Id van een CryptoWaarde het volgende format heeft:
     * YYYYMMDD<Cryptomunt.symbol>
     * @param localDate
     * @param cryptomunt
     * @return
     */
    public String generateCryptoWaardeId(LocalDate localDate, Cryptomunt cryptomunt) {
        int jaar = localDate.getYear();
        int maand = localDate.getMonthValue();
        int dag = localDate.getDayOfMonth();
        return String.format(jaar + "%02d" + "%02d" + cryptomunt.getSymbol(), maand, dag);
    }

    private static class CryptoWaardeRowMapper implements RowMapper<CryptoWaarde> {

    @Override
    public CryptoWaarde mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return new CryptoWaarde(resultSet.getString("cryptoWaardeId"),
                null,
                resultSet.getDouble("waardeCrypto"), resultSet.getDate("datum").toLocalDate());
    }
}
}
