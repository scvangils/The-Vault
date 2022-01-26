// Created by carme
// Creation date 11/12/2021

package com.example.thevault.financieel.repository;

import com.example.thevault.financieel.model.Cryptomunt;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class JDBCCryptomuntDAO implements CryptomuntDAO {

    private final JdbcTemplate jdbcTemplate;

    @JsonIgnore
    private final Logger logger = LoggerFactory.getLogger(JDBCCryptomuntDAO.class);

    //TODO JavaDoc
    @Autowired
    public JDBCCryptomuntDAO(JdbcTemplate jdbcTemplate) {
        super();
        this.jdbcTemplate = jdbcTemplate;
        logger.info("New JDBCCryptomuntDAO");
    }


    /**
     * Methode die de informatie over een specifieke cryptomunt uit de database haalt
     * @param cryptomuntId cryptomuntidentifier waarover informatie wordt opgevraagd
     * @return Cryptomunt alle informatie over de opgevraagde cryptomunt
     */
    @Override
    public Cryptomunt geefCryptomunt(int cryptomuntId) {
        String sql = "Select * from cryptomunt where cryptomuntId = ?;";
        return jdbcTemplate.queryForObject(sql, new JDBCCryptomuntDAO.CryptomuntRowMapper(), cryptomuntId);
    }
    /**
     * Methode die de informatie over een specifieke cryptomunt uit de database haalt
     * @param cryptoNaam cryptomuntidentifier waarover informatie wordt opgevraagd
     * @return Cryptomunt alle informatie over de opgevraagde cryptomunt
     */
    @Override
    public Cryptomunt geefCryptomuntByNaam(String cryptoNaam) {
        String sql = "Select * from cryptomunt where naam = ?;";
        return jdbcTemplate.queryForObject(sql, new JDBCCryptomuntDAO.CryptomuntRowMapper(), cryptoNaam);
    }


    /**
     * Deze methode haalt uit de database een lijst met alle cryptomunten die verhandeld kunnen worden
     *
     * @return een lijst met alle cryptomunten die verhandeld kunnen worden
     */
    @Override
    public List<Cryptomunt> geefAlleCryptomunten(){
        String sql = "SELECT * FROM cryptomunt;";
        return jdbcTemplate.query(sql, new CryptomuntRowMapper());
    }
    private static class CryptomuntRowMapper implements RowMapper<Cryptomunt> {
        @Override
        public Cryptomunt mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            return new Cryptomunt(resultSet.getInt("cryptomuntId"), resultSet.getString("naam"),
                    resultSet.getString("afkorting"));
        }
    }

}
