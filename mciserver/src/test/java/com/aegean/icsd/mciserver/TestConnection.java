package com.aegean.icsd.mciserver;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.jena.fuseki.main.FusekiServer;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.Test;

class TestConnection {

  @Test
  void test() throws FileNotFoundException {
    Model model = ModelFactory.createDefaultModel() ;
    model.read(new FileInputStream("D:\\WorkBench\\Diplomatiki\\MciOntology\\games.owl"), null, "ttl") ;
    Dataset ds = DatasetFactory.create(model);
    FusekiServer server = FusekiServer.create()
            .add("/games", ds)
            .build() ;
    server.start() ;
    server.stop();
  }

  @Test
  void testSpring() {

  }
}
