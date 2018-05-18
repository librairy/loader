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

public class TrainModel {

    private static final Logger LOG = LoggerFactory.getLogger(TrainModel.class);

    @Test
    public void execute(){

        String propertiesPath = Strings.isNullOrEmpty(System.getenv("LOADER_PROPERTIES"))? "application.properties" : System.getenv("LOADER_PROPERTIES");

        Config config = new Config(propertiesPath);

        File corpusFile = Paths.get(config.get("corpus.file")).toFile();

        if (!corpusFile.exists()){
            Assert.fail("File does not exist! " + corpusFile.getAbsolutePath());
        }

        ParallelService parallelService = new ParallelService();
        LearnerClient librairyClient = new LearnerClient(config.get("librairy.endpoint"),config.get("librairy.user"),config.get("librairy.pwd"));

        if (!librairyClient.reset()){
            Assert.fail("Communication error to librAIry");
        }


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

        String corpusFormat = config.get("corpus.format");
        Reader reader = ReaderFactory.newFrom(corpusFile, corpusFormat,readerParams);

        Optional<Document> doc;
        AtomicInteger counter = new AtomicInteger();
        Integer interval    = config.exists("corpus.interval")? Integer.valueOf(config.get("corpus.interval")): 100;
        Integer maxSize     = config.exists("corpus.size")? Integer.valueOf(config.get("corpus.size")) : -1;
        Integer offset      = config.exists("corpus.offset")? Integer.valueOf(config.get("corpus.offset")) : 0;
        reader.offset(offset);
        while(( maxSize<0 || counter.get()<=maxSize) &&  (doc = reader.next()).isPresent()){
            if (counter.incrementAndGet() % interval == 0) LOG.info(counter.get() + " documents indexed");
            final Document document = doc.get();
            parallelService.execute(() -> librairyClient.save(document));
        }
        parallelService.stop();
        LOG.info(counter.get() + " documents indexed");
        LOG.info("done!");

        librairyClient.train(modelParams);
        LOG.info("model creation started");
    }

}
