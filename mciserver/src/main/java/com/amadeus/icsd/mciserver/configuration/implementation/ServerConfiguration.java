package com.amadeus.icsd.mciserver.configuration.implementation;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;

import com.amadeus.icsd.mciserver.configuration.interafeces.IServerConfiguration;

@Service
public class ServerConfiguration implements IServerConfiguration {

  @Override
  public Path getOntologyPath() {
    return Paths.get("D:\\WorkBench\\Diplomatiki\\MciOntology\\", "games.owl");
  }

  @Override
  public String getOntologySyntax() {
    return "ttl";
  }

  @Override
  public String getOntologyName() {
    return "games";
  }
}
