package es.linkeddata.librairy.loader.reader;

import com.google.common.base.Strings;
import org.junit.Test;
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

public class JsonlReaderTask {

    private static final Logger LOG = LoggerFactory.getLogger(JsonlReaderTask.class);


    @Test
    public void remote() throws IOException {

        String endpoint = "https://delicias.dia.fi.upm.es/nextcloud/index.php/s/tCtq8K8wtm3aTjq/download";

        Map<String, String> params = new HashMap<>();
        params.put("id","id");
        params.put("name","title");
        params.put("text","text");

        Reader reader = ReaderFactory.newFrom(endpoint, "jsonl.gz", params);


        Optional<Document> doc = Optional.empty();
        AtomicInteger counter = new AtomicInteger(0);
        while( (doc = reader.next()).isPresent()){
            if (counter.incrementAndGet() % 100 == 0) LOG.info(counter.get() + " lines read");
            if (Strings.isNullOrEmpty(doc.get().getText())) continue;
            LOG.info("Paper with abstract!");
        }

        LOG.info("Document completed");

    }

}
