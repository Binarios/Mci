package com.aegean.icsd.ontology;


import com.aegean.icsd.ontology.beans.ClassSchema;
import com.aegean.icsd.ontology.beans.OntologyException;
import com.aegean.icsd.queries.InsertQuery;
import com.aegean.icsd.queries.SelectQuery;

import com.google.gson.JsonArray;

public interface IOntology {

  JsonArray select(SelectQuery query) throws OntologyException;

  boolean insert(InsertQuery query) throws OntologyException;

  ClassSchema getClassSchema(String className) throws OntologyException;

  String getPrefixedEntity(String entity);

  String nodeNameGenerator(String entityName);
}
