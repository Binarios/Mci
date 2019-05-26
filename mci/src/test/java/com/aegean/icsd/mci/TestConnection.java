package com.aegean.icsd.mci;

import java.io.InputStream;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.shared.JenaException;
import org.apache.jena.util.FileManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

public class TestConnection {

  private static final Logger LOGGER = LogManager.getLogger(TestConnection.class.getName());

  @Test
  public void testConnection() {
  }

  public static OntModel getOntologyModel(String ontoFile)
  {
    OntModel ontoModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
    try
    {
      InputStream in = FileManager.get().open(ontoFile);
      try
      {
        ontoModel.read(in, null);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
      LOGGER.info("Ontology " + ontoFile + " loaded.");
    }
    catch (JenaException je)
    {
      System.err.println("ERROR" + je.getMessage());
      je.printStackTrace();
      System.exit(0);
    }
    return ontoModel;
  }
}
