package org.zhadaev.adapter.service;

import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Service;
import org.zhadaev.adapter.model.Coordinates;

@Service
public interface IMeteoService {
    int getCurrentTemp(final ProducerTemplate template, final Coordinates coordinates, final String lang) throws Exception;
}
