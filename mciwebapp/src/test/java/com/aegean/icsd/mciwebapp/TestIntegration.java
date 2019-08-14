package com.aegean.icsd.mciwebapp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.observations.beans.ObservationResponse;
import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.mciwebapp.observations.interfaces.IObservationSvc;
import com.aegean.icsd.ontology.IOntology;

@Execution(ExecutionMode.CONCURRENT)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { WebAppConfig.class})
@WebAppConfiguration
@Disabled
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
  public void testObsIns() throws MciException {
    String playerName = "TestUser";

    ObservationResponse obs = observationImpl.createObservation(playerName, Difficulty.HARD);

    Assertions.assertNotNull(obs);
    Assertions.assertEquals(playerName, obs.getGame().getPlayerName());
    Assertions.assertEquals(Difficulty.HARD, obs.getGame().getDifficulty());
    Assertions.assertEquals(180000, obs.getGame().getMaxCompletionTime());
    Assertions.assertEquals(4, obs.getWords().size());
    Assertions.assertEquals(4, obs.getItems().size());
  }
}
