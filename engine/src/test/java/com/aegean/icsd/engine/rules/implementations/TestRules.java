package com.aegean.icsd.engine.rules.implementations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.rules.beans.EntityProperty;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.GameRules;
import com.aegean.icsd.engine.rules.beans.RestrictionType;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.beans.ValueRangeRestriction;
import com.aegean.icsd.ontology.IOntology;
import com.aegean.icsd.ontology.beans.CardinalitySchema;
import com.aegean.icsd.ontology.beans.DataRangeRestrinctionSchema;
import com.aegean.icsd.ontology.beans.ClassSchema;
import com.aegean.icsd.ontology.beans.PropertySchema;
import com.aegean.icsd.ontology.beans.RestrictionSchema;
import com.aegean.icsd.ontology.beans.OntologyException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Execution(ExecutionMode.CONCURRENT)
public class TestRules {

  @InjectMocks
  @Spy
  private Rules rules = new Rules();

  @Mock
  private IOntology ont;

  @Mock
  private ClassSchema indMock;

  @Mock
  private RestrictionSchema indResMock;

  @Mock
  private CardinalitySchema crdMock;

  @Mock
  private List listMock;

  @Mock
  private Iterator itMock;

  @Test
  public void testGetGameRules() throws RulesException, OntologyException {
    String gameName = "test";

    ClassSchema mockInd = mock(ClassSchema.class);
    List<PropertySchema> props = new ArrayList<>();
    given(ont.getClassSchema(any())).willReturn(mockInd);
    given(mockInd.getProperties()).willReturn(props);
    Mockito.doReturn(new ArrayList<>()).when(rules).getEntityRestrictions(mockInd);
    Mockito.doReturn(new ArrayList<>()).when(rules).getEntityProperties(props);

    GameRules res = rules.getGameRules(gameName, Difficulty.EASY);
    Assertions.assertNotNull(res);
    Assertions.assertEquals(gameName, res.getGameName());
  }

  @Test
  public void testGenerateGameRestrictionsOrder() {
    RestrictionSchema restrictionSchemaMock1 = mock(RestrictionSchema.class);
    RestrictionSchema restrictionSchemaMock2 = mock(RestrictionSchema.class);

    EntityRestriction gameRes1 = generateGameRes("test", RestrictionType.MIN, 2,"xsd:string");
    EntityRestriction gameRes2 = generateGameRes("test", RestrictionType.ONLY, -1,"xsd:string");

    given(indMock.getEqualityRestrictions()).willReturn(new ArrayList<>());
    given(indMock.getRestrictions()).willReturn(listMock);

    when(listMock.iterator()).thenReturn(itMock);
    when(itMock.hasNext()).thenReturn(true, true, false);
    when(itMock.next()).thenReturn(restrictionSchemaMock1, restrictionSchemaMock2);

    Mockito.doReturn(gameRes1).when(rules).getEntityRestriction(restrictionSchemaMock1);
    Mockito.doReturn(gameRes2).when(rules).getEntityRestriction(restrictionSchemaMock2);

    List<EntityRestriction> res = rules.getEntityRestrictions(indMock);
    Assertions.assertNotNull(res);
    Assertions.assertEquals(2, res.size());
    Assertions.assertEquals(RestrictionType.ONLY, res.get(0).getType());
    reset(listMock, indMock);
  }

  @Test
  public void testGetDataRanges() {
    String predicate = "predicate";
    String value = "value";
    String dataType = "dataType";

    DataRangeRestrinctionSchema dataRangeMock1 = mock(DataRangeRestrinctionSchema.class);
    DataRangeRestrinctionSchema dataRangeMock2 = mock(DataRangeRestrinctionSchema.class);

    given(indResMock.getCardinalitySchema()).willReturn(crdMock);
    given(crdMock.getDataRangeRestrictions()).willReturn(listMock);
    when(listMock.iterator()).thenReturn(itMock);
    when(itMock.hasNext()).thenReturn(true, true, false);
    when(itMock.next()).thenReturn(dataRangeMock1, dataRangeMock2);

    ValueRangeRestriction res = rules.getDataRanges(indResMock);

    Assertions.assertNotNull(res);
    Assertions.assertEquals(1, res.getRanges().size());
    Assertions.assertEquals(predicate, res.getRanges().get(0).getPredicate());
    Assertions.assertEquals(value, res.getRanges().get(0).getValue());
    Assertions.assertEquals(dataType, res.getDataType());
  }

  @Test
  public void testGetRestrictionCardinalityWithCardinality() {
    given(indResMock.getType()).willReturn(RestrictionSchema.EXACTLY_TYPE);
    given(indResMock.getCardinalitySchema()).willReturn(crdMock);
    given(crdMock.getOccurrence()).willReturn("1");
    int r = rules.getRestrictionCardinality(indResMock);
    Assertions.assertEquals(1, r);
  }

  @Test
  public void testGetRestrictionCardinalityWithValue() {
    given(indResMock.getType()).willReturn(RestrictionSchema.VALUE_TYPE);
    given(indResMock.getExactValue()).willReturn("1");
    int r = rules.getRestrictionCardinality(indResMock);
    Assertions.assertEquals(1, r);
  }

  @Test
  public void testGetRestrictionCardinalityWithOnlyValue() {
    given(indResMock.getType()).willReturn(RestrictionSchema.ONLY_TYPE);
    int r = rules.getRestrictionCardinality(indResMock);
    Assertions.assertEquals(-1, r);
  }

  private EntityRestriction generateGameRes(String propertyName, RestrictionType type, int cardinality, String range) {
    EntityProperty prop = new EntityProperty();
    prop.setName(propertyName);
    prop.setRange(range);

    EntityRestriction r = new EntityRestriction();
    r.setOnProperty(prop);
    r.setType(type);
    r.setCardinality(cardinality);
    return r;
  }
}
