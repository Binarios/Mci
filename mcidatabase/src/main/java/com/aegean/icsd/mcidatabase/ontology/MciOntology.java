package com.aegean.icsd.mcidatabase.ontology;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.tdb.TDBFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.mcidatabase.MciDatabaseException;
import com.aegean.icsd.mcidatabase.connection.ITdbConnection;
import com.aegean.icsd.mcidatabase.connection.TdbConnection;

@Service
public class MciOntology implements IMciOntology {

  @Autowired
  private ITdbConnection tdbSvc;

  private Dataset dataset;
  private Properties ontologyProps;

  @Override
  public String getEntityUri(String entityName) throws MciDatabaseException {
    try {
      return getOntologyPropertyValue("namespace") + "#" + entityName;
    } catch (IOException e) {
      throw new MciDatabaseException("ont.1", "Cannot retrieve entity full URI", e);
    }
  }

  @PostConstruct
  void setupDataset() throws IOException, MciDatabaseException {
    String ontologyName = getOntologyPropertyValue("ontologyName");
    if (dataset == null) {
      dataset = TDBFactory.createDataset(tdbSvc.getLocation());
    }
    if (!dataset.containsNamedModel(ontologyName) || dataset.isEmpty()) {
      Model model = ModelFactory.createDefaultModel();
      model.read(new FileInputStream(getOntologyPropertyValue("ontologyDir")), null, getOntologyPropertyValue("ontologyType"));
      dataset.begin(ReadWrite.WRITE);
      dataset.addNamedModel(ontologyName, model);
      dataset.commit();
      dataset.end();
    }
  }

  String getQueryEntityUri(String entity) throws IOException {
    return "<" + getOntologyPropertyValue("namespace") + "#" + entity + ">";
  }

  String getOntologyPropertyValue(String key) throws IOException {
    if (ontologyProps == null) {
      String rootPath = TdbConnection.class.getResource("").getPath();
      String configPath = rootPath + "/ontology.properties";
      ontologyProps = new Properties();
      ontologyProps.load(new FileInputStream(configPath));
    }
    return ontologyProps.getProperty(key);
  }
}
