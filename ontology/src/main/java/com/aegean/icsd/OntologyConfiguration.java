package com.aegean.icsd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;

import com.aegean.icsd.ontology.beans.DatasetProperties;

@Configuration
@ComponentScan({"com.aegean.icsd.ontology", "com.aegean.icsd.connection" })
@PropertySource("classpath:com/aegean/icsd/ontology.properties")
public class OntologyConfiguration {

  @Autowired
  private Environment env;

  @Bean
  public DatasetProperties getDatasetProperties() {
    DatasetProperties properties = new DatasetProperties();
    properties.setDatasetLocation(env.getProperty("datasetLoc"));
    properties.setNamespace(env.getProperty("namespace"));
    properties.setOntologyLocation(env.getProperty("ontologyLoc"));
    properties.setPrefix(env.getProperty("prefix"));
    properties.setOntologyName(env.getProperty("ontologyName"));
    properties.setOntologyType(env.getProperty("ontologyType"));
    return properties;
  }
}
