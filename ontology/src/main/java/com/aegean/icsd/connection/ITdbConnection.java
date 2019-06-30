package com.aegean.icsd.connection;

import java.sql.Connection;

public interface ITdbConnection {

  Connection connect(String directory) throws ConnectionException;
}
