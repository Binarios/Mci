package com.aegean.icsd.mci.observations;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.mci.ontology.beans.Difficulty;
import com.aegean.icsd.mci.common.beans.GameDescription;
import com.aegean.icsd.mci.connection.ITdbConnection;
import com.aegean.icsd.mci.observations.dao.IObservationsDao;
import com.aegean.icsd.mci.ontology.IMciOntology;
import com.aegean.icsd.mci.ontology.beans.TriplesBlock;

@Service
public class ObservationSvc implements IObservationSvc{

  @Autowired
  private IMciOntology ont;

  @Autowired
  private ITdbConnection conn;

  @Autowired
  private IObservationsDao obsDao;

  private final String OBSERVATION = "Observation";

  @Override
  public GameDescription createObservationGame(String difficulty, String level) {

    Difficulty chosenDifficulty = Difficulty.fromName(difficulty);
    List<TriplesBlock> triples = new ArrayList<>();

    TriplesBlock obsTriples = new TriplesBlock(
            ont.getPrefixedEntity(chosenDifficulty.getName() + OBSERVATION),
            "", "");


    return null;
  }
}
