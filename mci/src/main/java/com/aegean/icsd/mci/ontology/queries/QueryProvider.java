package com.aegean.icsd.mci.ontology.queries;

import java.util.Iterator;
import java.util.Map;

import org.apache.jena.query.ParameterizedSparqlString;
import org.springframework.beans.factory.annotation.Autowired;

import com.aegean.icsd.mci.ontology.MciOntologyException;
import com.aegean.icsd.mci.ontology.IMciOntology;


public class QueryProvider {

  @Autowired
  private IMciOntology ont;

  public String insertSubjectRelationsCommand(String subject, Map<String,String> relations) throws MciOntologyException {
    ParameterizedSparqlString pss = new ParameterizedSparqlString();
    pss.setNsPrefix(ont.getPrefix(), ont.getNamespace());
    StringBuilder sb = new StringBuilder("INSERT DATA { ?s");
    setParameter(pss,"?s", subject);
    Iterator<Map.Entry<String, String>> it = relations.entrySet().iterator();
    int count = 0;
    while (it.hasNext()) {
      Map.Entry<String, String> entry = it.next();
      sb.append(String.format(" ?p%s ", count));
      sb.append(String.format(" ?o%s ", count));
      if (it.hasNext()) {
        sb.append(";");
      } else {
        sb.append(".");
      }
      setParameter(pss,"?p" + count, entry.getKey());
      setParameter(pss,"?o" + count, entry.getValue());
      count++;
    }
    sb.append('}');
    pss.setCommandText(sb.toString());
    return pss.asUpdate().toString();
  }

  public String deleteSubjectRelationsCommand(String subject, Map<String,String> relations) throws MciOntologyException {
    ParameterizedSparqlString pss = new ParameterizedSparqlString();
    pss.setNsPrefix(ont.getPrefix(), ont.getNamespace());
    StringBuilder sb = new StringBuilder("DELETE DATA { ?s");
    setParameter(pss,"?s", subject);
    Iterator<Map.Entry<String, String>> it = relations.entrySet().iterator();
    int count = 0;
    while (it.hasNext()) {
      Map.Entry<String, String> entry = it.next();
      sb.append(String.format(" ?p%s ", count));
      sb.append(String.format(" ?o%s ", count));
      if (it.hasNext()) {
        sb.append(";");
      } else {
        sb.append(".");
      }
      setParameter(pss,"?p" + count, entry.getKey());
      setParameter(pss,"?o" + count, entry.getValue());
      count++;
    }
    sb.append('}');
    pss.setCommandText(sb.toString());
    return pss.asUpdate().toString();
  }

  void setParameter(ParameterizedSparqlString pss, String paramName, String paramValue) {
    if (paramValue.contains(":")) {
      pss.setIri(paramName, paramValue);
    } else {
      pss.setLiteral(paramName, paramValue);
    }
  }
}
