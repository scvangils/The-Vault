// Created by carme
// Creation date 03/12/2021

package com.example.thevault.financieel;

import com.example.thevault.klant.Gebruiker;
import com.example.thevault.handelingen.NotEnoughCryptoException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Author: Carmen
 * Beschrijving: De implementatie van de DAO voor Asset, waarin zowel de CRUD functionaliteit van Asset wordt geregeld,
 * alsmede het ophalen van de benodigde informatie voor het vullen van de portefeuille van de klant
 */

@Repository
public class JDBCAssetDAO implements AssetDAO{

    @JsonIgnore
    private final Logger logger = LoggerFactory.getLogger(JDBCAssetDAO.class);

    private JdbcTemplate jdbcTemplate;
    private Supplier<AssetNotExistsException> AssetNotExistsException;

    /**
     * Constructor voor JDBCAssetDAO
     * @param jdbcTemplate injectie van het jdbcTemplate in de constructor
     */
    @Autowired
    public JDBCAssetDAO(JdbcTemplate jdbcTemplate) {
        super();
        this.jdbcTemplate = jdbcTemplate;
        logger.info("New JDBCAssetDAO");
    }

        //TODO versimpelen?
    private PreparedStatement slaAssetOpStatement(Asset asset, Connection connection) throws SQLException {
        String sql = "INSERT INTO asset (gebruikerId, cryptomuntId, aantal) values " +
                "(?, ?, ?);";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, asset.getGebruiker().getGebruikerId());
        ps.setInt(2, asset.getCryptomunt().getId());
        ps.setDouble(3, asset.getAantal());
        return ps;
    }

    private PreparedStatement verwijderAssetStatement(Asset asset, Connection connection) throws SQLException {
        String sql = "DELETE FROM asset WHERE gebruikerId = ? AND cryptomuntId = ?;";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, asset.getGebruiker().getGebruikerId());
        ps.setInt(2, asset.getCryptomunt().getId());
        return ps;
    }

    private static class AssetRowMapper implements RowMapper<Asset> {
        @Override
        public Asset mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            Cryptomunt cryptomunt = new Cryptomunt(resultSet.getInt("cryptomuntId"));
            return new Asset(cryptomunt, resultSet.getDouble("aantal"));
        }
    }

    /**
     * Dit betreft het toevoegen van een cryptomunt die nog niet in de portefeuille zit
     * Dit gebeurt via een 'transactie', waarbij een klant crypto's koopt
     * @param asset de cryptomunt en het aantal dat de klant aanschaft
     * @return Asset de asset die de klant heeft toegevoegd
     */
    @Override
    public Asset voegNieuwAssetToeAanPortefeuille(Asset asset) {
        jdbcTemplate.update(connection -> slaAssetOpStatement(asset, connection));
        return asset;
    }

    //TODO Besluiten of deze methode nodig is. Eigenlijk is 'verwijderen' een speciale situatie van 'updaten' waarbij de
    // hoeveelheid verkochte cryptomunt gelijk is aan de hoeveelheid aanwezig in de portefeuille.
    /**
     * Dit betreft het verwijderen van een cryptomunt uit de portefeuille
     * Dit gebeurt via een 'transactie', waarbij een klant crypto's verkoopt
     * @param asset de asset die uit de portefeuille wordt verwijderd
     * @return String bericht dat de cryptomunt uit de portefeuille is verwijderd
     */
    @Override
    public Asset verwijderAssetUitPortefeuille(Asset asset) {
        jdbcTemplate.update(connection -> verwijderAssetStatement(asset, connection));
        asset.setAantal(0);
        return asset;
    }

    //TODO Besluiten of dit twee methodes moeten worden: kopen vs verkopen. Op zich gebeurt er in beide gevallen
    // hetzelfde, namelijk het aanpassen van het aantal van de cryptomunt.
    /**
     * Dit betreft het updaten van een cryptomunt die al in de portefeuille zit
     * Dit gebeurt via een 'transactie', waarbij een klant crypto's koopt of verkoopt
     * @param gebruiker de asset waarin de klant handelt, met de informatie wélke cryptomunt wordt verhandeld
     *              en hoeveel deze omhoog/omlaag gaat (oftewel: betreft het een koop of een verkoop)
     * @param cryptomunt
     * @param aantal
     * @return Asset de asset na de update, waarbij het nieuwe aantal wordt meegegeven
     */
    @Override // aantal is positief voor koper en negatief voor verkoper
    public Asset updateAsset(Gebruiker gebruiker, Cryptomunt cryptomunt, double aantal) {
        String sql = "UPDATE asset SET aantal = ? WHERE gebruikerId = ? AND cryptomuntId = ?;";
        Optional<Asset>  optionalAsset = geefAssetGebruiker(gebruiker, cryptomunt);
        // kijk of verkoper asset bezit en genoeg ervan heeft
        genoegCrypto(aantal, optionalAsset);
        //controleer of koper deze asset al bezit
        if(optionalAsset.isEmpty()) {
            return voegNieuwAssetToeAanPortefeuille(new Asset(cryptomunt, aantal, gebruiker));
        }
        jdbcTemplate.update(sql, (optionalAsset.get().getAantal() + aantal), gebruiker.getGebruikerId(), cryptomunt.getId());
        return geefAssetGebruiker(gebruiker, cryptomunt).get();
    }

    /**
     * Dit betreft een controle of een verkoper voldoende cryptomunt bezit om de transactie door te zetten
     * @param aantal het aantal cryptomunten dat de klant wil verkopen
     * @param optionalAsset de asset die de verkoper wil verkopen
     */
    private void genoegCrypto(double aantal, Optional<Asset> optionalAsset) {
        if(aantal < 0) {
            if (optionalAsset.isEmpty() || optionalAsset.get().getAantal() < -aantal) {
                throw new NotEnoughCryptoException();
            }
        }
    }

    /**
     * Dit betreft het vinden van een cryptomunt die in de portefeuille zit
     * @param gebruiker klant die informatie opvraagt over de cryptomunt
     * @param cryptomunt cryptomunt waarover informatie wordt opgevraagd
     * @return Asset de asset (cryptomunt + aantal) waarover informatie is opgevraagd
     */

    @Override
    public Optional<Asset> geefAssetGebruiker(Gebruiker gebruiker, Cryptomunt cryptomunt) {
        String sql = "Select * from asset where gebruikerId = ? AND cryptomuntId = ?;";
        List<Asset> assets = jdbcTemplate.query(sql, new JDBCAssetDAO.AssetRowMapper(), gebruiker.getGebruikerId(),
                cryptomunt.getId());
        if(assets.size() == 1){
            assets.get(0).setGebruiker(gebruiker);
            return Optional.of(assets.get(0));
        }
        return Optional.empty();
    }

    /**
     * Dit betreft het vinden van alle cryptomunten die in de portefeuille zitten
     * @param gebruiker klant die informatie opvraagt over de cryptomunt
     * @return List</Asset> een lijst van alle Assets (cryptomunten + hoeveelheden) in het bezit van de klant
     */
    @Override
    public List<Asset> geefAlleAssets(Gebruiker gebruiker){
        String sql = "SELECT * FROM asset WHERE gebruikerId = ?;";
        List<Asset> assets;
        assets = jdbcTemplate.query(sql, new JDBCAssetDAO.AssetRowMapper(), gebruiker.getGebruikerId());
        if(assets.size() == 0) {
            return assets;
            } else {
            for (Asset asset : assets) {
                asset.setGebruiker(gebruiker);
            }
        }
        return assets;
    }

    //TODO JavaDoc
    //WIBUL aantal in eigendom per gebruiker
    public double geefAantalCryptoInEigendom(Gebruiker gebruiker, Cryptomunt cryptomunt) {
        double aantal;
        String sql = "Select * from asset where gebruikerId = ? AND cryptomuntId = ?;";
        try{aantal = jdbcTemplate.queryForObject(sql, new JDBCAssetDAO.AssetRowMapper(), gebruiker.getGebruikerId(), cryptomunt.getId()).getAantal();}
        catch (EmptyResultDataAccessException geenCryptoEigendom){
            aantal = 0;
        }
        return aantal;
    }
}
