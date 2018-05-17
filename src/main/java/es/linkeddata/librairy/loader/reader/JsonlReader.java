package es.linkeddata.librairy.loader.reader;

import org.json.JSONObject;
import org.librairy.service.learner.facade.rest.model.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class  JsonlReader implements Reader{

    private static final Logger LOG = LoggerFactory.getLogger(JsonlReader.class);
    private final Map<String, String> map;
    private final String path;

    private BufferedReader reader;

    public JsonlReader(File jsonFile,Map<String,String> map) throws IOException {
        this.path = jsonFile.getAbsolutePath();
        this.reader = path.endsWith(".gz")? new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(jsonFile)))) : new BufferedReader(new InputStreamReader(new FileInputStream(jsonFile))) ;
        this.map = map;
    }

    @Override
    public Optional<Document> next()  {
        String line;
        try{
            if ((line = reader.readLine()) == null){
                reader.close();
                return Optional.empty();
            }
            Document document = new Document();

            JSONObject jsonObject = new JSONObject(line);

            if (map.containsKey("id"))      document.setId(jsonObject.getString(map.get("id")));
            if (map.containsKey("name"))    document.setName(jsonObject.getString(map.get("name")));
            if (map.containsKey("text"))    document.setText(jsonObject.getString(map.get("text")));
            if (map.containsKey("labels")){
                List<String> labels = new ArrayList<>();
                Iterator<Object> it = jsonObject.getJSONArray(map.get("labels")).iterator();
                while(it.hasNext()){
                    String label = (String) it.next();
                    labels.add(label);
                }
                document.setLabels(labels);
            }

            return Optional.of(document);

        }catch (Exception e){
            LOG.error("Unexpected error parsing file: " + path,e);
            return Optional.empty();
        }

    }

}
