package com.aegean.icsd.connection;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestTdbConnection {

  private TdbConnection svc = new TdbConnection();

  @Test
  void testConnect() throws ConnectionException, SQLException {
    Connection result = svc.connect("../../dataset");

    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isValid(0));
  }
}
