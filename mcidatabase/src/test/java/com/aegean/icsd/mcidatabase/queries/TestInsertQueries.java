package com.aegean.icsd.mcidatabase.queries;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aegean.icsd.mcidatabase.MciDatabaseException;
import com.aegean.icsd.mcidatabase.ontology.IMciOntology;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TestInsertQueries {

  @InjectMocks
  private InsertParametrizedQuery ipq = new InsertParametrizedQuery();

  @Mock
  private IMciOntology ont;

  @Test
  public void testCreateInsertStmOneRelation() throws MciDatabaseException {
    given(ont.getPrefix()).willReturn("mci");
    given(ont.getNamespace()).willReturn("http://ontology");
    String result = ipq.createInsert("mci:testSub",generateLiteralRelations(1));

    Assertions.assertNotNull(result);
    String expected = "PREFIX  mci:  <http://ontology>\n\n" +
                      "INSERT DATA {\n" +
                      "  <mci:testSub> <mci:p0> \"o0\" .\n}\n";
    Assertions.assertEquals(expected,result);

  }

  @Test
  public void testCreateInsertStmManyRelations() throws MciDatabaseException {
    given(ont.getPrefix()).willReturn("mci");
    given(ont.getNamespace()).willReturn("http://ontology");
    String result = ipq.createInsert("mci:testSub",generateLiteralRelations(2));

    Assertions.assertNotNull(result);
    String expected = "PREFIX  mci:  <http://ontology>\n\n" +
            "INSERT DATA {\n" +
            "  <mci:testSub> <mci:p0> \"o0\" .\n" +
            "  <mci:testSub> <mci:p1> \"o1\" .\n}\n";
    Assertions.assertEquals(expected,result);

  }

  private Map<String, String> generateLiteralRelations(int size) {
    Map<String, String> relations = new HashMap<>();
    for(int i = 0; i < size; i++) {
      relations.put("mci:p"+i, "o"+i);
    }
    return relations;
  }
}
