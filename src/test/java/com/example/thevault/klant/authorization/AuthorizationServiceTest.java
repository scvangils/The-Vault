// Elise Olthof
// 20210912

package com.example.thevault.klant.authorization;

import com.auth0.jwt.JWT;


import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.thevault.controller.rest_api_controller.RootRepository;
import com.example.thevault.klant.Klant;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;


class AuthorizationServiceTest {

    private Klant nieuweKlant;
    public Klant testKlant;
    public String testVerlopenAccessToken;
    public UUID testRefreshToken;
    public String secret_key = "secretTheVaultKey5dRQPD_sCsArU";
    public RootRepository mockRepo;
    public TokenKlantCombinatieDao mockTokenKlantCombinatieDao;
    public AuthorizationService authorizationServiceTest = new AuthorizationService(mockTokenKlantCombinatieDao);
    private final int negatieveVerlooptijd = -1;
    String testAccessTokenFromService;


    @JsonIgnore
    private final Logger logger = LoggerFactory.getLogger(AuthorizationService.class);


    @BeforeEach
    public void setup(){
        testKlant = new Klant( "Henknr1", "Welkom01", "Henk", 1890393, LocalDate.of(1991, 1, 12));
        nieuweKlant = new Klant("TimoBaasnummer1", "DuinDeBeste01", "Timo", 1890393, LocalDate.of(1992, 1, 10));
        mockRepo = Mockito.mock(RootRepository.class);
        mockTokenKlantCombinatieDao = Mockito.mock(TokenKlantCombinatieDao.class);
        authorizationServiceTest = new AuthorizationService(mockTokenKlantCombinatieDao);
        setTestVerlopenAccessToken();
        testRefreshToken = UUID.randomUUID();
    }

    /**
     * Methode om een testToken aan te maken waarvan de
     * tijd verstreken is, deze wordt in de methode
     * testVerlopenAccesToken aageropen
     *
     * */
    public void setTestVerlopenAccessToken (){
        // Genereer JWT token met verlopen tijd
        Algorithm algorithm = Algorithm.HMAC256(secret_key);

        Instant gemaaktOp = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Instant verlooptOp = gemaaktOp.plus(negatieveVerlooptijd, ChronoUnit.MINUTES);

        testVerlopenAccessToken = JWT.create()
                .withSubject(testKlant.getGebruikersnaam())
                .withIssuedAt(Date.from(gemaaktOp))
                .withExpiresAt(Date.from(verlooptOp))
                .withIssuer("TVLT")
                .sign(algorithm);
    }

    @Test
    void genereerRefreshToken() {
        testRefreshToken = UUID.randomUUID();
        assertThat(testRefreshToken).isNotNull().isInstanceOf(UUID.class);
        logger.info("UUID refresh token notNull en instantie UUID");
    }

    @Test
    void genereerAccessToken() {
        testAccessTokenFromService = authorizationServiceTest.genereerAccessToken(testKlant);

        assertThat(testAccessTokenFromService).isNotNull().isInstanceOf(String.class).contains("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9");

        logger.info("testAccessTokenFromService isNotNull en bevat juiste encodering");
    }

    /**
     * !!!TO DO
     *
     * Kijken of de methode genereerAccessToken de juiste exception vangt
     *
     * @return void
     * */
    @Test
    void genereerAccessTokenCatchError() {
        testAccessTokenFromService = authorizationServiceTest.genereerAccessToken(new Klant());
        DecodedJWT decodedJwt = JWT.decode(testAccessTokenFromService);

        assertThat(testAccessTokenFromService).isNotNull().isInstanceOf(String.class).contains("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9");
        logger.info("testAccessTokenFromService isNotNull en bevat juiste encodering");
    }


    @Test
    void valideerAccessTokenFromAuthorizationService() {
        testAccessTokenFromService = authorizationServiceTest.genereerAccessToken(new Klant());

        authorizationServiceTest.valideerAccessToken(testAccessTokenFromService, testKlant);

        // print alle eigenschappen van het acces token
        DecodedJWT decodedJwt = JWT.decode(testAccessTokenFromService);
        printOutComesTokenValidation(decodedJwt);
    }

    @Test
    void valideerAccessTokenMetVerkeerdeKlant() {
            testAccessTokenFromService = authorizationServiceTest.genereerAccessToken(new Klant());
            boolean returnedBooleanValue = authorizationServiceTest.valideerAccessToken(testAccessTokenFromService, new Klant( "harrynr1", "Welkom01", "Henk", 1890393, LocalDate.of(1991, 1, 12)));
            // print alle eigenschappen van het token
            DecodedJWT decodedJwt = JWT.decode(testAccessTokenFromService);
            printOutComesTokenValidation(decodedJwt);
            // als de boolean false is is de test geslaagd
            if(!returnedBooleanValue){
                System.out.println("test geslaagd");
            } else {
                fail("Moet een JWTVerificationException gooien met InvalidClaimException en false teruggeven");
            }
    }

    @Test
    void valideerAccessTokenVerlopen(){
        boolean outcomeValidatie = authorizationServiceTest.valideerAccessToken(testVerlopenAccessToken, testKlant);
        // print alle eigenschappen van het token
        DecodedJWT decodedJwt = JWT.decode(testVerlopenAccessToken);
        printOutComesTokenValidation(decodedJwt);
        if(!outcomeValidatie){
            System.out.println("test geslaagd");
        } else{
            fail("Moet een JWTVerificationException gooien en false teruggeven");
        }
    }


    public void printOutComesTokenValidation (DecodedJWT decodedAccesToken) {
        System.out.println("Header =  " + decodedAccesToken.getHeader());
        System.out.println("Algorithm =  " + decodedAccesToken.getAlgorithm());
        System.out.println("Audience =  " + decodedAccesToken.getAudience());
        decodedAccesToken.getClaims().forEach((k, v) -> {
            System.out.println("Claim " + k + " = " + v.asString());
        });
        System.out.println("ContentType =  " + decodedAccesToken.getContentType());
        System.out.println("ExpiresAt =  " + decodedAccesToken.getExpiresAt());
        System.out.println("Id =  " + decodedAccesToken.getId());
        System.out.println("Issuer =  " + decodedAccesToken.getIssuer());
        System.out.println("Subject =  " + decodedAccesToken.getSubject());
    }


    @Test
    void authoriseerKlantMetRefreshTokenNieuweKlant() {

        TokenKlantCombinatie expectedTokenKlantCombinatie = new TokenKlantCombinatie (testRefreshToken, nieuweKlant);

        Mockito.when(mockTokenKlantCombinatieDao.vindTokenKlantCombinatieMetKlant(nieuweKlant)).thenReturn(Optional.empty());
        Mockito.when(mockTokenKlantCombinatieDao.slaTokenKlantPairOp(expectedTokenKlantCombinatie)).thenReturn(expectedTokenKlantCombinatie);

        TokenKlantCombinatie actualTokenKlantCombinatie = authorizationServiceTest.authoriseerIngelogdeKlantMetRefreshToken(nieuweKlant);

        assertThat(actualTokenKlantCombinatie.getKlant().getNaam().equals(expectedTokenKlantCombinatie.getKlant().getNaam()));
        assertThat(actualTokenKlantCombinatie.getKey()).isNotNull().isInstanceOf(expectedTokenKlantCombinatie.getKey().getClass());
    }

    @Test
    void authoriseerKlantMetRefreshTokenBestaandeKlant() {
        // test of de refreshtoken wordt geupdated
        TokenKlantCombinatie startTokenKlantCombinatie = new TokenKlantCombinatie (testRefreshToken, testKlant);

        Mockito.when(mockTokenKlantCombinatieDao.vindTokenKlantCombinatieMetKlant(testKlant)).thenReturn(Optional.of(startTokenKlantCombinatie));
        Mockito.when(mockTokenKlantCombinatieDao.slaTokenKlantPairOp(startTokenKlantCombinatie)).thenReturn(startTokenKlantCombinatie);

        TokenKlantCombinatie updatetTokenKlantCombinatie = authorizationServiceTest.authoriseerIngelogdeKlantMetRefreshToken(testKlant);

        //controleer of de nieuwe combinatie wel dezelfde klant bevat maar niet dezelfde key
        assertThat(updatetTokenKlantCombinatie.getKlant().getNaam().equals(startTokenKlantCombinatie.getKlant().getNaam()));
        assertThat(updatetTokenKlantCombinatie.getKey()).isNotNull().isNotEqualTo(startTokenKlantCombinatie.getKey());

    }



}