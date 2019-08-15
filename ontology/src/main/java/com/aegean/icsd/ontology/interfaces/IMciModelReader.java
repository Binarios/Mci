package com.aegean.icsd.ontology.interfaces;

import com.aegean.icsd.ontology.beans.ClassSchema;
import com.aegean.icsd.ontology.beans.OntologyException;

public interface IMciModelReader {
  ClassSchema getClassSchema(String className) throws OntologyException;

  Class<?> getJavaClassFromOwlType (String owlType);

  String getPrefixedEntity(String entityName);

  String removePrefix(String prefixedEntity);
}
