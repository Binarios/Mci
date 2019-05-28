package com.aegean.icsd.mcidatabase.ontology;

import java.io.IOException;

import com.aegean.icsd.mcidatabase.MciDatabaseException;

public interface IMciOntology {
  String getEntityUri(String entityName) throws MciDatabaseException;

  String getQueryEntityUri(String entity) throws IOException;
}
