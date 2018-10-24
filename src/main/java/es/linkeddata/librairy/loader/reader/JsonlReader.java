package es.linkeddata.librairy.loader.reader;

import com.google.common.base.Strings;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.librairy.service.learner.facade.rest.model.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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
        this.reader = path.endsWith(".gz")?
                new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(jsonFile)))) :
                new BufferedReader(new InputStreamReader(new FileInputStream(jsonFile))) ;
        this.map = map;
    }

    public JsonlReader(InputStreamReader input,Map<String,String> map) throws IOException {
        this.path   ="inputStream";
        this.reader = new BufferedReader(input);
        this.map    = map;
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
            if (map.containsKey("name") && jsonObject.has(map.get("name"))){
                try{
                    document.setName(jsonObject.getString(map.get("name")));
                }catch (JSONException e){
                    LOG.warn(""+e.getMessage());
                }
            }
            if (map.containsKey("text") && jsonObject.has(map.get("text")))    {
                try{
                    document.setText(jsonObject.getString(map.get("text")));
                }catch (JSONException e){
                    LOG.warn(""+e.getMessage());
                }
            }
            if (map.containsKey("labels")){

                List<String> labels = new ArrayList<>();
                Object labelObject = jsonObject.get(map.get("labels"));

                if (labelObject instanceof JSONArray){
                    Iterator<Object> it = jsonObject.getJSONArray(map.get("labels")).iterator();
                    while(it.hasNext()){
                        String label = (String) it.next();
                        labels.add(label);
                    }
                }else{
                    labels.add(jsonObject.getString(map.get("labels")));
                }

                document.setLabels(labels);
            }

            return Optional.of(document);

        }catch (Exception e){
            LOG.error("Unexpected error parsing file: " + path,e);
            return Optional.empty();
        }
    }

    @Override
    public void offset(Integer numLines) {
        if (numLines>0){
            AtomicInteger counter = new AtomicInteger();
            String line;
            try{
                while (((line = reader.readLine()) != null) && (counter.incrementAndGet() <= numLines)){
                }

            }catch (Exception e){
                LOG.error("Unexpected error parsing file: " + path,e);
            }


        }
    }

}
