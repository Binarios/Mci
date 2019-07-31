package com.aegean.icsd;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.aegean.icsd.ontology.OntologyConfiguration;
import com.aegean.icsd.ontology.beans.DatasetProperties;

@ExtendWith(MockitoExtension.class)
public class TestSpringConfig {

  @Test
  public void testConfig() {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(OntologyConfiguration.class);
    DatasetProperties props = ctx.getBean(DatasetProperties.class);

    Assertions.assertNotNull(props);
    Assertions.assertEquals("../../MciOntology/games.owl", props.getOntologyLocation());
    Assertions.assertEquals("../../dataset", props.getDatasetLocation());
    Assertions.assertEquals("TURTLE", props.getOntologyType());
    Assertions.assertEquals("games", props.getOntologyName());
    Assertions.assertEquals("http://www.semanticweb.org/iigou/diplomatiki/ontologies/Games#", props.getNamespace());
    Assertions.assertEquals("mci", props.getPrefix());
  }
}
