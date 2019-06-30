package com.aegean.icsd.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.stereotype.Service;

@Service
public class TdbConnection implements ITdbConnection {

  private Connection connection;

  @Override
  public Connection connect(String directory) throws ConnectionException {
    try {
      if (connection == null || connection.isClosed() || !connection.isValid(0)) {
        connection = DriverManager.getConnection("jdbc:jena:tdb:location=" + directory);
      }
      return connection;
    } catch (SQLException e) {
      throw new ConnectionException("tdb.1", "Cannot retrieve connection", e);
    }
  }
}
