package com.aegean.icsd.mcidatabase.connection;

import java.sql.Connection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.aegean.icsd.mcidatabase.MciDatabaseException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestConnection {

  private ITbdConnection svc;

  @BeforeAll
  public void setup() {
    svc = new TbdConnection();
  }

  @Test
  void testConnect() throws MciDatabaseException {
    Connection result = svc.getConnection();
    Assertions.assertNotNull(result);
  }

  @Test
  void testConnectionString() throws MciDatabaseException {
    String result = svc.getConnectionString();
    Assertions.assertNotNull(result);
    Assertions.assertEquals("jdbc:jena:tdb:location=D:\\WorkBench\\Diplomatiki\\dataset", result);
  }
}
