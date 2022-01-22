// Created by E.S. Olthof
// Creation date 12/15/2021

package com.example.thevault.handelingen;

import com.example.thevault.financieel.Cryptomunt;
import com.example.thevault.klant.Gebruiker;
import com.example.thevault.klant.JDBCKlantDAO;
import com.example.thevault.klant.Klant;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class JDBCTransactieDAO implements TransactieDAO {
    JdbcTemplate jdbcTemplate;

    @JsonIgnore
    private final Logger logger = LoggerFactory.getLogger(JDBCKlantDAO.class);

    //TODO JavaDoc
    @Autowired
    public JDBCTransactieDAO(JdbcTemplate jdbcTemplate) {
        super();
        this.jdbcTemplate = jdbcTemplate;
        logger.info("New JDBCTransactieDAO");
    }

    //TODO bedrag in prijs veranderen in database
    /**@author Elise Olthof
     * De onderstaande functie creeert een prepared statement die kan worden gebruikt om transacties
     * in de database op te slaan. Deze functie zorgt ervoor dat er een generated key aan de transactie
     * wordt toegevoegd vanuit de database
     * @param transactie het transactieobject dat in de database moet worden opgeslagen
     * @param connection het connectionobject dat nodig is om de database te bereiken
     * @return ps het prepared statement dat we kunnen gebruiken om de transactie mee op te slaan
     * @throws SQLException
     * */
    private PreparedStatement slaTransactieOpStatement(Transactie transactie, Connection connection) throws SQLException {
        String sql = "INSERT INTO transactie (aantal, momentTransactie, koperGebruikerId, cryptomuntId, bedrag," +
                " verkoperGebruikerId, bankFee) values (?, ?, ?, ?, ?, ?, ?);";
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setDouble(1, transactie.getAantal());
        System.out.println("*** Dit is de datum: " + transactie.getMomentTransactie());
        ps.setTimestamp(2, Timestamp.valueOf(transactie.getMomentTransactie()));
        ps.setInt(3, transactie.getKoper().getGebruikerId());
        ps.setInt(4, transactie.getCryptomunt().getId());
        ps.setDouble(5, transactie.getPrijs());
        ps.setInt(6, transactie.getVerkoper().getGebruikerId());
        ps.setDouble(7, transactie.getBankFee());
        return ps;
    }


    /**@author Elise Olthof
     * SlaTransactieOp functie om een transactie in de database op te slaan
     *
     * @param transactie de transactie die moet worden opgeslagen.
     * @return transactie de opgeslagen transactie
     *
     * */
    @Override
    public Transactie slaTransactieOp(Transactie transactie) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> slaTransactieOpStatement(transactie, connection), keyHolder);
        int transactieId = keyHolder.getKey().intValue();
        transactie.setTransactieId(transactieId);
        return transactie;
    }


    /** @author Elise Olthof
     * Deze functie geeft een lijst met alle transacties uit de database terug.
     *
     * @return lijst met transacties
     * */
    @Override
    public List<Transactie> geefAlleTransacties(){
        String sql = "SELECT * FROM transactie;";
        return jdbcTemplate.query(sql, new TransactieRowMapper());
    }


    /**@author Elise Olthof
     * Deze functie geeft alle transacties van een meegegeven gebruiker(transactiepartij) terug,
     * dit heeft betrekking op zowel verkoop als aankoop transacties
     *
     * @param gebruiker de transactiepartij
     * @return lijst met transacties van de meegegeven gebruiker(transactiepartij)
     * */
    @Override
    public List<Transactie> geefTransactiesVanGebruiker(Gebruiker gebruiker) {
        String sql = "SELECT * FROM transactie WHERE verkoperGebruikerId = ? OR koperGebruikerId = ?;";
        List<Transactie> transactiesGebruiker = null;
        try {
            transactiesGebruiker = jdbcTemplate.query(sql, new TransactieRowMapper()
                    , gebruiker.getGebruikerId(), gebruiker.getGebruikerId());
        } catch (EmptyResultDataAccessException exception){
            System.out.println("Geen data gevonden, exceptie: " + exception);
        }
        return transactiesGebruiker;
    }

    /**@author Elise Olthof
     * Deze functie geeft alle transacties van een meegegeven gebruiker(transactiepartij) terug,
     * dit heeft betrekking op zowel verkoop als aankoop transacties
     * @param gebruiker de transactiepartij
     * @param startDatum de startdatum vanaf het moment dat je het transactieoverzicht wilt
     * @param eindDatum de laatste datum waarop een transactie gemaakt kan zijn
     *
     * @return lijst met transacties van de meegegeven gebruiker(transactiepartij) binnen de
     * meegegeven start- en einddatum
     * */
    @Override
    public List<Transactie> geefTransactiesVanGebruikerInPeriode(Gebruiker gebruiker, Timestamp startDatum, Timestamp eindDatum) {
        String sql = "SELECT * FROM transactie WHERE (verkoperGebruikerId = ? OR koperGebruikerId = ?) AND momentTransactie BETWEEN ? AND ?;";
        List<Transactie> transactiesGebruiker = null;
        try {
            transactiesGebruiker = jdbcTemplate.query(sql, new TransactieRowMapper()
                    , gebruiker.getGebruikerId(), gebruiker.getGebruikerId()
                    , startDatum, eindDatum);
        } catch (EmptyResultDataAccessException exception){
            System.out.println("Geen data gevonden, exceptie: " + exception);
        }
        return transactiesGebruiker;
    }

    /**@author Elise Olthof
     * Deze functie geeft alle transacties, die gemaakt zijn binnen een bepaalde meegegeven periode,
     * terug
     * @param startDatum de startdatum vanaf het moment dat je het transactieoverzicht wilt
     * @param eindDatum de laatste datum waarop een transactie gemaakt kan zijn
     *
     * @return lijst met transacties die binnen de meegegeven start- en einddatum vallen
     * */
    @Override
    public List<Transactie> geefAlleTransactiesInPeriode(Timestamp startDatum, Timestamp eindDatum) {
        String sql = "SELECT * FROM transactie WHERE momentTransactie BETWEEN ? AND ?;";
        List<Transactie> transactiesInPeriode = null;
        try {
            transactiesInPeriode = jdbcTemplate.query(sql, new TransactieRowMapper()
                    , startDatum, eindDatum);
        } catch (EmptyResultDataAccessException exception){
            System.out.println("Geen data gevonden, exceptie: " + exception);
        }
        return transactiesInPeriode;
    }

    /**@author Elise Olthof
     * Deze functie geeft alle transacties van een meegegeven gebruiker(transactiepartij) en
     * een bepaalde cryptomunt terug
     * @param gebruiker de transactiepartij
     * @param cryptomunt de munt waarmee de transacties zijn gedaan
     *
     * @return lijst met transacties van de meegegeven gebruiker(transactiepartij) en de meegegeven
     * cryptomunt
     * */
    @Override
    public List<Transactie> geefTransactiesVanGebruikerMetCryptomunt(Gebruiker gebruiker, Cryptomunt cryptomunt) {
        String sql = "SELECT * FROM transactie WHERE (verkoperGebruikerId = ? OR koperGebruikerId = ?) AND cryptomuntId = ?;";

        List<Transactie> transactiesGebruiker = null;
        try {
            transactiesGebruiker = jdbcTemplate.query(sql, new TransactieRowMapper()
                    , gebruiker.getGebruikerId(), gebruiker.getGebruikerId()
                    , cryptomunt.getId());
        } catch (EmptyResultDataAccessException exception){
            System.out.println("Geen data gevonden, exceptie: " + exception);
        }
        return transactiesGebruiker;
    }

    /**@author Elise Olthof
     * Deze functie verwijdert een meegegeven transactie uit de database
     *
     * @param transactie de transactie die verwijderd moet worden
     * @return transactie die verwijderd is
     * */
    @Override
    public Transactie verwijderTransactie(Transactie transactie) {
        String sql = "DELETE FROM transactie WHERE transactieId = ?";

        int amountOfRows = jdbcTemplate.update(sql, transactie.getTransactieId());
        if(amountOfRows == 0){
            // TODO bedenk exception
        }
        return transactie;

    }



    private static class TransactieRowMapper implements RowMapper<Transactie> {

        /**@author Elise Olthof
         * deze functie wordt gebruikt om op basis van de opgehaalde database waarden een nieuw transactie
         * object en terug te geven. Hierbij worden ook de objecten die in dit transactieobject zitten aangemaakt
         * zoals de verkoper en koper, cryptomunt en de transactietriggers.
         * @param resultSet het restultaat van een sql-query
         * @param rowNumber het rownummber van waar de transactie in de resultset staat
         * @return transactie die uit de restultset gehaald wordt
         * @throws SQLException
         * */
        @Override
        public Transactie mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
            LocalDateTime dateTime = resultSet.getObject("momentTransactie", LocalDateTime.class);
            Gebruiker koper = new Klant();
            Gebruiker verkoper = new Klant();
            Cryptomunt cryptomunt = new Cryptomunt(resultSet.getInt("cryptomuntId"));
            koper.setGebruikerId(resultSet.getInt("koperGebruikerId"));
            verkoper.setGebruikerId(resultSet.getInt("verkoperGebruikerId"));
            Trigger triggerKoper = new TriggerKoper(koper, cryptomunt, resultSet.getDouble("bedrag"),resultSet.getDouble("aantal"));
            Trigger triggerVerkoper = new TriggerVerkoper(verkoper, cryptomunt, resultSet.getDouble("bedrag"),resultSet.getDouble("aantal"));
            Transactie transactie = new Transactie(dateTime, triggerKoper,  triggerVerkoper);
            transactie.setTransactieId(resultSet.getInt("transactieId"));
            transactie.setBankFee(resultSet.getDouble("bankFee"));
            return transactie;
        }
    }
}
