package com.aegean.icsd.mci.observations;

import java.util.List;

import org.apache.jena.query.ParameterizedSparqlString;
import org.springframework.beans.factory.annotation.Autowired;

import com.aegean.icsd.mci.common.beans.MciOntologyException;
import com.aegean.icsd.mci.ontology.IMciOntology;
import com.aegean.icsd.mci.ontology.beans.TriplesBlock;

public class ObservationsDao {

  @Autowired
  private IMciOntology ont;

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
}
