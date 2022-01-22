package com.example.thevault.database.mapping.dao;

import com.example.thevault.domain.model.Cryptomunt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class JDBCCryptoWaardeDAOTest {

    public static String testCryptoWaardeId1;
    public static Cryptomunt testCryptomunt1;
    public static LocalDate testDatum1;
    //TODO checken of dit juiste DAO is om aan te roepen
    JDBCCryptoWaardeDAO jdbcCryptoWaardeDAO;



    @BeforeEach
    void setUp() {
        jdbcCryptoWaardeDAO = new JDBCCryptoWaardeDAO(new JdbcTemplate());
        testCryptoWaardeId1 = "20210205BTC";
        testCryptomunt1 = new Cryptomunt(1, "Bitcoin", "BTC");
        testDatum1 = LocalDate.of(2021, 2, 5);
    }

    @Test
    void getCryptoWaardeByCryptomunt() {
    }

    @Test
    void getCryptoWaardeByCryptomuntAndDate() {
    }

    @Test
    void slaCryptoWaardeOp() {
    }

    @Test
    void generateCryptoWaardeId() {
        String expected = testCryptoWaardeId1;
        String actual = jdbcCryptoWaardeDAO.generateCryptoWaardeId(testDatum1, testCryptomunt1);
        assertEquals(expected, actual);
    }
}