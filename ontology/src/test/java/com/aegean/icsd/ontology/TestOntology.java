package com.aegean.icsd.ontology;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.ontology.AllValuesFromRestriction;
import org.apache.jena.ontology.HasValueRestriction;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.ontology.Restriction;
import org.apache.jena.ontology.SomeValuesFromRestriction;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.OWL2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aegean.icsd.ontology.beans.DatasetProperties;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;


@ExtendWith(MockitoExtension.class)
public class TestOntology {

  @InjectMocks
  @Spy
  private Ontology ont = new Ontology();

  @Mock(lenient = true)
  private DatasetProperties ds;


  @BeforeEach
  public void setup() {
    given(ds.getOntologyName()).willReturn("games");
    given(ds.getDatasetLocation()).willReturn("../../dataset");
    given(ds.getOntologyLocation()).willReturn("../../MciOntology/games.owl");
    given(ds.getOntologyType()).willReturn("ttl");
    given(ds.getNamespace()).willReturn("http://www.semanticweb.org/iigou/diplomatiki/ontologies/Games#");
  }

  @Test
  public void testSetupDataset() throws OntologyException {
    ont.setupDataset();
    Assertions.assertTrue(TDBFactory.inUseLocation("../../dataset"));
  }

  @Test
  public void testGetOntClass() throws OntologyException {
    String ontClassName = "Questions";
    OntClass ontClass = ont.getOntClass(ontClassName);
    Assertions.assertNotNull(ontClass);
    Assertions.assertEquals(ontClassName, ontClass.getLocalName());
  }

  @Test
  public void testGenerateObjectProperty() {
    OntProperty objectPropertyMock = mock(OntProperty.class);
    OntResource resourceMock = mock(OntResource.class);
    OntClass ontClassMock = mock(OntClass.class);

    String objectPropertyMockName = "testPropName";
    String rangeMockName = "Sound";

    given(objectPropertyMock.getLocalName()).willReturn(objectPropertyMockName);
    given(objectPropertyMock.isObjectProperty()).willReturn(true);
    given(objectPropertyMock.getRange()).willReturn(resourceMock);
    given(resourceMock.asClass()).willReturn(ontClassMock);
    given(ontClassMock.getLocalName()).willReturn(rangeMockName);

    JsonObject prop = ont.generateProperty(objectPropertyMock);

    Assertions.assertNotNull(prop);
    Assertions.assertFalse(prop.isJsonNull());
    Assertions.assertEquals(objectPropertyMockName, prop.get("name").getAsString());
    Assertions.assertEquals("ObjectProperty", prop.get("type").getAsString());
    Assertions.assertEquals(rangeMockName, prop.get("range").getAsString());
  }

  @Test
  public void testGenerateDataTypeProperty() {
    OntProperty objectPropertyMock = mock(OntProperty.class);
    OntResource resourceMock = mock(OntResource.class);
    OntClass ontClassMock = mock(OntClass.class);

    String propertyMockName = "testPropName";
    String rangeMockName = "xsd:integer";

    given(objectPropertyMock.getLocalName()).willReturn(propertyMockName);
    given(objectPropertyMock.isObjectProperty()).willReturn(false);
    given(objectPropertyMock.getRange()).willReturn(resourceMock);
    given(resourceMock.asClass()).willReturn(ontClassMock);
    given(ontClassMock.getLocalName()).willReturn(rangeMockName);

    JsonObject prop = ont.generateProperty(objectPropertyMock);

    Assertions.assertNotNull(prop);
    Assertions.assertFalse(prop.isJsonNull());
    Assertions.assertEquals(propertyMockName, prop.get("name").getAsString());
    Assertions.assertEquals("DataTypeProperty", prop.get("type").getAsString());
    Assertions.assertEquals(rangeMockName, prop.get("range").getAsString());
  }

  @Test
  public void testGenerateDataRangeRestrictions() {

    String predicate = "maxExclusive";
    String value = "1800";
    String type = "string";

    OntClass ontClassMock = mock(OntClass.class);
    Property predicateMock = mock(Property.class);
    Literal objectMock = mock(Literal.class);
    Statement statementMock = mock(Statement.class);
    List<Statement> statementsMock = new ArrayList<>();
    statementsMock.add(statementMock);

    given(statementMock.getPredicate()).willReturn(predicateMock);
    given(predicateMock.getLocalName()).willReturn(predicate);
    given(statementMock.getLiteral()).willReturn(objectMock);
    given(objectMock.getString()).willReturn(value);
    given(objectMock.getDatatypeURI()).willReturn(type);

    Mockito.doReturn(statementsMock).when(ont).readDataRangeRestrictions(ontClassMock);

    JsonArray res = ont.generateDataRangeRestrictions(ontClassMock);

    Assertions.assertNotNull(res);
    Assertions.assertEquals(1,res.size() );
    Assertions.assertNotNull(res.get(0));
    Assertions.assertEquals(predicate, res.get(0).getAsJsonObject().get("predicate").getAsString());
    Assertions.assertEquals(value, res.get(0).getAsJsonObject().get("value").getAsString());
  }

  @Test
  public void testGenerateAllValuesRestrictions() {
    Restriction resMock = mock(Restriction.class);
    OntProperty onPropMock = mock(OntProperty.class);

    given(resMock.getOnProperty()).willReturn(onPropMock);
    given(resMock.isAllValuesFromRestriction()).willReturn(true);

    Mockito.doReturn(new JsonObject()).when(ont).generateProperty(onPropMock);

    JsonObject res = ont.generateRestriction(resMock);
    Assertions.assertNotNull(res);
    Assertions.assertEquals("only", res.get("restrictionType").getAsString());
  }

  @Test
  public void testGenerateHasValuesRestrictions() {
    String value = "5";

    Restriction resMock = mock(Restriction.class);
    OntProperty onPropMock = mock(OntProperty.class);
    HasValueRestriction hasValueMock = mock(HasValueRestriction.class);
    RDFNode nodeMock = mock(RDFNode.class);
    Literal literalMock = mock(Literal.class);

    given(resMock.getOnProperty()).willReturn(onPropMock);
    given(resMock.isHasValueRestriction()).willReturn(true);
    given(resMock.asHasValueRestriction()).willReturn(hasValueMock);
    given(hasValueMock.getHasValue()).willReturn(nodeMock);
    given(nodeMock.asLiteral()).willReturn(literalMock);
    given(literalMock.getString()).willReturn(value);

    Mockito.doReturn(new JsonObject()).when(ont).generateProperty(onPropMock);

    JsonObject res = ont.generateRestriction(resMock);
    Assertions.assertNotNull(res);
    Assertions.assertEquals("value", res.get("restrictionType").getAsString());
    Assertions.assertEquals(value, res.get("restrictionCardinality").getAsString());
  }

  @Test
  public void testGenerateSomeValuesRestrictions() {
    Restriction resMock = mock(Restriction.class);
    OntProperty onPropMock = mock(OntProperty.class);

    given(resMock.getOnProperty()).willReturn(onPropMock);
    given(resMock.isSomeValuesFromRestriction()).willReturn(true);

    Mockito.doReturn(new JsonObject()).when(ont).generateProperty(onPropMock);

    JsonObject res = ont.generateRestriction(resMock);
    Assertions.assertNotNull(res);
    Assertions.assertEquals("some", res.get("restrictionType").getAsString());
  }

  @Test
  public void testGenerateCardinalityRestrictions() {
    Restriction resMock = mock(Restriction.class);
    OntProperty onPropMock = mock(OntProperty.class);

    given(resMock.getOnProperty()).willReturn(onPropMock);
    given(resMock.isHasValueRestriction()).willReturn(false);
    given(resMock.isSomeValuesFromRestriction()).willReturn(false);
    given(resMock.isAllValuesFromRestriction()).willReturn(false);

    Mockito.doReturn(new JsonObject()).when(ont).generateProperty(onPropMock);
    Mockito.doReturn(new JsonObject()).when(ont).generateOwl2Cardinality(resMock);

    JsonObject res = ont.generateRestriction(resMock);
    Assertions.assertNotNull(res);
    Assertions.assertEquals("cardinality", res.get("restrictionType").getAsString());
  }

  @Test
  public void testGenerateMaxCardinality() {
    String value = "5";
    Restriction resMock = mock(Restriction.class);
    RDFNode nodeMock = mock(RDFNode.class);
    Literal literalMock = mock(Literal.class);

    given(resMock.getPropertyValue(OWL2.qualifiedCardinality)).willReturn(null);
    given(resMock.getPropertyValue(OWL2.maxQualifiedCardinality)).willReturn(nodeMock);
    given(resMock.getPropertyValue(OWL2.minQualifiedCardinality)).willReturn(null);
    given(nodeMock.asLiteral()).willReturn(literalMock);
    given(literalMock.getString()).willReturn(value);

    Mockito.doReturn(new JsonArray()).when(ont).generateDataRangeRestrictions(resMock);

    JsonObject res = ont.generateOwl2Cardinality(resMock);
    Assertions.assertNotNull(res);
    Assertions.assertEquals("max", res.get("type").getAsString());
    Assertions.assertEquals(value, res.get("occurrences").getAsString());
  }

  @Test
  public void testGenerateMinCardinality() {
    String value = "5";
    Restriction resMock = mock(Restriction.class);
    RDFNode nodeMock = mock(RDFNode.class);
    Literal literalMock = mock(Literal.class);

    given(resMock.getPropertyValue(OWL2.qualifiedCardinality)).willReturn(null);
    given(resMock.getPropertyValue(OWL2.maxQualifiedCardinality)).willReturn(null);
    given(resMock.getPropertyValue(OWL2.minQualifiedCardinality)).willReturn(nodeMock);
    given(nodeMock.asLiteral()).willReturn(literalMock);
    given(literalMock.getString()).willReturn(value);

    Mockito.doReturn(new JsonArray()).when(ont).generateDataRangeRestrictions(resMock);

    JsonObject res = ont.generateOwl2Cardinality(resMock);
    Assertions.assertNotNull(res);
    Assertions.assertEquals("min", res.get("type").getAsString());
    Assertions.assertEquals(value, res.get("occurrences").getAsString());
  }

  @Test
  public void testGenerateExactlyCardinality() {
    String value = "5";
    Restriction resMock = mock(Restriction.class);
    RDFNode nodeMock = mock(RDFNode.class);
    Literal literalMock = mock(Literal.class);

    given(resMock.getPropertyValue(OWL2.qualifiedCardinality)).willReturn(nodeMock);
    given(resMock.getPropertyValue(OWL2.maxQualifiedCardinality)).willReturn(null);
    given(resMock.getPropertyValue(OWL2.minQualifiedCardinality)).willReturn(null);
    given(nodeMock.asLiteral()).willReturn(literalMock);
    given(literalMock.getString()).willReturn(value);

    Mockito.doReturn(new JsonArray()).when(ont).generateDataRangeRestrictions(resMock);

    JsonObject res = ont.generateOwl2Cardinality(resMock);
    Assertions.assertNotNull(res);
    Assertions.assertEquals("exactly", res.get("type").getAsString());
    Assertions.assertEquals(value, res.get("occurrences").getAsString());
  }

  @Test
  public void testGenerateRestrictLessIndividual() throws OntologyException {
    String ontClassMockName = "Sound";
    String propertyMockName = "hasAssetPath";

    OntClass ontClassMock = mock(OntClass.class);
    OntProperty propertyMock = mock(OntProperty.class);
    ExtendedIterator<OntProperty> propItMock = mock(ExtendedIterator.class);
    ExtendedIterator<OntClass> classItMock = mock(ExtendedIterator.class);

    given(ontClassMock.listDeclaredProperties()).willReturn(propItMock);
    given(propItMock.hasNext()).willReturn(true,false);
    given(propItMock.next()).willReturn(propertyMock);
    given(ontClassMock.listSuperClasses()).willReturn(classItMock);
    given(classItMock.hasNext()).willReturn( false);

    JsonObject prop = generateDataProperty(propertyMockName, "asdf");

    Mockito.doReturn(ontClassMock).when(ont).getOntClass(ontClassMockName);
    Mockito.doReturn(prop).when(ont).generateProperty(propertyMock);

    JsonObject result = ont.generateIndividual(ontClassMockName);
    Assertions.assertNotNull(result);
    Assertions.assertEquals(ontClassMockName, result.get("class").getAsString());
    JsonArray props = result.get("properties").getAsJsonArray();
    Assertions.assertEquals(1, props.size());
    Assertions.assertEquals(propertyMockName, props.get(0).getAsJsonObject().get("name").getAsString());
  }

  @Test
  public void testGenerateRestrictFullIndividual() throws OntologyException {
    String ontClassMockName = "Sound";
    String propertyMockName = "hasAssetPath";

    OntClass ontClassMock = mock(OntClass.class);
    OntClass superClassMock = mock(OntClass.class);
    OntProperty propertyMock = mock(OntProperty.class);
    Restriction resClassMock = mock(Restriction.class);

    ExtendedIterator<OntProperty> propItMock = mock(ExtendedIterator.class);
    ExtendedIterator<OntClass> classItMock = mock(ExtendedIterator.class);

    given(ontClassMock.listDeclaredProperties()).willReturn(propItMock);
    given(propItMock.hasNext()).willReturn(true,false);
    given(propItMock.next()).willReturn(propertyMock);

    given(ontClassMock.listSuperClasses()).willReturn(classItMock);
    given(classItMock.hasNext()).willReturn( true,false);
    given(classItMock.next()).willReturn(superClassMock);
    given(superClassMock.isRestriction()).willReturn(true);
    given(superClassMock.asRestriction()).willReturn(resClassMock);

    JsonObject prop = generateDataProperty(propertyMockName, "asdf");

    Mockito.doReturn(ontClassMock).when(ont).getOntClass(ontClassMockName);
    Mockito.doReturn(prop).when(ont).generateProperty(propertyMock);
    Mockito.doReturn(prop).when(ont).generateRestriction(resClassMock);

    JsonObject result = ont.generateIndividual(ontClassMockName);
    Assertions.assertNotNull(result);
    Assertions.assertEquals(ontClassMockName, result.get("class").getAsString());
    JsonArray props = result.get("properties").getAsJsonArray();
    Assertions.assertEquals(1, props.size());
    Assertions.assertEquals(propertyMockName, props.get(0).getAsJsonObject().get("name").getAsString());
  }

  @Test
  @Disabled("Exploring the Jena API")
  public void test() throws OntologyException {
    ont.setupDataset();
    String className = "EasyFindTheSound";

    ont.generateIndividual(className);

  }

  private JsonObject generateDataProperty (String name, String className) {
    JsonObject obj = new JsonObject();
    obj.addProperty("name", name);
    obj.addProperty("type", "DataTypeProperty");
    obj.addProperty("range", className);
    return obj;
  }

  private JsonObject generateObjectProperty (String name, String className) {
    JsonObject obj = new JsonObject();
    obj.addProperty("name", name);
    obj.addProperty("type", "ObjectProperty");
    obj.addProperty("range", className);
    return obj;
  }

  private JsonObject generateIndividual (String name, int nbOfProps) {
    JsonObject obj = new JsonObject();
    obj.addProperty("class", name);
    JsonArray properties = new JsonArray();
    for(int i = 0; i < nbOfProps; i++) {
      properties.add(generateDataProperty("prop" + i, "value" + i));
    }
    obj.add("properties", properties);
    return obj;
  }
}

