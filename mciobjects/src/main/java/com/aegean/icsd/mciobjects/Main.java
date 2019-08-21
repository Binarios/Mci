package com.aegean.icsd.mciobjects;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.aegean.icsd.engine.EngineConfiguration;
import com.aegean.icsd.mciobjects.common.beans.ProviderException;
import com.aegean.icsd.ontology.OntologyConfiguration;

public class Main {
  public static void main(String[] args) throws ProviderException {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(MciObjectsConfiguration.class,
      EngineConfiguration.class,
      OntologyConfiguration.class);

    ObjectsInitiator init = ctx.getBean(ObjectsInitiator.class);
    init.setupObjects();
  }
}
