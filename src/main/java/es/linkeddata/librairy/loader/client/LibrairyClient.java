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

public class LibrairyClient {

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

    private final String endpoint;
    private final String user;
    private final String pwd;

    public LibrairyClient(String endpoint, String user, String pwd) {
        this.endpoint = endpoint;
        this.user = user;
        this.pwd = pwd;
    }

    public boolean save(Document document){
        try {
            HttpResponse<String> response = Unirest.post(endpoint + "/documents").basicAuth(user, pwd).body(document).asString();

            if (response.getStatus() != 200 && response.getStatus() != 201){
                return false;
            }

            return true;

        } catch (UnirestException e) {
            LOG.error("Unexpected error",e);
            return false;
        }
    }

    public boolean reset(){
        try {
            HttpResponse<String> response = Unirest.delete(endpoint + "/documents").basicAuth(user, pwd).asString();

            if (response.getStatus() != 200 && response.getStatus() != 202){
                return false;
            }

            return true;

        } catch (UnirestException e) {
            LOG.error("Unexpected error",e);
            return false;
        }
    }

    public boolean train(Map<String, String> parameters){
        try {

            ModelParameters modelParameters = new ModelParameters();
//            Map<String, String> parameters = ImmutableMap.of(
//                    "algorithm","llda",
//                    "language","en",
//                    "email","cbadenes@fi.upm.es"
//            );
            modelParameters.setParameters(parameters);

            HttpResponse<String> response = Unirest.post(endpoint + "/dimensions").basicAuth(user, pwd).body(modelParameters).asString();

            if (response.getStatus() != 200 && response.getStatus() != 201 && response.getStatus() != 202){
                return false;
            }

            return true;

        } catch (UnirestException e) {
            LOG.error("Unexpected error",e);
            return false;
        }
    }

}
