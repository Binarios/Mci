package com.aegean.icsd.ontology;

import java.util.List;

import org.apache.jena.query.ParameterizedSparqlString;

import com.aegean.icsd.ontology.beans.Individual;
import com.aegean.icsd.ontology.beans.OntologyException;

import com.google.gson.JsonArray;

public interface IOntology {

  JsonArray executeSelect(ParameterizedSparqlString sparql, List<String> colNames) throws OntologyException;

  boolean executeUpdate(ParameterizedSparqlString sparql) throws OntologyException;

  Individual generateIndividual(String className) throws OntologyException;
}
