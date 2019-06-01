package com.aegean.icsd.mci.observations.dao;

import java.util.List;

import org.apache.jena.query.ParameterizedSparqlString;
import org.springframework.beans.factory.annotation.Autowired;

import com.aegean.icsd.mci.ontology.MciOntologyException;
import com.aegean.icsd.mci.ontology.IMciOntology;
import com.aegean.icsd.mci.ontology.beans.TriplesBlock;

public class ObservationsDao implements IObservationsDao {

  @Autowired
  private IMciOntology ont;

  @Override
  public boolean insertObservations(List<TriplesBlock> triples) throws MciOntologyException {
    ParameterizedSparqlString sparql = new ParameterizedSparqlString();
    sparql.setNsPrefix(ont.getPrefix(), ont.getNamespace());

    StringBuilder sb = new StringBuilder("INSERT {");

    for(TriplesBlock triplesBlock : triples) {
      sb.append(triplesBlock.asString()).append("\n");
    }
    sb.append("}");
    sparql.setCommandText(sb.toString());
    return ont.executeUpdate(sparql);
  }

  @Override
  public String getSubjectNs() {
    return ont.getPrefix() + ":Observation";
  }
}
