package com.example.thevault.handelingen.api;

import com.example.thevault.controller.rest_api_controller.Cryptomunt;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class CryptoAPI {

    private static HttpURLConnection connection;
    private static String apiKey = "ce1e8754-b694-41b5-99ca-dbb28dc5b68d";
    private static double waarde;
    //TODO Verwijderen?
    @JsonIgnore
    private final Logger logger = LoggerFactory.getLogger(CryptoAPI.class);

    //TODO JavaDoc
    public static double cryptoDagwaarde(Cryptomunt cryptomunt){
        String uri = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest";
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();

        parameters.add(new BasicNameValuePair("slug", cryptomunt.getName()));
        parameters.add(new BasicNameValuePair("convert","EUR"));

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        bepaalWaarde(cryptomunt, uri, parameters, objectMapper);
        return waarde;
    }

    private static void bepaalWaarde(Cryptomunt cryptomunt, String uri, List<NameValuePair> parameters, ObjectMapper objectMapper) {
        try {
            String result = makeAPICall(uri, parameters);
            JsonNode cryptoNode = objectMapper.readTree(result);
            waarde = (cryptoNode.at("/data/" + cryptomunt.getId() + "/quote/EUR/price").doubleValue());
        } catch (IOException e) {
            System.out.println("Error: cannot access content - " + e.toString());
        } catch (URISyntaxException e) {
            System.out.println("Error: Invalid URL " + e.toString());
        }
    }

    //TODO JavaDoc
    public static String makeAPICall(String uri, List<NameValuePair> parameters)
            throws URISyntaxException, IOException {
        String response_content = "";

        URIBuilder query = new URIBuilder(uri);
        query.addParameters(parameters);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(query.build());

        request.setHeader(HttpHeaders.ACCEPT, "application/json");
        request.addHeader("X-CMC_PRO_API_KEY", apiKey);

        response_content = getResponseContent(client, request);
        return response_content;
    }

    private static String getResponseContent(CloseableHttpClient client, HttpGet request) throws IOException {
        String response_content;
        CloseableHttpResponse response = client.execute(request);
        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            response_content = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }
        return response_content;
    }

}
