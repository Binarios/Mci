package com.aegean.icsd.engine;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"com.aegean.icsd.engine", "com.aegean.icsd.ontology"})
public class EngineConfiguration {
}
