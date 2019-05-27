package com.aegean.icsd.mcidatabase.connection;

import java.sql.Connection;

import com.aegean.icsd.mcidatabase.MciDatabaseException;

public interface ITdbConnection {

  Connection getConnection() throws MciDatabaseException;

  String getConnectionString() throws MciDatabaseException;

  String getLocation() throws MciDatabaseException;
}
