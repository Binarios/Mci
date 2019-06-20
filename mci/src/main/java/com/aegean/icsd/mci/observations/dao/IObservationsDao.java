package com.aegean.icsd.mci.observations.dao;

import java.util.List;

import com.aegean.icsd.mci.ontology.MciOntologyException;
import com.aegean.icsd.mci.generator.beans.TriplesBlock;

public interface IObservationsDao {

  boolean insertObservations(List<TriplesBlock> triples) throws MciOntologyException;
  String getSubjectNs();
}
