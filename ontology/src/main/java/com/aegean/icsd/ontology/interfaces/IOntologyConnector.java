package com.aegean.icsd.ontology.interfaces;

import com.aegean.icsd.ontology.beans.OntologyException;
import com.aegean.icsd.ontology.queries.AskQuery;
import com.aegean.icsd.ontology.queries.InsertQuery;
import com.aegean.icsd.ontology.queries.SelectQuery;

import com.google.gson.JsonArray;

public interface IOntologyConnector {
  JsonArray select(SelectQuery query) throws OntologyException;

  boolean insert(InsertQuery query) throws OntologyException;

  boolean ask(AskQuery ask) throws OntologyException;
}
