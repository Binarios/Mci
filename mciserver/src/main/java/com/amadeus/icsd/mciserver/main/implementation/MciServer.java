package com.amadeus.icsd.mciserver.main.implementation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.jena.fuseki.main.FusekiServer;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amadeus.icsd.mciserver.configuration.interafeces.IServerConfiguration;
import com.amadeus.icsd.mciserver.main.interfaces.IMciServer;

@Service
public class MciServer implements IMciServer {

  @Autowired
  private IServerConfiguration config;

  @Override
  public FusekiServer startServer() throws FileNotFoundException {
    Model model = ModelFactory.createDefaultModel() ;
    model.read(new FileInputStream(config.getOntologyPath().toAbsolutePath().toString()),
            null, config.getOntologySyntax()) ;

    Dataset ds = DatasetFactory.create(model);
    FusekiServer server = FusekiServer.create()
            .add(config.getOntologyName(), ds)
            .build() ;
    server.start() ;
    server.stop();
    return server;
  }
}
