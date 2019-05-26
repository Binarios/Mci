package com.amadeus.icsd.mciserver.configuration.interafeces;

import java.nio.file.Path;

public interface IServerConfiguration {

  Path getOntologyPath();

  String getOntologySyntax();

  String getOntologyName();
}
