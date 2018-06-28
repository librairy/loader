package es.linkeddata.librairy.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class PropertiesConfig implements Config{

    private static final Logger LOG = LoggerFactory.getLogger(PropertiesConfig.class);

    private Properties prop = new Properties();

    public PropertiesConfig(String path) {
        InputStream input = null;

        try {

            input = new FileInputStream("application.properties");

            // load a properties file
            prop.load(input);

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String get(String id){
        return prop.getProperty(id);
    }

    public boolean exists(String id){
        return prop.containsKey(id);
    }

    public List<String> list() {
        return prop.keySet().stream().map(k -> String.valueOf(k)).collect(Collectors.toList());
    }
}
