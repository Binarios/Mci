package com.aegean.icsd.mci.generator;

import com.aegean.icsd.mci.ontology.beans.Game;
import com.aegean.icsd.mci.ontology.beans.IndividualDescriptor;
import com.aegean.icsd.mci.ontology.beans.Difficulty;
import com.aegean.icsd.mci.ontology.MciOntologyException;

public interface IGameGenerator {

  IndividualDescriptor generateGame(Game game, Difficulty difficulty, String level) throws MciOntologyException;
}
