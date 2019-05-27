package com.aegean.icsd.mcidatabase.connection;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.springframework.stereotype.Service;

import com.aegean.icsd.mcidatabase.MciDatabaseException;

@Service
public class TdbConnection implements ITdbConnection {

  private Properties dbProps;
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
      String driver = getDatabasePropertyValue("driver");
      String dataSetLocation = getDatabasePropertyValue("datasetDir");
      return driver + "=" + dataSetLocation;
    } catch (IOException e) {
      throw new MciDatabaseException("tdb.2", "Cannot retrieve connection string", e);
    }
  }

  @Override
  public String getLocation() throws MciDatabaseException {
    try {
      return getDatabasePropertyValue("datasetDir");
    } catch (IOException e) {
      throw new MciDatabaseException("tdb.3", "Cannot retrieve location", e);
    }
  }


  String getDatabasePropertyValue(String key) throws IOException {
    if (dbProps == null) {
      String rootPath = TdbConnection.class.getResource("").getPath();
      String configPath = rootPath + "/database.properties";
      dbProps = new Properties();
      dbProps.load(new FileInputStream(configPath));
    }
    return dbProps.getProperty(key);
  }
}
