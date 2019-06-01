package com.aegean.icsd.mci.ontology;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.tdb.TDBFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.mci.connection.ITdbConnection;
import com.aegean.icsd.mci.ontology.beans.DatasetProperties;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Service
public class MciOntology implements IMciOntology {

  @Autowired
  private DatasetProperties ontologyProps;

  @Autowired
  private ITdbConnection conProvider;

  private Dataset dataset;

  private final String SEPARATOR = ":";

  private Model model;

  @Override
  public String getNamespace() {
    return ontologyProps.getNamespace();
  }

  @Override
  public String getPrefix() {
    return ontologyProps.getPrefix();
  }

  @Override
  public JsonArray executeSelect(ParameterizedSparqlString sparql, List<String> colNames) throws MciOntologyException {
    Connection con = null;
    JsonArray result = new JsonArray();
    try {
      con = conProvider.connect(ontologyProps.getDatasetLocation());
      Statement sel = con.createStatement();
      ResultSet raw = sel.executeQuery(sparql.asQuery().toString());
      while (raw.next()) {
        JsonObject row = new JsonObject();
        for(String col : colNames) {
          row.addProperty(col, raw.getString("s"));
        }
      }
      raw.close();
      return result;
    } catch (MciOntologyException | SQLException e) {
      throw new MciOntologyException("ONT.EX1", "Cannot execute sparql query", e);
    } finally {
      try {
        if (con != null && !con.isClosed()) {
          con.close();
        }
      } catch (SQLException e) {
        //noinspection ThrowFromFinallyBlock
        throw new MciOntologyException("ONT.EX2", "Cannot close connection", e);
      }
    }
  }

  @Override
  public boolean executeUpdate(ParameterizedSparqlString sparql) throws MciOntologyException {
    Connection con = null;
    try{
      con = conProvider.connect(ontologyProps.getDatasetLocation());
      Statement sel = con.createStatement();
      int res = sel.executeUpdate(sparql.asUpdate().toString());
      return res == 0;
    } catch (SQLException e) {
      throw new MciOntologyException("ONT.EX3", "Cannot execute sparql query", e);
    } finally {
      try {
        if (con != null && !con.isClosed()) {
          con.close();
        }
      } catch (SQLException e) {
        //noinspection ThrowFromFinallyBlock
        throw new MciOntologyException("ONT.EX4", "Cannot close connection", e);
      }
    }
  }

  @Override
  public String getPrefixedEntity(String entityName) {
    return ontologyProps.getPrefix() + SEPARATOR + entityName;
  }

  @Override
  public OntClass getOntClass(String className) throws MciOntologyException {
    OntModel model = ModelFactory.createOntologyModel();
    try {
      model.read(new FileInputStream(ontologyProps.getOntologyLocation()), null, this.ontologyProps.getOntologyType());
    } catch (FileNotFoundException e) {
      throw new MciOntologyException("ONT.GETCLASS.1", "Cannot load ontology model", e);
    }
    OntClass result = model.getOntClass(ontologyProps.getNamespace() + className);
    return  result;
  }


  @PostConstruct
  void setupDataset() throws MciOntologyException {
    String ontologyName = this.ontologyProps.getOntologyName();
    if (this.dataset == null) {
      this.dataset = TDBFactory.createDataset(this.ontologyProps.getDatasetLocation());
    }
    this.dataset.begin(ReadWrite.READ);
    boolean init = !this.dataset.containsNamedModel(ontologyName) || this.dataset.isEmpty();
    this.dataset.end();
    if (init) {
      this.dataset.begin(ReadWrite.WRITE);
      OntModel model = ModelFactory.createOntologyModel();
      try {
        model.read(new FileInputStream(this.ontologyProps.getOntologyLocation()), null, this.ontologyProps.getOntologyType());
      } catch (FileNotFoundException e) {
        throw new MciOntologyException("ONT.LOAD.1", "Cannot load ontology model", e);
      }
      dataset.addNamedModel(ontologyName, model);
      dataset.commit();
      dataset.end();
    }
  }
}
