package com.aegean.icsd.queries;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aegean.icsd.queries.InsertQuery;
import com.aegean.icsd.queries.beans.InsertParam;

@ExtendWith(MockitoExtension.class)
@Execution(ExecutionMode.CONCURRENT)

public class TestInsertQuery {

  @Test
  public void testBuilder() {
    InsertParam subject = new InsertParam();
    subject.setIriParam(true);
    subject.setName("?sub");

    InsertParam predicate = new InsertParam();
    predicate.setIriParam(true);
    predicate.setName("?pred");

    InsertParam object = new InsertParam();
    object.setIriParam(false);
    object.setName("?obj");

    InsertQuery insertQuery = new InsertQuery.Builder()
      .insertEntry(subject, "mci:EasyObservation")
      .addRelation(predicate, object)
      .build();

    String expected = "INSERT DATA {\n\t?sub ?rdfType ?typeToAssociate ;\n\t\t?pred ?obj .\n}\n";

    Assertions.assertEquals(expected, insertQuery.getCommand());
  }

}
