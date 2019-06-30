package com.aegean.icsd.ontology;

import java.util.List;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.query.ParameterizedSparqlString;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public interface IOntology {

  JsonArray executeSelect(ParameterizedSparqlString sparql, List<String> colNames) throws OntologyException;

  boolean executeUpdate(ParameterizedSparqlString sparql) throws OntologyException;

  JsonObject generateIndividual(String className) throws OntologyException;
}
