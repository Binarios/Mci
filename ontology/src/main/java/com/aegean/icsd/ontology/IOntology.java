package com.aegean.icsd.ontology;


import com.aegean.icsd.ontology.beans.ClassSchema;
import com.aegean.icsd.ontology.beans.OntologyException;
import com.aegean.icsd.ontology.queries.SelectQuery;

import com.google.gson.JsonObject;

public interface IOntology {

  JsonObject select(SelectQuery query);

  JsonObject selectTriplet(String subject, String predicate, String object);

  boolean insertTriplet(String subject, String predicate, String object);

  ClassSchema getClassSchema(String className) throws OntologyException;
}
