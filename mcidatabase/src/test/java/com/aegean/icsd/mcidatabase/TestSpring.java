package com.aegean.icsd.mcidatabase;

import java.io.IOException;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.tdb.TDBFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.aegean.icsd.mcidatabase.connection.ITdbConnection;
import com.aegean.icsd.mcidatabase.ontology.IMciOntology;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestSpring {

  @Test
  public void testSpring() throws IOException {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfiguration.class);
    ITdbConnection tbd = ctx.getBean(ITdbConnection.class);
    IMciOntology ont = ctx.getBean(IMciOntology.class);
    Dataset dataset = TDBFactory.createDataset(Utils.getDatabasePropertyValue("datasetDir"));
    Assertions.assertNotNull(tbd);
    Assertions.assertNotNull(ont);
    dataset.begin(ReadWrite.READ);
    Assertions.assertTrue(dataset.containsNamedModel(Utils.getOntologyPropertyValue("ontologyName")));
    dataset.end();
  }
}
