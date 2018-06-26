package es.linkeddata.librairy.loader.tasks;

import com.google.common.base.Strings;
import es.linkeddata.librairy.loader.Config;
import es.linkeddata.librairy.loader.client.LearnerClient;
import es.linkeddata.librairy.loader.reader.Reader;
import es.linkeddata.librairy.loader.reader.ReaderFactory;
import es.linkeddata.librairy.loader.service.ParallelService;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.librairy.service.learner.facade.rest.model.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class ReTrainModel {

    private static final Logger LOG = LoggerFactory.getLogger(ReTrainModel.class);

    @Test
    public void execute(){

        String propertiesPath = Strings.isNullOrEmpty(System.getenv("LOADER_PROPERTIES"))? "application.properties" : System.getenv("LOADER_PROPERTIES");

        Config config = new Config(propertiesPath);

        LearnerClient librairyClient = new LearnerClient(config.get("librairy.endpoint"),config.get("librairy.user"),config.get("librairy.pwd"));

        Map<String,String> readerParams = new HashMap<>();
        Map<String, String> modelParams = new HashMap<>();
        for (Object property: config.getProperties().keySet()){
            String key = (String) property;
            if (key.startsWith("model")){
                String param = StringUtils.substringAfter(key,"model.");
                String value = config.get(key);
                modelParams.put(param,value);
            }
            else if (key.startsWith("document")){
                String param = StringUtils.substringAfter(key,"document.");
                String value = config.get(key);
                readerParams.put(param,value);
            }
        }

        librairyClient.train(modelParams);
        LOG.info("model re-creation started");
    }

}
