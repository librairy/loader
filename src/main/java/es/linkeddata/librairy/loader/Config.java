package es.linkeddata.librairy.loader;

import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public interface Config {

    String get(String id);

    boolean exists(String id);

    List<String> list();
}
