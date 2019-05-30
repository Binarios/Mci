package com.aegean.icsd.mci;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.aegean.icsd.mci.ontology.beans.DatasetProperties;

@ExtendWith(MockitoExtension.class)
public class TestSpringConfig {

  @Test
  public void testConfig() {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfiguration.class);
    DatasetProperties props = ctx.getBean(DatasetProperties.class);

    Assertions.assertNotNull(props);
    Assertions.assertEquals("../../MciOntology/games.owl", props.getOntologyLocation());
    Assertions.assertEquals("../../dataset", props.getDatasetLocation());
    Assertions.assertEquals("ttl", props.getOntologyType());
    Assertions.assertEquals("games", props.getOntologyName());
    Assertions.assertEquals("http://www.semanticweb.org/iigou/diplomatiki/ontologies/Games#", props.getNamespace());
    Assertions.assertEquals("mci", props.getPrefix());
  }
}
