package es.linkeddata.librairy.loader.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.librairy.service.learner.facade.rest.model.Document;
import org.librairy.service.learner.facade.rest.model.ModelParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public abstract class LibrairyClient {

    private static final Logger LOG = LoggerFactory.getLogger(LibrairyClient.class);

    static{
        Unirest.setDefaultHeader("Accept", "application/json");
        Unirest.setDefaultHeader("Content-Type", "application/json");

        com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
//        jacksonObjectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        jacksonObjectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        Unirest.setObjectMapper(new ObjectMapper() {

            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return jacksonObjectMapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public String writeValue(Object value) {
                try {
                    return jacksonObjectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }


    protected final String endpoint;

    public LibrairyClient(String endpoint) {
        this.endpoint = endpoint;
    }
}
