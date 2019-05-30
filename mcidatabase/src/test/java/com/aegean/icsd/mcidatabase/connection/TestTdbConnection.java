package com.aegean.icsd.mcidatabase.connection;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.aegean.icsd.mcidatabase.MciDatabaseException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestTdbConnection {

  private TdbConnection svc;

  @BeforeAll
  public void setup() {
    svc = new TdbConnection();
  }

  @Test
  void testConnect() throws MciDatabaseException, SQLException {
    Connection result = svc.getConnection();

    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isValid(0));
  }

  @Test
  void testConnectionString() throws MciDatabaseException {
    String result = svc.getConnectionString();
    Assertions.assertNotNull(result);
    Assertions.assertEquals("jdbc:jena:tdb:location=..\\..\\ontology", result);
  }

  @Test
  void testGetLocation() throws MciDatabaseException {
    String result = svc.getLocation();
    Assertions.assertNotNull(result);
    Assertions.assertEquals("..\\..\\ontology", result);
  }
}
