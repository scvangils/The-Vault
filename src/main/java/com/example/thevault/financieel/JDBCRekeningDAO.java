package com.example.thevault.financieel;

import com.example.thevault.klant.Gebruiker;
import com.example.thevault.klant.JDBCKlantDAO;
import com.example.thevault.support.exceptions.BalanceTooLowException;
import net.minidev.json.annotate.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Author: Ju-Sen Cheung
 * Beschrijving: De DAO voor Rekening, waar enkele methodes instaan voor de CRUD-functionaliteiten van Rekening en het
 * vinden van een rekening en het opvragen van het saldo op een rekening.
 */

@Repository
public class JDBCRekeningDAO implements RekeningDAO {

    @JsonIgnore
    private final Logger logger = LoggerFactory.getLogger(JDBCKlantDAO.class);

    JdbcTemplate jdbcTemplate;

    /**
     * Constructor voor JDBCRekeningDAO
     * @param jdbcTemplate injectie van het jdbcTemplate in de constructor
     */
    @Autowired
    public JDBCRekeningDAO(JdbcTemplate jdbcTemplate) {
        super();
        this.jdbcTemplate = jdbcTemplate;
        logger.info("New JDBCRekeningDAO");
    }

    private PreparedStatement slaRekeningOpStatement(Rekening rekening, Connection connection) throws SQLException {
        String sql = "INSERT INTO rekening (gebruikerId, iban, saldo) values " +
                "(?, ?, ?);";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, rekening.getGebruiker().getGebruikerId());
        ps.setString(2, rekening.getIban());
        ps.setDouble(3, rekening.getSaldo());
        return ps;
    }

    private PreparedStatement wijzigSaldoStatement(Rekening rekening, Connection connection) throws SQLException {
        String sql = "UPDATE rekening SET saldo = ? WHERE IBAN = ? ;";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setDouble(1, rekening.getSaldo());
        ps.setString(2, rekening.getIban());
        return ps;
    }

    private static class RekeningRowMapper implements RowMapper<Rekening> {
        @Override
        public Rekening mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            return new Rekening(resultSet.getString("iban"), resultSet.getDouble("saldo"));
        }
    }

    /**
     * Met deze methode wordt er een rekening opgeslagen.
     * @param rekening is de rekening die is aangemaakt bij het registreren van de gebruiker.
     * @return rekening die is opgeslagen na registratie van een gebruiker.
     */
    @Override
    public Rekening slaRekeningOp(Rekening rekening)  {
        jdbcTemplate.update(connection -> slaRekeningOpStatement(rekening, connection));
        return rekening;
    }

    /**
     * Met deze methode kan je een rekening terugvinden als je de gebruiker meegeeft.
     * @param gebruiker is de gerbuiker van wie de rekening wordt opgevraagd.
     * @return rekening waarvan je de iban en het saldo kunt zien van de gebruiker.
     */
    @Override
    public Rekening vindRekeningVanGebruiker(Gebruiker gebruiker) {
        String sql = "SELECT * FROM rekening WHERE gebruikerId = ?;";
        Rekening rekening;
        try {
            rekening = jdbcTemplate.queryForObject(sql, new RekeningRowMapper(), gebruiker.getGebruikerId());
            gebruiker.setRekening(rekening);
        } catch (EmptyResultDataAccessException noResult) {
            rekening = null;
        }
        return rekening;
    }

    /**
     * Met deze methode kan je het saldo van de rekening opvragen als je de gebruiker meegeeft.
     * @param gebruiker is de gebruiker van wie het rekeningsaldo wordt opgevraagd.
     * @return het saldo van de rekening.
     */
    @Override
    public double vraagSaldoOpVanGebruiker(Gebruiker gebruiker) {
        String sql = "SELECT * FROM rekening WHERE gebruikerId = ?;";
        Rekening rekening;
        try {
            rekening = jdbcTemplate.queryForObject(sql, new JDBCRekeningDAO.RekeningRowMapper(), gebruiker.getGebruikerId());
        } catch (EmptyResultDataAccessException geenResultaatGevonden) {
            return -1;
        }
       return rekening.getSaldo();
    }

    /**
     * Met deze methode kan je het saldo van de rekening updaten als je de gebruiker en het transactiebedrag meegeeft.
     * Het wijzigen van het saldo gebeurt doordat je een cryptomunt koopt of verkoopt via een transactie.
     * @param gebruiker is de gebruiker bij wie een transactie plaatsvindt.
     * @param transactiebedrag is het bedrag dat bij het rekeningsaldo opgeteld of afgetrokken wordt.
     * @return Als er voldoende saldo is voor de transactie, dan wordt het saldo geüpdatet. Zo niet, dan komt er een
     * bericht dat het saldo niet toereikend is.
     */
    @Override // transactiebedrag is negatief bij koper
    public double updateSaldo(Gebruiker gebruiker, double transactiebedrag) throws BalanceTooLowException {
        Rekening rekening = vindRekeningVanGebruiker(gebruiker);
        double huidigSaldo = rekening.getSaldo();
        if(huidigSaldo > - transactiebedrag) {
            return huidigSaldo+transactiebedrag;
        } else {
            throw new BalanceTooLowException();
        }
    }

    /**
     * Met deze methode kan je het nieuwe saldo na een transactie in de rekening opslaan.
     * @param gebruiker is de gebruiker van wie het saldo geüpdatet moet worden.
     * @param transactiebedrag is het bedrag waarmee het saldo van de rekening verhoogd of verlaagd moet worden.
     * @return rekening met geüpdatete saldo.
     */
    @Override
    public Rekening wijzigSaldoVanGebruiker(Gebruiker gebruiker, double transactiebedrag) {
        double nieuwSaldo = updateSaldo(gebruiker, transactiebedrag);
        gebruiker.getRekening().setSaldo(nieuwSaldo);
        System.out.println(gebruiker.getRekening() + "  " + gebruiker.getRekening().getSaldo());
        jdbcTemplate.update(connection -> wijzigSaldoStatement(gebruiker.getRekening(), connection));
        return gebruiker.getRekening();
    }
}
