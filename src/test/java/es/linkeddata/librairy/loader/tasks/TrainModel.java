package es.linkeddata.librairy.loader.tasks;

import com.google.common.base.Strings;
import es.linkeddata.librairy.loader.Config;
import es.linkeddata.librairy.loader.PropertiesConfig;
import es.linkeddata.librairy.loader.client.LearnerClient;
import es.linkeddata.librairy.loader.reader.Reader;
import es.linkeddata.librairy.loader.reader.ReaderFactory;
import es.linkeddata.librairy.loader.service.ModelService;
import es.linkeddata.librairy.loader.service.ParallelService;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.librairy.service.learner.facade.rest.model.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class TrainModel {

    private static final Logger LOG = LoggerFactory.getLogger(TrainModel.class);

    @Test
    public void execute(){

        String propertiesPath = Strings.isNullOrEmpty(System.getenv("LOADER_PROPERTIES"))? "application.properties" : System.getenv("LOADER_PROPERTIES");

        ModelService modelService = new ModelService();

        try {
            modelService.train(new PropertiesConfig(propertiesPath), false);
        } catch (Exception e) {
            LOG.error("Unexpected error", e);
        }

    }

}
