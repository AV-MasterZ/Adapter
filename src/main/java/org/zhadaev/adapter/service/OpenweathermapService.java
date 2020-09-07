package org.zhadaev.adapter.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.*;
import org.springframework.stereotype.Service;
import org.zhadaev.adapter.model.Coordinates;

@Service
public class OpenweathermapService implements IMeteoService {

    private final String meteoUrl = "https://api.openweathermap.org/data/2.5/weather";
    private final String appid = "8ca4ffd6ec8420eb4e57e43c175e01a5";

    @Override
    public int getCurrentTemp(final ProducerTemplate template, final Coordinates coordinates, final String lang) throws Exception {

        String url = meteoUrl.concat("?")
                            .concat("lang=").concat(lang)
                            .concat("&lat=").concat(coordinates.getLatitude())
                            .concat("&lon=").concat(coordinates.getLongitude())
                            .concat("&appid=").concat(appid);
        Exchange exToMeteo = template.send(url, exchange -> {});
        String messageFromMeteo = exToMeteo.getMessage().getBody(String.class);

        ObjectMapper mapper = new ObjectMapper();
        Double t = null;
        try {
            JsonNode rootNode = mapper.readValue(messageFromMeteo, JsonNode.class);
            String temp = rootNode.get("main").get("temp").asText();
            t = Double.parseDouble(temp) - 273.15;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (t == null) throw new Exception();

        return t.intValue();
    }
}
