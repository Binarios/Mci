package com.aegean.icsd.engine;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.generator.implementations.Generator;
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

  Rules r;
  Ontology o;
  Generator g;

  @BeforeEach
  public void setup() {
    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(EngineConfiguration.class,
      OntologyConfiguration.class);

    r = ctx.getBean(Rules.class);
    g = ctx.getBean(Generator.class);
    o = ctx.getBean(Ontology.class);
  }

  @Test
  public void testSpring() {
    Assertions.assertNotNull(r);
    Assertions.assertNotNull(o);
    Assertions.assertNotNull(g);
  }

  @Test
  @Disabled("for api discovery purposes")
  public void testEngine() throws EngineException {
    TestWordBean test = new TestWordBean();
    test.setValue("UT");

    String id = g.upsertObj(test);

    Assertions.assertNotNull(id);
    Assertions.assertEquals(TestWordBean.NAME + "_"+test.getValue(), id);


  }
}
