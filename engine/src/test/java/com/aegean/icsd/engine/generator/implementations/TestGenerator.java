package com.aegean.icsd.engine.generator.implementations;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.RestrictionType;

@ExtendWith(MockitoExtension.class)
@Execution(ExecutionMode.CONCURRENT)
public class TestGenerator {

  @InjectMocks
  @Spy
  private Generator svc = new Generator();
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
  public void testCalculateCardinalityMax() {
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
  public void testCalculateCardinalityMinMax() {
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
  public void testCalculateCardinalityExactly() {
    EntityRestriction res1 = new EntityRestriction();
    res1.setType(RestrictionType.EXACTLY);
    res1.setCardinality(3);

    List<EntityRestriction> restrictions = new ArrayList<>();
    restrictions.add(res1);

    int cardinality = svc.calculateCardinality(restrictions);

    Assertions.assertEquals(res1.getCardinality(), cardinality);
  }

}
