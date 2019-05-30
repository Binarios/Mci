package com.aegean.icsd.mci.ontology;


import java.util.List;

import org.apache.jena.query.ParameterizedSparqlString;

import com.aegean.icsd.mci.common.beans.MciOntologyException;

import com.google.gson.JsonArray;

public interface IMciOntology {
  String getNamespace();

  String getPrefix();

  JsonArray executeSelect(ParameterizedSparqlString sparql, List<String> colNames) throws MciOntologyException;

  boolean executeUpdate(ParameterizedSparqlString sparql) throws MciOntologyException;
}
