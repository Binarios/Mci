package com.aegean.icsd.mcidatabase;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.tdb.TDBFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TestConnection {

  private String datasetDir;
  private String ontologyDir;
  private Dataset dataset;
  private Model model;
  private Connection conn;
  private String ns;

  @BeforeAll
  void setup() {
    this.datasetDir = "D:\\WorkBench\\Diplomatiki\\dataset";
    this.ontologyDir = "D:\\WorkBench\\Diplomatiki\\MciOntology\\games.owl";
    this.dataset = TDBFactory.createDataset(datasetDir);
    this.model =  ModelFactory.createDefaultModel();
    this.ns = "http://www.semanticweb.org/iigou/diplomatiki/ontologies/Games";
  }

  @Test
  void testConnect() throws FileNotFoundException, SQLException {
    setupDataset();
    try {
      this.conn = DriverManager.getConnection("jdbc:jena:tdb:location=" + datasetDir);
    } finally {
      if(conn!=null) {
        conn.close();
      }
    }
  }

  @Test
  void testInsertDeleteData() throws FileNotFoundException, SQLException {
    setupDataset();
    String result = null;
    String deleteResult = null;
    try {
      this.conn = DriverManager.getConnection("jdbc:jena:tdb:location=" + datasetDir);
      Statement insert = conn.createStatement();
      insert.executeUpdate(String.format("INSERT DATA {<%s#%s> <%s#%s> \"testString\"}", this.ns, "Word", this.ns, "isWord"));
      ResultSet rset = insert.executeQuery(String.format("SELECT ?s WHERE { ?s <%s#%s> \"%s\" } LIMIT 100", this.ns, "isWord", "testString"));
      while (rset.next()) {
        result = rset.getString("s");
      }
      rset.close();
      Statement delete = conn.createStatement();
      delete.executeUpdate(String.format("DELETE DATA {<%s#%s> <%s#%s> \"testString\"}", this.ns, "Word", this.ns, "isWord"));
      ResultSet dset = insert.executeQuery(String.format("SELECT ?s WHERE { ?s <%s#%s> \"%s\" } LIMIT 100", this.ns, "isWord", "testString"));
      while (dset.next()) {
        deleteResult = dset.getString("s");
      }
      dset.close();
    } finally {
      if(conn!=null) {
        conn.close();
      }
    }

    Assertions.assertNotNull(result);
    Assertions.assertNull(deleteResult);
    Assertions.assertEquals(this.ns + "#Word", result);
  }

  private void setupDataset() throws FileNotFoundException {
    dataset.begin(ReadWrite.WRITE) ;
    model.read(new FileInputStream(this.ontologyDir), null, "ttl") ;
    dataset.addNamedModel("games", model);
    dataset.commit();
    dataset.end() ;
  }
}
