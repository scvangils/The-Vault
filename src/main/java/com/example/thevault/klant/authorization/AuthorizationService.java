// Created by carme
// Creation date 01/12/2021

package com.example.thevault.klant.authorization;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.thevault.klant.Klant;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthorizationService {
    private final static String SECRET_KEY = "secretTheVaultKey5dRQPD_sCsArU";
    private int accessExpirationDateInMinutes = 2;
    public static TokenKlantCombinatieDao tokenKlantCombinatieDao;

    @JsonIgnore
    private final Logger logger = LoggerFactory.getLogger(AuthorizationService.class);


    /**
    * Deze klasse zorgt ervoor dat er een accesstoken en een refreshtoken worden aangemaakt waarmee
     * een gebruiker kan worden gecheckt. Het access token vervalt binnen een hier aangegeven aantal
     * minuten waarna een refreshtoken vanuit de database moet worden aangesproken om de accesstoken
     * te refreshen.
     *
    * */
    @Autowired
    public AuthorizationService(TokenKlantCombinatieDao tokenKlantCombinatieDao) {
        super();
        this.tokenKlantCombinatieDao = tokenKlantCombinatieDao;
        logger.info("New AuthorizationSupport");
    }
    /**
     * @author Elise Olthof
     * Genereert een refresh token op basis van de UUID (universally unique identifier)
     * library en bestaat uit een willekeurig gegenereerde 128-bit waarde
     *
     * @return refreshToken
     */
    public UUID genereerRefreshToken() {
        UUID refreshToken = UUID.randomUUID();
        logger.info("Nieuw refresh token opgehaald: {}", refreshToken);
        return refreshToken;
    }

    //TODO Kortere methodes van maken?
    /**
     * @author Elise Olthof
     * Genereert een access token middels een JWT (JSON Web Token). Hierbij wordt
     * het HMAC256 algorithme gebruikt om het token te signeren. De token
     * vervalt na een aangegeven aantal minuten via de accessExpirationDateInMs
     * en neemt daarnaast de gebruikersnaam van de meegegeven klant mee.
     *
     * @param klant de klant waarvoor het token bij inlog gegenereerd moet worden
     * @return String accesToken
     * @throws JWTCreationException als er een probleem is met het "signen" van
     * het token of als de meegegeven items niet kunnen worden omgezet naar JSON
     */
    public String genereerAccessToken(Klant klant) {
        String accessToken = null;
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);

            Instant gemaaktOp = Instant.now().truncatedTo(ChronoUnit.SECONDS);
            Instant verlooptOp = gemaaktOp.plus(accessExpirationDateInMinutes, ChronoUnit.MINUTES);

            logger.info("Gecreeerd op: {}", gemaaktOp);
            logger.info("Verloopt op: {}", verlooptOp);

            accessToken = JWT.create()
                    .withSubject(klant.getGebruikersnaam())
                    .withIssuedAt(Date.from(gemaaktOp))
                    .withExpiresAt(Date.from(verlooptOp))
                    .withIssuer("TVLT")
                    .sign(algorithm);

        } catch (JWTCreationException exception){
            logger.info("Invalid Signing configuration.", exception);
        }
        logger.info("Token (HMAC256) gemaakt: {}", accessToken);
        return accessToken;
    }

    //TODO Wat is beter try catch of een throw exception?????
    /**
     * @author Elise Olthof
     * Valideert het access token. Deze methode kan worden gebruikt zodra de klant
     * een nieuwe request verstuurt. De validatie van de methode hangt onder andere
     * af van de vervaldatum
     *
     * @param accessToken
     * @return boolean
     * @throws JWTVerificationException als het token niet gevalideerd wordt
     */
    public boolean valideerAccessToken(String accessToken, Klant klant){
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withSubject(klant.getGebruikersnaam())
                    .withIssuer("TVLT")
                    .build();
            DecodedJWT jwt = verifier.verify(accessToken);
            logger.info("Access token gevalideerd {}", jwt);
        } catch (JWTVerificationException exception) {
            logger.info("Access token niet gevalideerd");
            System.out.println("Exeption bericht :" + exception);
            return false;
        }
        return true;
    }

    /**
     * @author Elise Olthof
     * Authoriseert de klant doormiddel van het refresh token. Dit token wordt
     * in de database opgezocht. Als er als een klant-token combinatie bestaat
     * wordt het oude token weggegooid en wordt het nieuwe token opgeslagen
     * in de database. Dit token wordt later gebruikt bij het refreshen van
     * het JWT acces token zodat de klant geen last heeft van het verlopen van het
     * token
     *
     * @param klant
     * @return tokenKlantCombinatie
     * @throws JWTCreationException als er een probleem is met het "signen" van
     * het token of als de meegegeven items niet kunnen worden omgezet naar JSON
     */
    public TokenKlantCombinatie authoriseerIngelogdeKlantMetRefreshToken(Klant klant) {
        Optional<TokenKlantCombinatie> optioneleCombinatie = tokenKlantCombinatieDao.vindTokenKlantCombinatieMetKlant(klant);
        if (optioneleCombinatie.isPresent()) {
            tokenKlantCombinatieDao.delete(optioneleCombinatie.get().getKey());
        }
        return getNewTokenKlantCombinatie(klant);
    }

    private TokenKlantCombinatie getNewTokenKlantCombinatie(Klant klant) {
        UUID refreshToken = genereerRefreshToken();
        TokenKlantCombinatie tokenKlantCombinatie = new TokenKlantCombinatie(refreshToken, klant);
        tokenKlantCombinatieDao.slaTokenKlantPairOp(tokenKlantCombinatie);
        return tokenKlantCombinatie;
    }

    //TODO JavaDoc
    public TokenKlantCombinatie controleerRefreshToken(Klant klant, String refreshToken){
        Optional<TokenKlantCombinatie> optioneleCombinatie = tokenKlantCombinatieDao.vindTokenKlantCombinatieMetKlant(klant);
        if (optioneleCombinatie.isPresent()) {
            if(!optioneleCombinatie.get().getKey().toString().equals(refreshToken)){
                //TODO juiste exception
                return null;
            }
            tokenKlantCombinatieDao.delete(optioneleCombinatie.get().getKey());
        }
        return getNewTokenKlantCombinatie(klant);
    }


}
