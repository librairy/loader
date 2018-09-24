package es.linkeddata.librairy.loader.service;

import es.linkeddata.librairy.loader.Config;
import es.linkeddata.librairy.loader.client.LearnerClient;
import es.linkeddata.librairy.loader.reader.Reader;
import es.linkeddata.librairy.loader.reader.ReaderFactory;
import org.apache.commons.lang.StringUtils;
import org.librairy.service.learner.facade.rest.model.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class ModelService {

    private static final Logger LOG = LoggerFactory.getLogger(ModelService.class);

    public void train(Config config, Boolean fg) throws IOException {

        ParallelService parallelService = new ParallelService();
        LearnerClient librairyClient = new LearnerClient(config.get("librairy.endpoint"),config.get("librairy.user"),config.get("librairy.pwd"));

        if (!librairyClient.reset()){
            throw new RuntimeException("Communication error to librAIry");
        }


        Map<String,String> readerParams = new HashMap<>();
        Map<String, String> modelParams = new HashMap<>();
        for (String key: config.list()){
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
        Reader reader = ReaderFactory.newFrom(config.get("corpus.file"), corpusFormat,readerParams);

        Optional<Document> doc;
        AtomicInteger counter = new AtomicInteger();
        Integer interval    = config.exists("corpus.interval")? Integer.valueOf(config.get("corpus.interval")): 100;
        Integer maxSize     = config.exists("corpus.size")? Integer.valueOf(config.get("corpus.size")) : -1;
        Integer offset      = config.exists("corpus.offset")? Integer.valueOf(config.get("corpus.offset")) : 0;
        Boolean multigrams  = config.exists("corpus.multigrams")? Boolean.valueOf(config.get("corpus.multigrams")) : false;
        reader.offset(offset);
        while(( maxSize<0 || counter.get()<=maxSize) &&  (doc = reader.next()).isPresent()){
            if (counter.incrementAndGet() % interval == 0) LOG.info(counter.get() + " documents indexed");
            final Document document = doc.get();
            parallelService.execute(() -> librairyClient.save(document, multigrams));
        }
        parallelService.stop();
        LOG.info(counter.get() + " documents indexed");
        LOG.info("done!");

        librairyClient.train(modelParams);
        LOG.info("model creation started");

        if (fg) waitForFinish(librairyClient);

    }


    public void inference(Config config, Boolean fg) throws IOException {

        ParallelService parallelService = new ParallelService();
        LearnerClient librairyClient = new LearnerClient(config.get("librairy.endpoint"),config.get("librairy.user"),config.get("librairy.pwd"));


        Map<String,String> readerParams = new HashMap<>();
        Map<String, String> modelParams = new HashMap<>();
        for (String key: config.list()){
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
        Reader reader = ReaderFactory.newFrom(config.get("corpus.file"), corpusFormat,readerParams);

        Optional<Document> doc;
        AtomicInteger counter = new AtomicInteger();
        Integer interval    = config.exists("corpus.interval")? Integer.valueOf(config.get("corpus.interval")): 100;
        Integer maxSize     = config.exists("corpus.size")? Integer.valueOf(config.get("corpus.size")) : -1;
        Integer offset      = config.exists("corpus.offset")? Integer.valueOf(config.get("corpus.offset")) : 0;
        Boolean multigrams  = config.exists("corpus.multigrams")? Boolean.valueOf(config.get("corpus.multigrams")) : false;
        reader.offset(offset);
        while(( maxSize<0 || counter.get()<=maxSize) &&  (doc = reader.next()).isPresent()){
            if (counter.incrementAndGet() % interval == 0) LOG.info(counter.get() + " documents indexed");
            final Document document = doc.get();
            parallelService.execute(() -> librairyClient.save(document, multigrams));
        }
        parallelService.stop();
        LOG.info(counter.get() + " documents indexed");
        LOG.info("done!");

        librairyClient.train(modelParams);
        LOG.info("model creation started");

        if (fg) waitForFinish(librairyClient);

    }

    public void retrain(Config config, Boolean fg){

        LearnerClient librairyClient = new LearnerClient(config.get("librairy.endpoint"),config.get("librairy.user"),config.get("librairy.pwd"));
        Map<String,String> readerParams = new HashMap<>();
        Map<String, String> modelParams = new HashMap<>();
        for (String key: config.list()){
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

        if (fg) waitForFinish(librairyClient);

    }


    public void export(Config config){
        LearnerClient librairyClient = new LearnerClient(config.get("librairy.endpoint"),config.get("librairy.user"),config.get("librairy.pwd"));

        Map<String,String> params = new HashMap<>();
        for (String key: config.list()){
            if (key.startsWith("docker")){
                String param = StringUtils.substringAfter(key,"docker.");
                String value = config.get(key);
                params.put(param,value);
            }
        }

        librairyClient.export(params);
        LOG.info("model exported");


    }

    private void waitForFinish(LearnerClient librairyClient){
        LOG.info("waiting for finish the training process");

        Boolean completed = false;

        try {
            while(!completed){

                Thread.sleep(2000);
                completed = librairyClient.isCompleted();
            }
        } catch (InterruptedException e) {
            LOG.debug("thread interrupted!");
        }
        LOG.info("model created");
    }

}
