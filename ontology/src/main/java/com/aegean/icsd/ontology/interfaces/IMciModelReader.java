package com.aegean.icsd.ontology.interfaces;

import java.util.List;

import com.aegean.icsd.ontology.beans.ClassSchema;
import com.aegean.icsd.ontology.beans.OntologyException;

public interface IMciModelReader {
  ClassSchema getClassSchema(String className) throws OntologyException;

  List<String> getClassChildren(String className);

  boolean isSubclassOf(String subclassName, String className);

  Class<?> getJavaClassFromOwlType (String owlType);

  String getPrefixedEntity(String entityName);

  String removePrefix(String prefixedEntity);
}
