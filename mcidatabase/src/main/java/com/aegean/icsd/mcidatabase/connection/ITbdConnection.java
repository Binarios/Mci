package com.aegean.icsd.mcidatabase.connection;

import java.sql.Connection;

import com.aegean.icsd.mcidatabase.MciDatabaseException;

public interface ITbdConnection {

  Connection getConnection() throws MciDatabaseException;

  String getConnectionString() throws MciDatabaseException;

}
