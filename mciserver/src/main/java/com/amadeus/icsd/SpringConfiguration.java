package com.amadeus.icsd;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.amadeus.icsd.mciserver.configuration.implementation.ServerConfiguration;
import com.amadeus.icsd.mciserver.configuration.interafeces.IServerConfiguration;
import com.amadeus.icsd.mciserver.main.implementation.MciServer;
import com.amadeus.icsd.mciserver.main.interfaces.IMciServer;

@Configuration
@ComponentScan("com.aegean.icsd")
public class SpringConfiguration {

  @Bean
  public IServerConfiguration getServerConfiguration() {
    return new ServerConfiguration();
  }

  @Bean
  public IMciServer getServer() {
    return new MciServer();
  }
}
