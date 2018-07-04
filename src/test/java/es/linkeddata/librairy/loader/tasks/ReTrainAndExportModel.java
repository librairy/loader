package es.linkeddata.librairy.loader.tasks;

import com.google.common.base.Strings;
import es.linkeddata.librairy.loader.PropertiesConfig;
import es.linkeddata.librairy.loader.service.ModelService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class ReTrainAndExportModel {

    private static final Logger LOG = LoggerFactory.getLogger(ReTrainAndExportModel.class);

    @Test
    public void execute(){

        String propertiesPath = Strings.isNullOrEmpty(System.getenv("LOADER_PROPERTIES"))? "application.properties" : System.getenv("LOADER_PROPERTIES");

        ModelService modelService = new ModelService();

        try {
            PropertiesConfig config = new PropertiesConfig(propertiesPath);
            modelService.retrain(config, true);
            modelService.export(config);
        } catch (Exception e) {
            LOG.error("Unexpected error", e);
        }

    }

}
