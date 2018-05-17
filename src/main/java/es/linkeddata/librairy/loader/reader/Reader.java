package es.linkeddata.librairy.loader.reader;

import org.librairy.service.learner.facade.rest.model.Document;

import java.util.Optional;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public interface Reader {

    Optional<Document> next();
}
