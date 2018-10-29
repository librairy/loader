package es.linkeddata.librairy.loader.reader;

import com.google.common.base.Strings;
import es.linkeddata.librairy.loader.io.Reader;
import es.linkeddata.librairy.loader.io.ReaderFactory;
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

public class CSVReaderTask {

    private static final Logger LOG = LoggerFactory.getLogger(CSVReaderTask.class);


    @Test
    public void remote() throws IOException {

        String endpoint = "https://delicias.dia.fi.upm.es/nextcloud/index.php/s/fZib348ETqd2Xpa/download";

        Map<String, String> params = new HashMap<>();
        params.put("id","0");
        params.put("name","0");
        params.put("text","2");
        params.put("labels","1");

        Reader reader = ReaderFactory.newFrom(endpoint, "csv.gz_\t", params);


        Optional<Document> doc = Optional.empty();
        AtomicInteger counter = new AtomicInteger(0);
        while( (doc = reader.next()).isPresent()){
            if (counter.incrementAndGet() % 100 == 0) LOG.info(counter.get() + " lines read");
            if (Strings.isNullOrEmpty(doc.get().getText())) continue;
            LOG.info("Paper with abstract! -> " + doc.get());
        }

        LOG.info("Document completed");

    }

}
