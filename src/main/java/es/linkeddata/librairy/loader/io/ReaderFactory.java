package es.linkeddata.librairy.loader.io;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class ReaderFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ReaderFactory.class);


    public static es.linkeddata.librairy.loader.io.Reader newFrom(String url, String formatExp, Map<String,String> parsingMap) throws IOException {


        String format       = StringUtils.substringBefore(formatExp,"_");

        InputStreamReader inputReader;

        if (url.startsWith("http")){
            if (url.endsWith("gz") || format.contains("gz")) {
                inputReader = new InputStreamReader(new GZIPInputStream(new URL(url).openStream()));
            }else{
                inputReader = new InputStreamReader(new URL(url).openStream());
            }
        }else{
            if (url.endsWith("gz")|| format.contains("gz")) {
                inputReader = new InputStreamReader(new GZIPInputStream(new FileInputStream(url)));
            }else{
                inputReader = new InputStreamReader(new FileInputStream(url));
            }
        }



        try {
            if (format.toLowerCase().startsWith("jsonl")){
                return new JsonlReader(inputReader,parsingMap);
            }else if (format.toLowerCase().startsWith("csv")){
                String separator = StringUtils.substringAfter(formatExp,"_");
                Map<String,Integer> map = new HashMap<>();
                parsingMap.entrySet().stream().filter(entry -> !entry.getKey().contains("labels")).forEach(entry -> map.put(entry.getKey(), Integer.valueOf(entry.getValue())));
                String labelsSeparator = null;
                if (parsingMap.containsKey("labels")) {
                    String labelsExp        = String.valueOf(parsingMap.get("labels"));
                    Integer labelsIndex     = Integer.valueOf(StringUtils.substringBefore(labelsExp,"_"));
                    labelsSeparator  = StringUtils.substringAfter(labelsExp,"_");
                    map.put("labels", labelsIndex);
                }
                return new CSVReader(inputReader,separator, labelsSeparator, map);
            }else{
                throw new RuntimeException("File format not supported: " + format);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
