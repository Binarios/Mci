package com.aegean.icsd.mciwebapp.observations.implementations;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.RestrictionType;
import com.aegean.icsd.engine.rules.beans.ValueRange;
import com.aegean.icsd.engine.rules.beans.ValueRangeRestriction;
import com.aegean.icsd.engine.rules.beans.ValueRangeType;
import com.aegean.icsd.mciwebapp.observations.beans.ObservationsException;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
@Execution(ExecutionMode.CONCURRENT)
public class TestObsSvc {

  @InjectMocks
  @Spy
  private ObservationImpl svc = new ObservationImpl();

  @Mock
  private IGenerator generator;

  @Test
  public void testCalculateStringDataValue() {
    String expected = "hello my love!";
    ValueRangeRestriction res = new ValueRangeRestriction();
    res.setDataType("string");

    String value = svc.calculateDataValue(res);

    Assertions.assertNotNull(value);
    Assertions.assertEquals(expected, value);
  }

  @Test
  public void testCalculateEqualsIntDataValue() {
    String expected = "21";
    ValueRange range = new ValueRange();
    range.setValue(expected);
    range.setPredicate(ValueRangeType.EQUALS);
    List<ValueRange> ranges = new ArrayList<>();
    ranges.add(range);
    ValueRangeRestriction res = new ValueRangeRestriction();
    res.setDataType("positiveInteger");
    res.setRanges(ranges);

    given(generator.generateIntDataValue(eq(res))).willReturn(21);

    String value = svc.calculateDataValue(res);

    Assertions.assertNotNull(value);
    Assertions.assertEquals(expected, value);
  }

  @Test
  public void testCalculateMaxIntDataValue() {
    int max = 3;
    ValueRange maxRange = new ValueRange();
    maxRange.setValue("" + max);
    maxRange.setPredicate(ValueRangeType.MAX);
    List<ValueRange> ranges = new ArrayList<>();
    ranges.add(maxRange);
    ValueRangeRestriction res = new ValueRangeRestriction();
    res.setDataType("positiveInteger");
    res.setRanges(ranges);

    given(generator.generateIntDataValue(eq(res))).willReturn(2);

    String value = svc.calculateDataValue(res);

    Assertions.assertNotNull(value);
    Assertions.assertTrue(Integer.parseInt(value) < max);
  }

  @Test
  public void testCalculateMaxIntDataValueEquals() {
    int max = 1;
    ValueRange maxRange = new ValueRange();
    maxRange.setValue("" + max);
    maxRange.setPredicate(ValueRangeType.MAX);
    List<ValueRange> ranges = new ArrayList<>();
    ranges.add(maxRange);
    ValueRangeRestriction res = new ValueRangeRestriction();
    res.setDataType("positiveInteger");
    res.setRanges(ranges);

    given(generator.generateIntDataValue(eq(res))).willReturn(0);

    String value = svc.calculateDataValue(res);

    Assertions.assertNotNull(value);
    Assertions.assertEquals(0, Integer.parseInt(value));
  }

  @Test
  public void testCalculateMinMaxIntDataValue() {
    int min = 21;
    int max = 23;
    ValueRange minRange = new ValueRange();
    minRange.setValue("" + min);
    minRange.setPredicate(ValueRangeType.MIN);
    ValueRange maxRange = new ValueRange();
    maxRange.setValue("" + max);
    maxRange.setPredicate(ValueRangeType.MAX);
    List<ValueRange> ranges = new ArrayList<>();
    ranges.add(minRange);
    ranges.add(maxRange);
    ValueRangeRestriction res = new ValueRangeRestriction();
    res.setDataType("positiveInteger");
    res.setRanges(ranges);

    given(generator.generateIntDataValue(eq(res))).willReturn(22);

    String value = svc.calculateDataValue(res);

    Assertions.assertNotNull(value);
    Assertions.assertEquals("22", value);
  }

  @Test
  public void testCalculateMinMaxInclusiveIntDataValue() {
    int min = 21;
    int max = 25;
    ValueRange minRange = new ValueRange();
    minRange.setValue("" + min);
    minRange.setPredicate(ValueRangeType.MIN);
    ValueRange maxRange = new ValueRange();
    maxRange.setValue("" + max);
    maxRange.setPredicate(ValueRangeType.MAX);
    List<ValueRange> ranges = new ArrayList<>();
    ranges.add(minRange);
    ranges.add(maxRange);
    ValueRangeRestriction res = new ValueRangeRestriction();
    res.setDataType("positiveInteger");
    res.setRanges(ranges);

    given(generator.generateIntDataValue(eq(res))).willReturn(23);

    String value = svc.calculateDataValue(res);

    Assertions.assertNotNull(value);
    Assertions.assertTrue(Integer.parseInt(value) >= min);
    Assertions.assertTrue(Integer.parseInt(value) <= max);

  }

  @Test
  public void testCalculateRestrictionCardinality() {
    int expected = 2;
    EntityRestriction restriction = new EntityRestriction();
    restriction.setType(RestrictionType.EXACTLY);
    restriction.setCardinality(expected);
    int result = svc.calculateRestrictionCardinality(restriction);
    Assertions.assertEquals(expected,result );
  }

  @Test
  public void testCalculateCardinalitySome() {
    EntityRestriction restriction = new EntityRestriction();
    restriction.setType(RestrictionType.SOME);
    int result = svc.calculateRestrictionCardinality(restriction);
    Assertions.assertTrue(result > -1);
  }

  @Test
  public void testCalculateMinMaxCardinality() {
    EntityRestriction res1 = new EntityRestriction();
    res1.setType(RestrictionType.MIN);
    res1.setCardinality(1);
    EntityRestriction res2 = new EntityRestriction();
    res2.setType(RestrictionType.MAX);
    res2.setCardinality(2);

    List<EntityRestriction> restrictions = new ArrayList<>();
    restrictions.add(res1);
    restrictions.add(res2);

    int cardinality = svc.calculateMinMaxCardinality(restrictions);

    Assertions.assertTrue(cardinality >= 1);
    Assertions.assertTrue(cardinality <= 2);
  }

  @Test
  public void testCalculateCardinalityMax() throws ObservationsException {
    EntityRestriction res1 = new EntityRestriction();
    res1.setType(RestrictionType.MAX);
    res1.setCardinality(2);

    List<EntityRestriction> restrictions = new ArrayList<>();
    restrictions.add(res1);

    int cardinality = svc.calculateCardinality(restrictions);

    Assertions.assertTrue(cardinality >= 0);
    Assertions.assertTrue(cardinality <= res1.getCardinality());
  }

  @Test
  public void testCalculateCardinalityMinMax() throws ObservationsException {
    EntityRestriction res1 = new EntityRestriction();
    res1.setType(RestrictionType.MIN);
    res1.setCardinality(1);
    EntityRestriction res2 = new EntityRestriction();
    res2.setType(RestrictionType.MAX);
    res2.setCardinality(2);

    List<EntityRestriction> restrictions = new ArrayList<>();
    restrictions.add(res1);
    restrictions.add(res2);

    int cardinality = svc.calculateCardinality(restrictions);

    Assertions.assertTrue(cardinality >= res1.getCardinality());
    Assertions.assertTrue(cardinality <= res2.getCardinality());
  }

  @Test
  public void testCalculateCardinalityExactly() throws ObservationsException {
    EntityRestriction res1 = new EntityRestriction();
    res1.setType(RestrictionType.EXACTLY);
    res1.setCardinality(3);

    List<EntityRestriction> restrictions = new ArrayList<>();
    restrictions.add(res1);

    int cardinality = svc.calculateCardinality(restrictions);

    Assertions.assertEquals(res1.getCardinality(), cardinality);
  }


}
