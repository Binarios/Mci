package com.aegean.icsd.mcidatabase.ontology;

import java.io.IOException;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.tdb.TDBFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aegean.icsd.mcidatabase.MciDatabaseException;
import com.aegean.icsd.mcidatabase.Utils;
import com.aegean.icsd.mcidatabase.connection.ITdbConnection;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TestMciOntology {

  @InjectMocks
  private MciOntology svc = new MciOntology();

  @Mock
  private ITdbConnection tdb;

  @Test
  public void testSetupDataset() throws IOException, MciDatabaseException {
    given(tdb.getLocation()).willReturn(Utils.getDatabasePropertyValue("datasetDir"));
    svc.setupDataset();
    Dataset dataset = TDBFactory.createDataset(Utils.getDatabasePropertyValue("datasetDir"));
    try {
      dataset.begin(ReadWrite.READ);
      Assertions.assertTrue(dataset.containsNamedModel(Utils.getOntologyPropertyValue("ontologyName")));
    } finally {
      dataset.end();
    }
  }

  @Test
  public void testGetEntityUri() throws MciDatabaseException {
    String result = svc.getEntityUri("test");
    Assertions.assertEquals("http://www.semanticweb.org/iigou/diplomatiki/ontologies/Games#test", result);
  }

  @Test
  public void testGetNamespace() throws MciDatabaseException {
    String result = svc.getNamespace();
    Assertions.assertEquals("http://www.semanticweb.org/iigou/diplomatiki/ontologies/Games#", result);
  }

  @Test
  public void testGetPrefix() throws MciDatabaseException {
    String result = svc.getPrefix();
    Assertions.assertEquals("mci", result);
  }
}
