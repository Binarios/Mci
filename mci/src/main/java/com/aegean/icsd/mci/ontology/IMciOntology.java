package com.aegean.icsd.mci.ontology;

import java.util.List;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.query.ParameterizedSparqlString;

import com.google.gson.JsonArray;

public interface IMciOntology {

  String getRdfNamespace();

  String getRdfPrefix();

  String getMciNamespace();

  String getMciPrefix();

  String getPrefixedEntity(String entityName);

  JsonArray executeSelect(ParameterizedSparqlString sparql, List<String> colNames) throws MciOntologyException;

  boolean executeUpdate(ParameterizedSparqlString sparql) throws MciOntologyException;

  OntClass getOntClass(String className) throws MciOntologyException;
}
