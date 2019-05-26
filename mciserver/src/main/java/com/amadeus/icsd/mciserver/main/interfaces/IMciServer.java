package com.amadeus.icsd.mciserver.main.interfaces;

import java.io.FileNotFoundException;

import org.apache.jena.fuseki.main.FusekiServer;

public interface IMciServer {

  FusekiServer startServer() throws FileNotFoundException;
}
