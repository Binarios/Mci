package com.aegean.icsd.ontology;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.aegean.icsd.ontology.beans.DatasetProperties;

@Configuration
@ComponentScan({"com.aegean.icsd.ontology"})
@PropertySource("classpath:com/aegean/icsd/ontology/ontology.properties")
public class OntologyConfiguration {
  private static final Logger LOGGER = LogManager.getLogger(OntologyConfiguration.class);

  @Autowired
  private Environment env;

  @Bean
  public DatasetProperties getDatasetProperties() {
    LOGGER.info("Started reading configuration of the ontology");
    DatasetProperties properties = new DatasetProperties();
    properties.setDatasetLocation(env.getProperty("datasetLoc"));
    properties.setNamespace(env.getProperty("namespace"));
    properties.setOntologyLocation(env.getProperty("ontologyLoc"));
    properties.setPrefix(env.getProperty("prefix"));
    properties.setOntologyName(env.getProperty("ontologyName"));
    properties.setOntologyType(env.getProperty("ontologyType"));
    LOGGER.info("Finished configuration of the ontology");
    return properties;
  }
}
