package com.aegean.icsd.mcidatabase;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Utils {
  private static Properties dbProps;
  private static Properties ontProps;

  public static String getDatabasePropertyValue(String key) throws IOException {
    if (dbProps == null) {
      String rootPath = Utils.class.getResource("").getPath();
      String configPath = rootPath + "/connection/database.properties";
      dbProps = new Properties();
      dbProps.load(new FileInputStream(configPath));
    }
    return dbProps.getProperty(key);
  }

  public static String getOntologyPropertyValue(String key) throws IOException {
    if (ontProps == null) {
      String rootPath = Utils.class.getResource("").getPath();
      String configPath = rootPath + "/connection/ontology.properties";
      ontProps = new Properties();
      ontProps.load(new FileInputStream(configPath));
    }
    return ontProps.getProperty(key);
  }
}
