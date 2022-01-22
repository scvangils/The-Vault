// Created by E.S. Olthof
// 20210712

package com.example.thevault.klant.authorization;

import com.example.thevault.klant.KlantDAO;
import com.example.thevault.klant.Klant;
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
import java.util.Optional;
import java.util.UUID;

@Repository
public class JDBCTokenKlantCombinatieDao implements TokenKlantCombinatieDao{

    private JdbcTemplate jdbcTemplate;
    private KlantDAO klantDAO;

    @JsonIgnore
    private final Logger logger = LoggerFactory.getLogger(AuthorizationService.class);

    /**
     * Deze klasse is gemaakt om de database aan te spreken voor het refreshtoken dat aan een klant
     * gekoppeld is op het moment dat er wordt ingelogd in het systeem.
     * */
    @Autowired
    public JDBCTokenKlantCombinatieDao(JdbcTemplate jdbcTemplate, KlantDAO klantDAO) {
        super();
        this.jdbcTemplate = jdbcTemplate;
        this.klantDAO = klantDAO;
        logger.info("New JDBCTokenKlantCombinatieDao.");
    }

    /**
     * Slaat een token met klant op in de database
     *
     * @param tokenKlantCombinatie
     * @return de tokenKlantCombinatie die wordt opgeslagen
     */
    @Override
    public TokenKlantCombinatie slaTokenKlantPairOp(TokenKlantCombinatie tokenKlantCombinatie) {
        jdbcTemplate.update(
                "insert into refreshToken (token, gebruikerId) values (?, ?)",
                tokenKlantCombinatie.getKey().toString(), tokenKlantCombinatie.getKlant().getGebruikerId());
        return tokenKlantCombinatie;
    }

    /**
     * Vind een token klant combinatie op basis van een megegeven token en geeft een
     * klant terug
     *
     * @param refreshToken
     * @return klant
    * */
    @Override
    public Optional<TokenKlantCombinatie> vindTokenKlantPairMetKey(UUID refreshToken) {
        List<TokenKlantCombinatie> tokenKlantCombinaties =
                jdbcTemplate.query(
                        "select * from refreshToken where token = ?", new ConnectionRowMapper(), refreshToken.toString());
        if (tokenKlantCombinaties.size() == 1) {
            return Optional.of(tokenKlantCombinaties.get(0));
        }
        return Optional.empty();
    }

    /**
     * Zoekt het refresh token op op basis van een klant als deze in de database staat
     *
     * @param klant
     * @return refresh token
     */
    @Override
    public Optional<TokenKlantCombinatie> vindTokenKlantCombinatieMetKlant(Klant klant) {
        List<TokenKlantCombinatie> tokenKlantCombinaties =
                jdbcTemplate.query(
                        "select * from refreshToken where gebruikerId = ?", new ConnectionRowMapper(), klant.getGebruikerId());
        if (tokenKlantCombinaties.size() == 1) {
            return Optional.of(tokenKlantCombinaties.get(0));
        }
        return Optional.empty();
    }

    /**
     * Delete een token
     *
     * @param uuid = refresh token
     * @return void
    * */
    @Override
    public UUID delete(UUID uuid) {
        jdbcTemplate.update("delete from refreshToken where token = ?", uuid.toString());
        logger.info("Verwijderde uuid " + uuid.toString());
        return uuid;
    }

    private class ConnectionRowMapper implements RowMapper<TokenKlantCombinatie> {


        /**
         * Deze methode zorgt ervoor dat er een tokenklant object wordt aangemaakt op basis
         * van de meegegeven resultset die wordt opgehaald uit de database en het rijnummer waarop
         * de tokenklantcombinatie te vinden is.
         * @param resultSet de opgehaalde data uit de databasetabel
         * @param i rijnummer van de betreffende token-klantcombinatie
         * @return tokenKlantcombinatie (bestaande uit het UUID token en een klantobject)
         * @throws SQLException
         * */
        @Override
        public TokenKlantCombinatie mapRow(ResultSet resultSet, int i) throws SQLException {
            UUID uuid = UUID.fromString(resultSet.getString("token"));
            int klantId = resultSet.getInt("gebruikerId");
            Klant klant = klantDAO.vindKlantById(klantId);
            return new TokenKlantCombinatie(uuid, klant);
        }
    }
}

