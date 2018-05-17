package es.linkeddata.librairy.loader.reader;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class ReaderFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ReaderFactory.class);


    public static Reader newFrom(Properties properties){

        File file           = Paths.get(properties.getProperty("file.path")).toFile();
        String formatExp    = properties.getProperty("file.format");
        String format       = StringUtils.substringBefore(formatExp,"_");

        try {
            if (format.equalsIgnoreCase("jsonl")){
                Map<String,String> map = new HashMap<>();
                map.put("id", properties.getProperty("document.id"));
                map.put("name", properties.getProperty(("document.name")));
                map.put("text", properties.getProperty(("document.text")));
                if (properties.containsKey("labels"))   map.put("labels", properties.getProperty("document.labels"));
                return new JsonlReader(file,map);
            }else if (format.equalsIgnoreCase("csv")){
                String separator = StringUtils.substringAfter(formatExp,"_");
                Map<String,Integer> map = new HashMap<>();
                map.put("id",   Integer.valueOf(properties.getProperty(("document.id"))));
                map.put("name", Integer.valueOf(properties.getProperty(("document.name"))));
                map.put("text", Integer.valueOf(properties.getProperty(("document.text"))));
                String labelsSeparator = null;
                if (properties.containsKey("labels")) {
                    String labelsExp        = properties.getProperty("document.labels");
                    Integer labelsIndex     = Integer.valueOf(StringUtils.substringBefore(labelsExp,"_"));
                    labelsSeparator  = StringUtils.substringAfter(labelsExp,"_");
                    map.put("labels", labelsIndex);
                }
                return new CSVReader(file,separator, labelsSeparator, map);
            }else{
                throw new RuntimeException("File format not supported: " + format);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
