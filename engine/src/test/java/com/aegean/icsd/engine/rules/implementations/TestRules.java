package com.aegean.icsd.engine.rules.implementations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aegean.icsd.engine.rules.beans.GameRestriction;
import com.aegean.icsd.engine.rules.beans.GameRules;
import com.aegean.icsd.engine.rules.beans.RestrictionType;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.beans.ValueRangeRestriction;
import com.aegean.icsd.ontology.IOntology;
import com.aegean.icsd.ontology.beans.Cardinality;
import com.aegean.icsd.ontology.beans.DataRangeRestrinction;
import com.aegean.icsd.ontology.beans.Individual;
import com.aegean.icsd.ontology.beans.IndividualProperty;
import com.aegean.icsd.ontology.beans.IndividualRestriction;
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
  private Individual indMock;

  @Mock
  private IndividualRestriction indResMock;

  @Mock
  private Cardinality crdMock;

  @Mock
  private List listMock;

  @Mock
  private Iterator itMock;

  @Test
  public void testGetGameRules() throws RulesException, OntologyException {
    String gameName = "test";
    String difficulty = "easy";

    Individual mockInd = mock(Individual.class);
    List<IndividualProperty> props = new ArrayList<>();
    given(ont.generateIndividual(any())).willReturn(mockInd);
    given(mockInd.getProperties()).willReturn(props);
    Mockito.doReturn(new ArrayList<>()).when(rules).generateGameRestrictions(mockInd);
    Mockito.doReturn(new ArrayList<>()).when(rules).generateGameProperties(props);

    GameRules res = rules.getGameRules(gameName, difficulty);
    Assertions.assertNotNull(res);
    Assertions.assertEquals(gameName, res.getGameName());
  }

  @Test
  public void testGenerateGameRestrictionsOrder() {
    IndividualRestriction individualRestrictionMock1 = mock(IndividualRestriction.class);
    IndividualRestriction individualRestrictionMock2 = mock(IndividualRestriction.class);

    GameRestriction gameRes1 = generateGameRes("test", RestrictionType.MIN, 2,"xsd:string");
    GameRestriction gameRes2 = generateGameRes("test", RestrictionType.ONLY, -1,"xsd:string");

    given(indMock.getEqualityRestrictions()).willReturn(new ArrayList<>());
    given(indMock.getRestrictions()).willReturn(listMock);

    when(listMock.iterator()).thenReturn(itMock);
    when(itMock.hasNext()).thenReturn(true, true, false);
    when(itMock.next()).thenReturn(individualRestrictionMock1, individualRestrictionMock2);

    Mockito.doReturn(gameRes1).when(rules).generateGameRestriction(individualRestrictionMock1);
    Mockito.doReturn(gameRes2).when(rules).generateGameRestriction(individualRestrictionMock2);

    List<GameRestriction> res = rules.generateGameRestrictions(indMock);
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

    DataRangeRestrinction dataRangeMock1 = mock(DataRangeRestrinction.class);
    DataRangeRestrinction dataRangeMock2 = mock(DataRangeRestrinction.class);

    given(indResMock.getCardinality()).willReturn(crdMock);
    given(crdMock.getDataRangeRestrictions()).willReturn(listMock);
    when(listMock.iterator()).thenReturn(itMock);
    when(itMock.hasNext()).thenReturn(true, true, false);
    when(itMock.next()).thenReturn(dataRangeMock1, dataRangeMock2);

    Mockito.doReturn(null).when(rules).toValueRangeRestriction(dataRangeMock1);
    Mockito.doReturn(generateRangeRes(value, predicate, dataType)).when(rules).toValueRangeRestriction(dataRangeMock2);

    List<ValueRangeRestriction> res = rules.getDataRanges(indResMock);

    Assertions.assertNotNull(res);
    Assertions.assertEquals(1, res.size());
    Assertions.assertEquals(predicate, res.get(0).getPredicate());
    Assertions.assertEquals(value, res.get(0).getValue());
    Assertions.assertEquals(dataType, res.get(0).getDataType());
  }

  @Test
  public void testGetRestrictionCardinalityWithCardinality() {
    given(indResMock.getType()).willReturn(IndividualRestriction.EXACTLY_TYPE);
    given(indResMock.getCardinality()).willReturn(crdMock);
    given(crdMock.getOccurrence()).willReturn("1");
    int r = rules.getRestrictionCardinality(indResMock);
    Assertions.assertEquals(1, r);
  }

  @Test
  public void testGetRestrictionCardinalityWithValue() {
    given(indResMock.getType()).willReturn(IndividualRestriction.VALUE_TYPE);
    given(indResMock.getExactValue()).willReturn("1");
    int r = rules.getRestrictionCardinality(indResMock);
    Assertions.assertEquals(1, r);
  }

  @Test
  public void testGetRestrictionCardinalityWithOnlyValue() {
    given(indResMock.getType()).willReturn(IndividualRestriction.ONLY_TYPE);
    int r = rules.getRestrictionCardinality(indResMock);
    Assertions.assertEquals(-1, r);
  }

  private GameRestriction generateGameRes(String propertyName, RestrictionType type, int cardinality, String range) {
    GameRestriction r = new GameRestriction();
    r.setOnProperty(propertyName);
    r.setType(type);
    r.setCardinality(cardinality);
    r.setRange(range);
    return r;
  }

  private ValueRangeRestriction generateRangeRes(String value, String predicate, String dataType) {
    ValueRangeRestriction r = new ValueRangeRestriction();

    r.setValue(value);
    r.setPredicate(predicate);
    r.setDataType(dataType);

    return r;
  }
}
