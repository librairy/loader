package es.linkeddata.librairy.loader;

import es.linkeddata.librairy.loader.client.LibrairyClient;
import es.linkeddata.librairy.loader.reader.Reader;
import es.linkeddata.librairy.loader.reader.ReaderFactory;
import es.linkeddata.librairy.loader.service.ParallelService;
import org.apache.commons.lang.StringUtils;
import org.librairy.service.learner.facade.rest.model.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {


        File corpusFile = Paths.get(Config.get("file.path")).toFile();

        if (!corpusFile.exists()){
            LOG.warn("File does not exist! " + corpusFile.getAbsolutePath());
            return;
        }

        ParallelService parallelService = new ParallelService();
        LibrairyClient librairyClient = new LibrairyClient(Config.get("librairy.endpoint"),Config.get("librairy.user"),Config.get("librairy.pwd"));

        if (!librairyClient.reset()){
            LOG.error("Communication error to librAIry");
            return;
        }

        Reader reader = ReaderFactory.newFrom(Config.getProperties());

        Optional<Document> doc;
        AtomicInteger counter = new AtomicInteger();
        while((doc = reader.next()).isPresent()){
            if (counter.incrementAndGet() % 100 == 0) LOG.info(counter.get() + " documents indexed");
            final Document document = doc.get();
            parallelService.execute(() -> librairyClient.save(document));
        }
        parallelService.stop();
        LOG.info(counter.get() + " documents indexed");
        LOG.info("done!");

        Map<String, String> parameters = new HashMap<>();

        for (Object property: Config.getProperties().keySet()){
            String key = (String) property;
            if (key.startsWith("model")){
                String param = StringUtils.substringAfter(key,"model.");
                String value = Config.get(key);
                parameters.put(param,value);
            }


        }

        librairyClient.train(parameters);
        LOG.info("model creation started");

    }

}
