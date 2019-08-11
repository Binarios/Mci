package com.aegean.icsd.ontology;


import com.aegean.icsd.ontology.beans.ClassSchema;
import com.aegean.icsd.ontology.beans.OntologyException;
import com.aegean.icsd.ontology.queries.AskQuery;
import com.aegean.icsd.ontology.queries.InsertQuery;
import com.aegean.icsd.ontology.queries.SelectQuery;

import com.google.gson.JsonArray;

public interface IOntology {

  JsonArray select(SelectQuery query) throws OntologyException;

  boolean insert(InsertQuery query) throws OntologyException;

  ClassSchema getClassSchema(String className) throws OntologyException;

  String getPrefixedEntity(String entity);

  Class<?> getJavaClassFromOwlType (String owlType);

  String removePrefix(String prefixedEntity);

  boolean ask(AskQuery ask) throws OntologyException;
}
