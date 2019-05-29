package com.aegean.icsd.mcidatabase.queries;

import java.util.Iterator;
import java.util.Map;

import org.apache.jena.query.ParameterizedSparqlString;
import org.springframework.beans.factory.annotation.Autowired;

import com.aegean.icsd.mcidatabase.MciDatabaseException;
import com.aegean.icsd.mcidatabase.ontology.IMciOntology;


public class InsertParametrizedQuery {

  @Autowired
  private IMciOntology ont;

  public String createInsert(String subject, Map<String,String> relations) throws MciDatabaseException {
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


  void setParameter(ParameterizedSparqlString pss, String paramName, String paramValue) {
    if (paramValue.contains(":")) {
      pss.setIri(paramName, paramValue);
    } else {
      pss.setLiteral(paramName, paramValue);
    }
  }
}
