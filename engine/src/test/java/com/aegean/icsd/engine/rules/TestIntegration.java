package com.aegean.icsd.engine.rules;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.aegean.icsd.ontology.OntologyConfiguration;
import com.aegean.icsd.engine.EngineConfiguration;
import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.rules.beans.EntityProperty;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.GameRules;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.implementations.Rules;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.ontology.IOntology;
import com.aegean.icsd.ontology.Ontology;

@ExtendWith(MockitoExtension.class)
@Execution(ExecutionMode.CONCURRENT)
public class TestIntegration {

  @Test
  public void testSpring() {
    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(EngineConfiguration.class,
            OntologyConfiguration.class);

    IRules r = ctx.getBean(Rules.class);
    IOntology o = ctx.getBean(Ontology.class);

    Assertions.assertNotNull(r);
    Assertions.assertNotNull(o);
  }

  @Test
  @Disabled("Unstable")
  public void testRulesGeneration() throws RulesException {
    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(EngineConfiguration.class,
            OntologyConfiguration.class);

    IRules r = ctx.getBean(Rules.class);
    String gameName = "Observation";
    List<EntityRestriction> restrictions= r.getGameRules(gameName, Difficulty.EASY);

    Assertions.assertNotNull(restrictions);
    Assertions.assertEquals(4, restrictions.size());
  }
}
