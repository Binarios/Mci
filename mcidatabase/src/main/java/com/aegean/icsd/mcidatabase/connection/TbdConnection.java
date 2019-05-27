package com.aegean.icsd.mcidatabase.connection;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.aegean.icsd.mcidatabase.MciDatabaseException;

public class TbdConnection implements ITbdConnection {

  private Properties appProps;
  private Connection connection;

  @Override
  public Connection getConnection() throws MciDatabaseException {
    try {
      if (connection == null || connection.isClosed() || !connection.isValid(0)) {
        connection = DriverManager.getConnection(getConnectionString());
      }
      return connection;
    } catch (SQLException e) {
      throw new MciDatabaseException("tdb.1", "Cannot retrieve connection", e);
    }
  }

  @Override
  public String getConnectionString() throws MciDatabaseException {
    try {
      String driver = getPropertyValue("driver");
      String dataSetLocation = getPropertyValue("datasetDir");
      return driver + "=" + dataSetLocation;
    } catch (IOException e) {
      throw new MciDatabaseException("tdb.2", "Cannot retrieve connection string", e);
    }
  }


  String getPropertyValue(String key) throws IOException {
    String rootPath = TbdConnection.class.getResource("").getPath();
    String appConfigPath = rootPath + "/database.properties";
    if (appProps == null) {
      appProps = new Properties();
    }
    appProps.load(new FileInputStream(appConfigPath));
    return appProps.getProperty(key);
  }
}
