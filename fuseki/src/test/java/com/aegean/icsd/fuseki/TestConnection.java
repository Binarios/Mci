package com.aegean.icsd.fuseki;

import org.apache.jena.fuseki.main.FusekiServer;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.junit.jupiter.api.Test;

public class TestConnection {

  @Test
  public void test() {
    Dataset ds = null;
            DatasetFactory. create("C:\\Users\\Desouleo\\WORKBENCH\\MciOntology\\games.owl");
    FusekiServer server = FusekiServer.create()
            .add("/games", ds)
            .build() ;
    server.start() ;
    server.stop();
  }
}
