package com.aegean.icsd.ontology.beans;

public class RestrictionSchema {
  public static String EXACTLY_TYPE = "exactly";
  public static String MIN_TYPE = "min";
  public static String MAX_TYPE = "max";
  public static String ONLY_TYPE = "only";
  public static String SOME_TYPE = "some";
  public static String VALUE_TYPE = "value";

  /**
   * The property that this restriction is associated with
   */
  private PropertySchema onPropertySchema;

  /**
   * The type of the association. For example is it a given value, is it min/max
   */
  private String type;

  /**
   * if the type is exact, then this is the value
   */
  private String exactValue;

  /**
   * if the type has a cardinalitySchema (min, max, exact) then it is described here
   */
  private CardinalitySchema cardinalitySchema;

  public PropertySchema getOnPropertySchema() {
    return onPropertySchema;
  }

  public void setOnPropertySchema(PropertySchema onPropertySchema) {
    this.onPropertySchema = onPropertySchema;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public CardinalitySchema getCardinalitySchema() {
    return cardinalitySchema;
  }

  public void setCardinalitySchema(CardinalitySchema cardinalitySchema) {
    this.cardinalitySchema = cardinalitySchema;
  }

  public String getExactValue() {
    return exactValue;
  }

  public void setExactValue(String exactValue) {
    this.exactValue = exactValue;
  }
}
