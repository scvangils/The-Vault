package com.example.thevault.controller.rest_api_controller;

import com.example.thevault.handelingen.*;
import com.example.thevault.klant.KlantService;
import com.example.thevault.klant.LoginService;
import com.example.thevault.klant.RegistrationService;
import com.example.thevault.klant.authorization.AuthorizationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest
class TransactieControllerTest {

    private MockMvc mockMvc;
    private TransactieController transactieController;
    @MockBean
    private KlantService klantService;
    @MockBean
    private RegistrationService registrationService;
    @MockBean
    private LoginService loginService;
    @MockBean
    private AuthorizationService authorizationService;
    @MockBean
    private TransactieService transactieService;
    @MockBean
    private CryptoHistorischService testService;
    @MockBean
    private TriggerService triggerService;

    @Autowired
    public TransactieControllerTest(MockMvc mockMvc){
        super();
        this.mockMvc = mockMvc;
    }

    TransactieStartDto testTransactieStartDto;
    TransactiePaginaDto testTransactiePaginaDto;

    @BeforeEach
    void setup(){
        testTransactieStartDto = new TransactieStartDto("Carmen", "Elrond");
        testTransactiePaginaDto = new TransactiePaginaDto();
        testTransactiePaginaDto.setKlantnaam("Carmen");
        testTransactiePaginaDto.setRekeningsaldo(100.0);
        testTransactiePaginaDto.setIban("NL20INGB0006582287");
        testTransactiePaginaDto.setCryptoNaam("Elrond");
        testTransactiePaginaDto.setCryptoDagkoers(20.5);
        testTransactiePaginaDto.setCryptoAantal(6.3);
        testTransactiePaginaDto.setBankfee(5.0);
    }

    @Test
    void transactieAanvraagHandler() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String testTransactieStartJson = objectMapper.writeValueAsString(testTransactieStartDto);
        Mockito.when(transactieService.openTransactiescherm(testTransactieStartDto)).thenReturn(testTransactiePaginaDto);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/transaction");
        requestBuilder.content(testTransactieStartJson).contentType(MediaType.APPLICATION_JSON);
        try {
            ResultActions response = mockMvc.perform(requestBuilder);
            response.andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().json("{\"klantnaam\":\"Carmen\"," +
                            "\"rekeningsaldo\":100.0,\"iban\":\"NL20INGB0006582287\",\"cryptoNaam\":\"Elrond\"," +
                            "\"cryptoDagkoers\":20.5,\"cryptoAantal\":6.3,\"bankfee\":5.0}"))
                    .andDo(MockMvcResultHandlers.print());
        } catch (Exception e){
            System.out.println("dit gaat niet goed: " + e);
            fail();
        }
    }

}