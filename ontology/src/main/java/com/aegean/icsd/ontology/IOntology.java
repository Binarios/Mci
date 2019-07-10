package com.aegean.icsd.ontology;

import java.util.List;

import org.apache.jena.query.ParameterizedSparqlString;

import com.aegean.icsd.ontology.beans.ClassSchema;
import com.aegean.icsd.ontology.beans.OntologyException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public interface IOntology {

  JsonArray executeSelect(ParameterizedSparqlString sparql, List<String> colNames) throws OntologyException;

  JsonObject selectTriplet(String subject, String predicate, String object);

  boolean insertTriplet(String subject, String predicate, String object);

  ClassSchema getClassSchema(String className) throws OntologyException;
}
