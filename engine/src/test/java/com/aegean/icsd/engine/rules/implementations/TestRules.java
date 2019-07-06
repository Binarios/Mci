package com.aegean.icsd.engine.rules.implementations;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aegean.icsd.engine.rules.beans.GameRestriction;
import com.aegean.icsd.engine.rules.beans.GameRules;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.ontology.IOntology;
import com.aegean.icsd.ontology.beans.Individual;
import com.aegean.icsd.ontology.beans.IndividualProperty;
import com.aegean.icsd.ontology.beans.OntologyException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@Execution(ExecutionMode.CONCURRENT)
public class TestRules {

  @InjectMocks
  @Spy
  private Rules rules = new Rules();

  @Mock
  private IOntology ont;

  @Test
  public void testGetGameRules() throws RulesException, OntologyException {
    String gameName = "test";
    String difficulty = "easy";

    Individual mockInd = mock(Individual.class);
    List<IndividualProperty> props = new ArrayList<>();
    given(ont.generateIndividual(any())).willReturn(mockInd);
    given(mockInd.getProperties()).willReturn(props);
    Mockito.doReturn( new ArrayList<>()).when(rules).generateGameRestrictions(mockInd);
    Mockito.doReturn( new ArrayList<>()).when(rules).generateGameProperties(props);

    GameRules res = rules.getGameRules(gameName, difficulty);
    Assertions.assertNotNull(res);
    Assertions.assertEquals(gameName, res.getGameName());
  }

}
