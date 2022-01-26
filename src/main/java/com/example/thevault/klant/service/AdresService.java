/* @author Wim
check of postcode voldoet aan opmaak CCCCLL
//TODO ophalen adres adhv postcode huisnummer
*/

package com.example.thevault.klant.service;

import org.springframework.stereotype.Service;

@Service
public class AdresService {

    //TODO JavaDoc
    public AdresService() {
        super();
    }

    //TODO JavaDoc
    public static final String REGEX_POSTCODE = "[1-9][0-9]{3}[a-zA-Z]{2}";

    //TODO JavaDoc
    public boolean postcodeOpmaak(String postcode){
        return postcode.matches(REGEX_POSTCODE);
    }
}
