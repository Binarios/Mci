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
import com.aegean.icsd.engine.rules.beans.EntityRules;
import com.aegean.icsd.engine.rules.beans.RestrictionType;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.beans.ValueRangeRestriction;
import com.aegean.icsd.ontology.beans.CardinalitySchema;
import com.aegean.icsd.ontology.beans.DataRangeRestrinctionSchema;
import com.aegean.icsd.ontology.beans.ClassSchema;
import com.aegean.icsd.ontology.beans.PropertySchema;
import com.aegean.icsd.ontology.beans.RestrictionSchema;
import com.aegean.icsd.ontology.beans.OntologyException;
import com.aegean.icsd.ontology.interfaces.IMciModelReader;

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
  private IMciModelReader model;

  @Mock
  private ClassSchema classSchema;

  @Mock
  private RestrictionSchema restrictionSchema;

  @Mock
  private CardinalitySchema cardinalitySchema;

  @Mock
  private PropertySchema propertySchema;

  @Mock
  private List list;

  @Mock
  private Iterator iterator;

  @Test
  public void testGetGameRules() throws RulesException, OntologyException {
    String gameName = "test";

    ClassSchema mockInd = mock(ClassSchema.class);
    List<PropertySchema> props = new ArrayList<>();
    given(model.getClassSchema(any())).willReturn(mockInd);
    given(mockInd.getProperties()).willReturn(props);
    Mockito.doReturn(new ArrayList<>()).when(rules).getEntityRestrictions(mockInd);
    Mockito.doReturn(new ArrayList<>()).when(rules).getEntityProperties(props);

   EntityRules res = rules.getGameRules(gameName, Difficulty.EASY);
    Assertions.assertNotNull(res);
  }

  @Test
  public void testGenerateGameRestrictionsOrder() {
    RestrictionSchema restrictionSchemaMock1 = mock(RestrictionSchema.class);
    RestrictionSchema restrictionSchemaMock2 = mock(RestrictionSchema.class);

    EntityRestriction gameRes1 = generateGameRes("test", RestrictionType.MIN, 2,"xsd:string");
    EntityRestriction gameRes2 = generateGameRes("test2", RestrictionType.ONLY, -1,"xsd:string");

    given(classSchema.getEqualityRestrictions()).willReturn(new ArrayList<>());
    given(classSchema.getRestrictions()).willReturn(list);

    when(list.iterator()).thenReturn(iterator);
    when(iterator.hasNext()).thenReturn(true, true, false);
    when(iterator.next()).thenReturn(restrictionSchemaMock1, restrictionSchemaMock2);

    Mockito.doReturn(gameRes1).when(rules).getEntityRestriction(restrictionSchemaMock1);
    Mockito.doReturn(gameRes2).when(rules).getEntityRestriction(restrictionSchemaMock2);

    List<EntityRestriction> res = rules.getEntityRestrictions(classSchema);
    Assertions.assertNotNull(res);
    Assertions.assertEquals(2, res.size());
    Assertions.assertEquals(RestrictionType.MIN, res.get(0).getType());
    reset(list, classSchema);
  }

  @Test
  public void testGetDataRanges() {
    String predicate = "minInclusive";
    String value = "value";
    String dataType = "dataType";

    DataRangeRestrinctionSchema dataRangeMock1 = mock(DataRangeRestrinctionSchema.class);
    DataRangeRestrinctionSchema dataRangeMock2 = mock(DataRangeRestrinctionSchema.class);
    List<DataRangeRestrinctionSchema> listMock = new ArrayList<>();
    listMock.add(dataRangeMock1);
    listMock.add(dataRangeMock2);
    given(restrictionSchema.getCardinalitySchema()).willReturn(cardinalitySchema);
    given(restrictionSchema.getOnPropertySchema()).willReturn(propertySchema);
    given(propertySchema.getRange()).willReturn(dataType);
    given(cardinalitySchema.getDataRangeRestrictions()).willReturn(listMock);
    given(dataRangeMock1.getDatatype()).willReturn(dataType);
    given(dataRangeMock1.getPredicate()).willReturn(predicate);
    given(dataRangeMock1.getValue()).willReturn(value);
    given(dataRangeMock2.getDatatype()).willReturn(dataType);


    ValueRangeRestriction res = rules.getDataRanges(restrictionSchema);

    Assertions.assertNotNull(res);
    Assertions.assertEquals(2, res.getRanges().size());
    Assertions.assertEquals(predicate, res.getRanges().get(0).getPredicate().getName());
    Assertions.assertEquals(value, res.getRanges().get(0).getValue());
    Assertions.assertEquals(dataType, res.getDataType());
  }

  @Test
  public void testGetRestrictionCardinalityWithCardinality() {
    given(restrictionSchema.getType()).willReturn(RestrictionSchema.EXACTLY_TYPE);
    given(restrictionSchema.getCardinalitySchema()).willReturn(cardinalitySchema);
    given(cardinalitySchema.getOccurrence()).willReturn("1");
    int r = rules.getRestrictionCardinality(restrictionSchema);
    Assertions.assertEquals(1, r);
  }

  @Test
  public void testGetRestrictionCardinalityWithValue() {
    given(restrictionSchema.getType()).willReturn(RestrictionSchema.VALUE_TYPE);
    int r = rules.getRestrictionCardinality(restrictionSchema);
    Assertions.assertEquals(-1, r);
  }

  @Test
  public void testGetRestrictionCardinalityWithOnlyValue() {
    given(restrictionSchema.getType()).willReturn(RestrictionSchema.ONLY_TYPE);
    int r = rules.getRestrictionCardinality(restrictionSchema);
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
