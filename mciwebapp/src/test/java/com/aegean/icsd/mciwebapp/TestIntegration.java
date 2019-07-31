package com.aegean.icsd.mciwebapp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import com.aegean.icsd.ontology.OntologyConfiguration;
import com.aegean.icsd.engine.EngineConfiguration;
import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.rules.implementations.Rules;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.mciwebapp.observations.beans.Observation;
import com.aegean.icsd.mciwebapp.observations.beans.ObservationsException;
import com.aegean.icsd.mciwebapp.observations.implementations.ObservationImpl;
import com.aegean.icsd.mciwebapp.observations.interfaces.IObservationSvc;
import com.aegean.icsd.ontology.IOntology;
import com.aegean.icsd.ontology.Ontology;

@Execution(ExecutionMode.CONCURRENT)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { WebAppConfig.class})
@WebAppConfiguration
public class TestIntegration {

  @Autowired
  IObservationSvc observationImpl;

  @Autowired
  IRules r;

  @Autowired
  IOntology o;

  @Test
  public void testSpring() {
    Assertions.assertNotNull(observationImpl);
    Assertions.assertNotNull(r);
    Assertions.assertNotNull(o);
  }

  @Test
  @Disabled("Unstable")
  public void testObsIns() throws ObservationsException {
    String playerName = "TestUser";

    Observation obs = observationImpl.createObservation(playerName, Difficulty.EASY);
    Assertions.assertNotNull(obs);
    Assertions.assertEquals(playerName, obs.getPlayerName());
    Assertions.assertEquals(Difficulty.EASY, obs.getDifficulty());
    Assertions.assertEquals("1800", obs.getMaxCompletionTime());
  }
}
