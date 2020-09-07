package org.zhadaev.adapter.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Service;
import org.zhadaev.adapter.model.Coordinates;

@Service
public class GismeteoService implements IMeteoService {

    private final String meteoUrl = "https://api.gismeteo.net/v2/weather/current";
    private final String token = "";

    @Override
    public int getCurrentTemp(ProducerTemplate template, Coordinates coordinates, String lang) throws Exception {
        String url = meteoUrl.concat("?")
                .concat("lang=").concat(lang)
                .concat("&latitude=").concat(coordinates.getLatitude())
                .concat("&longitude=").concat(coordinates.getLongitude());
        Exchange exToMeteo = template.send(url, exchange -> {
            exchange.getMessage().setHeader("X-Gismeteo-Token", token);
        });
        String messageFromMeteo = exToMeteo.getMessage().getBody(String.class);

        ObjectMapper mapper = new ObjectMapper();
        Double t = null;
        try {
            JsonNode rootNode = mapper.readValue(messageFromMeteo, JsonNode.class);
            String temp = rootNode.get("temperature").get("air").get("C").asText();
            t = Double.parseDouble(temp);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (t == null) throw new Exception();

        return t.intValue();
    }

}
