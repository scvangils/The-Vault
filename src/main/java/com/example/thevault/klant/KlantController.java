// Created by S.C. van Gils
// Creation date 1-12-2021

package com.example.thevault.klant;

import com.example.thevault.handelingen.TransactieService;
import com.example.thevault.klant.authorization.AuthorizationService;
import com.example.thevault.klant.authorization.TokenKlantCombinatie;
import com.example.thevault.support.exceptions.LoginFailedException;
import com.example.thevault.support.exceptions.RefreshJWTFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class KlantController extends BasisApiController {

    private final Logger logger = LoggerFactory.getLogger(KlantController.class);
    private final KlantService klantService;

    //TODO JavaDoc
    public KlantController(RegistrationService registrationService,
                           AuthorizationService authorizationService, LoginService loginService,
                           TransactieService transactieService, KlantService klantService) {
        super(registrationService, authorizationService, loginService, transactieService);
        this.klantService = klantService;
        logger.info("New KlantController");
    }

    /**
     *  Deze methode zorgt ervoor dat ingevoerde registratiegegevens in een Klant-object
     *  worden omgezet, waarna het registratieproces in gang wordt gezet.
     *  Als alles goed gaat, krijgt de klant de voor hem relevante informatie,
     *  met daarin zijn nieuwe rekeningnummer, terug alsmede een HTTP-respons 201 = created.
     *
     * @param klant een Klant-object dat wordt aangemaakt op basis van ingevoerde gegevens
     * @return een DTO waar de relevante klantgegevens in staan als de klant succesvol is opgeslagen
     */
    @PostMapping("/register")
    public ResponseEntity<RegistrationDto> registreerKlantHandler(@RequestBody Klant klant){
        RegistrationDto registrationDto = registrationService.registreerKlant(klant);
    return ResponseEntity.status(HttpStatus.CREATED).body(registrationDto);
    }

    //TODO JavaDoc
    @PostMapping("/klantDashboard") // 401 response nodig indien token niet geldig
    public ResponseEntity<String> clientDashboardHandler(@RequestHeader("Authorization") String token, @RequestBody String inhoud){
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(inhoud);
    }

    //TODO waar gebruikersnaam vandaan halen?
    //TODO JavaDoc
    @PostMapping("/validate/jwt")
    public ResponseEntity<String> valideerJWT(@RequestHeader("Authorization") String accessToken, @RequestBody String gebruikersnaam){
        accessToken = accessToken.substring(7); // Verwijder "Bearer "
        Klant klant = loginService.vindKlantByGebruikersnaam(gebruikersnaam);
        boolean accessAllowed = authorizationService.valideerAccessToken(accessToken, klant);
                if(accessAllowed){
                    return ResponseEntity.ok().header("Authorization", "Bearer " + accessToken).body("toegestaan");
                }
                return ResponseEntity.status(401).body("niet toegestaan");
    }
    //TODO return type aanpassen en dubbele code beperken
    //TODO JavaDoc
    @PostMapping("/validate/refresh")
    public ResponseEntity<WelkomDTO> valideerRefreshToken(@CookieValue("RefreshToken") String refreshToken, @RequestBody String gebruikersnaam){
        Klant klant = loginService.vindKlantByGebruikersnaam(gebruikersnaam);
        if(klant != null){
            TokenKlantCombinatie tokenKlantCombinatie = authorizationService.controleerRefreshToken(klant, refreshToken);
            if(tokenKlantCombinatie == null){
                throw new RefreshJWTFailedException();
            }
        ResponseCookie responseCookie = getResponseCookie(tokenKlantCombinatie);
        // genereer JWT token
        String jwtToken = authorizationService.genereerAccessToken(klant);
        // hier moeten de tokens worden toegevoegd aan de header
        return getResponseEntity(klant, responseCookie, jwtToken);
        }
        throw new RefreshJWTFailedException();
    }

    /**
    @author Wim 20211207
    methode die inloggegevens ontvangt en laat checken of deze correct zijn en inlog succesvol wordt
    of een algemene foutmelding verstuurt
     */
    @PostMapping("/login")
    public ResponseEntity<WelkomDTO> loginHandler(@RequestBody LoginDto loginDto) throws LoginFailedException {
        //roep loginValidatie aan in de service
        Klant klant = loginService.valideerLogin(loginDto);
        if(klant != null){
            // haal opaak token op uit database
            TokenKlantCombinatie tokenKlantCombinatie = authorizationService.authoriseerIngelogdeKlantMetRefreshToken(klant);
            // genereer cookie met het opgehaalde opaakToken
            ResponseCookie responseCookie = getResponseCookie(tokenKlantCombinatie);
            // genereer JWT token
            String jwtToken = authorizationService.genereerAccessToken(klant);
            // hier moeten de tokens worden toegevoegd aan de header
            return getResponseEntity(klant, responseCookie, jwtToken);
        }
        throw new LoginFailedException();
    }

    private ResponseEntity<WelkomDTO> getResponseEntity(Klant klant, ResponseCookie responseCookie, String jwtToken) {
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + jwtToken)
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(new WelkomDTO(klant, klantService.geefNuttigePortefeuille(klant)));
    }

    private ResponseCookie getResponseCookie(TokenKlantCombinatie tokenKlantCombinatie) {
        return ResponseCookie.from("RefreshToken",
                tokenKlantCombinatie.getKey().toString()).path("/validate/refresh").httpOnly(true).build();
    }


}
