package com.aegean.icsd.mci.observations;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.jena.query.ParameterizedSparqlString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aegean.icsd.mci.common.beans.MciOntologyException;
import com.aegean.icsd.mci.connection.ITdbConnection;
import com.aegean.icsd.mci.connection.TdbConnection;
import com.aegean.icsd.mci.ontology.IMciOntology;

@ExtendWith(MockitoExtension.class)
public class TestObservation {

  @InjectMocks
  ObservationSvc svc = new ObservationSvc();

  @Mock
  private IMciOntology ontology;

  @Mock
  private ITdbConnection conn;


  @Test
  public void testSelect() throws SQLException, MciOntologyException {
    Connection con = null;
    try {
      String ns = "http://www.semanticweb.org/iigou/diplomatiki/ontologies/Games";
      con = new TdbConnection().connect("../../dataset");

      ParameterizedSparqlString pss = new ParameterizedSparqlString();
      pss.setCommandText("SELECT ?s WHERE { ?s ?p ?o }");
      pss.setNsPrefix("mci", ns);
      pss.setIri("?p", "mci:hasId" );
      pss.setLiteral("?o", "test" );

      Statement sel = con.createStatement();
      String result = null;
      ResultSet rset = sel.executeQuery(pss.asQuery().toString());
      while (rset.next()) {
        result = rset.getString("s");
      }
      rset.close();

      Assertions.assertNotNull(result);
    } finally {
      if (con != null && !con.isClosed()) {
        con.close();
      }
    }
  }

  @Test
  public void testInsert() throws SQLException, MciOntologyException {
    Connection con = null;
    try {
      String ns = "http://www.semanticweb.org/iigou/diplomatiki/ontologies/Games#";
      con = new TdbConnection().connect("../../dataset");

      ParameterizedSparqlString pss = new ParameterizedSparqlString();
      pss.setCommandText("INSERT DATA { ?s ?p ?o }");
      pss.setNsPrefix("mci", ns);
      pss.setIri("?s", "mci:EasyObservation" );
      pss.setIri("?p", "mci:hasId" );
      pss.setLiteral("?o", "test" );

      var req = pss.asUpdate();
      Statement prp = con.createStatement();
      int res = prp.executeUpdate(req.toString());
      Assertions.assertEquals(0, res);
    } finally {
      if (con != null && !con.isClosed()) {
        con.close();
      }
    }
  }

  @Test
  public void testDelete() throws SQLException, MciOntologyException {
    Connection con = null;
    try {
      String ns = "http://www.semanticweb.org/iigou/diplomatiki/ontologies/Games#";
      con = new TdbConnection().connect("../../dataset");

      ParameterizedSparqlString pss = new ParameterizedSparqlString();
      pss.setCommandText("DELETE { ?s ?p ?o } WHERE {}");
      pss.setNsPrefix("mci", ns);
      pss.setIri("?s", "mci:EasyObservation" );
      pss.setIri("?p", "mci:hasId" );
      pss.setLiteral("?o", "test" );

      var req = pss.asUpdate();
      Statement prp = con.createStatement();
      int res = prp.executeUpdate(req.toString());
      Assertions.assertEquals(0, res);
    } finally {
      if (con != null && !con.isClosed()) {
        con.close();
      }
    }
  }
}
