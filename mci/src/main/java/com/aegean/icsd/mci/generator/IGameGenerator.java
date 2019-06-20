package com.aegean.icsd.mci.generator;

import com.aegean.icsd.mci.generator.beans.Game;
import com.aegean.icsd.mci.generator.beans.IndividualDescriptor;
import com.aegean.icsd.mci.generator.beans.Difficulty;
import com.aegean.icsd.mci.ontology.MciOntologyException;

public interface IGameGenerator {

  IndividualDescriptor generateGame(Game game, Difficulty difficulty, String playerName) throws MciOntologyException;
}
