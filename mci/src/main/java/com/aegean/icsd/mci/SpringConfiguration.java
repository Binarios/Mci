package com.aegean.icsd.mci;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.codec.language.bm.Rule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.aegean.icsd.mci.ontology.beans.DatasetProperties;

@Configuration
@ComponentScan("com.aegean.icsd.mci")
public class SpringConfiguration {

  @Bean
  public DatasetProperties getDatasetProperties() throws IOException {
    String rootPath = this.getClass().getResource("").getPath();
    String configPath = rootPath + "/ontology.properties";
    Properties ontProps = new Properties();
    ontProps.load(new FileInputStream(configPath));
    DatasetProperties properties = new DatasetProperties();
    properties.setDatasetLocation(ontProps.getProperty("datasetLoc"));
    properties.setNamespace(ontProps.getProperty("namespace"));
    properties.setOntologyLocation(ontProps.getProperty("ontologyLoc"));
    properties.setPrefix(ontProps.getProperty("prefix"));
    properties.setOntologyName(ontProps.getProperty("ontologyName"));
    properties.setOntologyType(ontProps.getProperty("ontologyType"));
    return properties;
  }
}
