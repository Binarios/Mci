package com.aegean.icsd.mci.observations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.mci.common.beans.Difficulty;
import com.aegean.icsd.mci.common.beans.GameDescription;
import com.aegean.icsd.mci.connection.ITdbConnection;
import com.aegean.icsd.mci.ontology.IMciOntology;

@Service
public class ObservationSvc implements IObservationSvc{

  @Autowired
  private IMciOntology ontology;

  @Autowired
  private ITdbConnection conn;


  @Override
  public GameDescription createObservationGame(String difficulty, String level) {

    Difficulty chosenDifficulty = Difficulty.fromName(difficulty);
    return null;
  }
}
