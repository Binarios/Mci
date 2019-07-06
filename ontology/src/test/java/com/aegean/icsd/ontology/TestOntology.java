package com.aegean.icsd.ontology;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.jena.ontology.EnumeratedClass;
import org.apache.jena.ontology.HasValueRestriction;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.ontology.Restriction;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.OWL2;
import org.apache.jena.vocabulary.RDF;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aegean.icsd.ontology.beans.Cardinality;
import com.aegean.icsd.ontology.beans.DataRangeRestrinction;
import com.aegean.icsd.ontology.beans.DatasetProperties;
import com.aegean.icsd.ontology.beans.Individual;
import com.aegean.icsd.ontology.beans.IndividualProperty;
import com.aegean.icsd.ontology.beans.IndividualRestriction;
import com.aegean.icsd.ontology.beans.OntologyException;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;


@ExtendWith(MockitoExtension.class)
@Execution(ExecutionMode.CONCURRENT)
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
    given(ontClassMock.isEnumeratedClass()).willReturn(false);
    given(ontClassMock.getLocalName()).willReturn(rangeMockName);

    IndividualProperty prop = ont.generateProperty(objectPropertyMock);

    Assertions.assertNotNull(prop);
    Assertions.assertEquals(objectPropertyMockName, prop.getName());
    Assertions.assertEquals("ObjectProperty", prop.getType());
    Assertions.assertEquals(rangeMockName, prop.getRange());
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
    given(ontClassMock.isEnumeratedClass()).willReturn(false);
    given(ontClassMock.getLocalName()).willReturn(rangeMockName);

    IndividualProperty prop = ont.generateProperty(objectPropertyMock);

    Assertions.assertNotNull(prop);
    Assertions.assertEquals(propertyMockName, prop.getName());
    Assertions.assertEquals("DataTypeProperty", prop.getType());
    Assertions.assertEquals(rangeMockName, prop.getRange());
  }
  @Test
  public void testGenerateEnumeratedDataTypeProperty() {
    OntProperty objectPropertyMock = mock(OntProperty.class);
    OntResource resourceMock = mock(OntResource.class);
    OntClass ontClassMock = mock(OntClass.class);
    EnumeratedClass enumMock = mock(EnumeratedClass.class);
    RDFList rdfListMock = mock(RDFList.class);
    List<RDFNode> listMock = mock(List.class);
    ListIterator<RDFNode> itMock = mock(ListIterator.class);
    RDFNode nodeMock = mock(RDFNode.class);
    Literal literalMock = mock(Literal.class);

    String propertyMockName = "testPropName";
    String rangeMockName = "testEnum";

    given(objectPropertyMock.getLocalName()).willReturn(propertyMockName);
    given(objectPropertyMock.isObjectProperty()).willReturn(false);
    given(objectPropertyMock.getRange()).willReturn(resourceMock);
    given(resourceMock.asClass()).willReturn(ontClassMock);
    given(ontClassMock.isEnumeratedClass()).willReturn(true);
    given(ontClassMock.asEnumeratedClass()).willReturn(enumMock);
    given(enumMock.getOneOf()).willReturn(rdfListMock);
    given(rdfListMock.asJavaList()).willReturn(listMock);
    given(listMock.listIterator()).willReturn(itMock);
    given(itMock.hasNext()).willReturn(true, false, false);
    given(itMock.next()).willReturn(nodeMock);
    given(nodeMock.asLiteral()).willReturn(literalMock);
    given(literalMock.getString()).willReturn(rangeMockName);

    IndividualProperty prop = ont.generateProperty(objectPropertyMock);

    Assertions.assertNotNull(prop);
    Assertions.assertEquals(propertyMockName, prop.getName());
    Assertions.assertEquals("DataTypeProperty", prop.getType());
    Assertions.assertEquals(rangeMockName, prop.getRange());
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

    List<DataRangeRestrinction> res = ont.generateDataRangeRestrictions(ontClassMock);

    Assertions.assertNotNull(res);
    Assertions.assertEquals(1,res.size() );
    Assertions.assertNotNull(res.get(0));
    Assertions.assertEquals(predicate, res.get(0).getPredicate());
    Assertions.assertEquals(value, res.get(0).getValue());
  }

  @Test
  public void testGenerateAllValuesRestrictions() throws OntologyException {
    Restriction resMock = mock(Restriction.class);
    OntProperty onPropMock = mock(OntProperty.class);

    given(resMock.getOnProperty()).willReturn(onPropMock);
    given(resMock.isAllValuesFromRestriction()).willReturn(true);

    Mockito.doReturn(new IndividualProperty()).when(ont).generateProperty(onPropMock);

    IndividualRestriction res = ont.generateRestriction(resMock);
    Assertions.assertNotNull(res);
    Assertions.assertEquals("only", res.getType());
  }

  @Test
  public void testGenerateHasValuesRestrictions() throws OntologyException {
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

    Mockito.doReturn(new IndividualProperty()).when(ont).generateProperty(onPropMock);

    IndividualRestriction res = ont.generateRestriction(resMock);
    Assertions.assertNotNull(res);
    Assertions.assertEquals("value", res.getType());
    Assertions.assertEquals(value, res.getExactValue());
  }

  @Test
  public void testGenerateSomeValuesRestrictions() throws OntologyException {
    Restriction resMock = mock(Restriction.class);
    OntProperty onPropMock = mock(OntProperty.class);

    given(resMock.getOnProperty()).willReturn(onPropMock);
    given(resMock.isSomeValuesFromRestriction()).willReturn(true);

    Mockito.doReturn(new IndividualProperty()).when(ont).generateProperty(onPropMock);

    IndividualRestriction res = ont.generateRestriction(resMock);
    Assertions.assertNotNull(res);
    Assertions.assertEquals("some", res.getType());
  }

  @Test
  public void testGenerateCardinalityRestrictions() throws OntologyException {
    Restriction resMock = mock(Restriction.class);
    OntProperty onPropMock = mock(OntProperty.class);

    given(resMock.getOnProperty()).willReturn(onPropMock);
    given(resMock.isHasValueRestriction()).willReturn(false);
    given(resMock.isSomeValuesFromRestriction()).willReturn(false);
    given(resMock.isAllValuesFromRestriction()).willReturn(false);

    Mockito.doReturn(new IndividualProperty()).when(ont).generateProperty(onPropMock);
    Mockito.doReturn(new Cardinality()).when(ont).generateOwl2Cardinality(resMock);

    IndividualRestriction res = ont.generateRestriction(resMock);
    Assertions.assertNotNull(res);
    Assertions.assertEquals("cardinality", res.getType());
  }

  @Test
  public void testGenerateMaxCardinality() throws OntologyException {
    String value = "5";
    Restriction resMock = mock(Restriction.class);
    RDFNode nodeMock = mock(RDFNode.class);
    Literal literalMock = mock(Literal.class);

    given(resMock.getPropertyValue(OWL2.qualifiedCardinality)).willReturn(null);
    given(resMock.getPropertyValue(OWL2.maxQualifiedCardinality)).willReturn(nodeMock);
    given(resMock.getPropertyValue(OWL2.minQualifiedCardinality)).willReturn(null);
    given(nodeMock.asLiteral()).willReturn(literalMock);
    given(literalMock.getString()).willReturn(value);

    Mockito.doReturn(new ArrayList<>()).when(ont).generateDataRangeRestrictions(resMock);

    Cardinality res = ont.generateOwl2Cardinality(resMock);
    Assertions.assertNotNull(res);
    Assertions.assertEquals("max", res.getType());
    Assertions.assertEquals(value, res.getOccurrence());
  }

  @Test
  public void testGenerateMinCardinality() throws OntologyException {
    String value = "5";
    Restriction resMock = mock(Restriction.class);
    RDFNode nodeMock = mock(RDFNode.class);
    Literal literalMock = mock(Literal.class);

    given(resMock.getPropertyValue(OWL2.qualifiedCardinality)).willReturn(null);
    given(resMock.getPropertyValue(OWL2.maxQualifiedCardinality)).willReturn(null);
    given(resMock.getPropertyValue(OWL2.minQualifiedCardinality)).willReturn(nodeMock);
    given(nodeMock.asLiteral()).willReturn(literalMock);
    given(literalMock.getString()).willReturn(value);

    Mockito.doReturn(new ArrayList<>()).when(ont).generateDataRangeRestrictions(resMock);

    Cardinality res = ont.generateOwl2Cardinality(resMock);
    Assertions.assertNotNull(res);
    Assertions.assertEquals("min", res.getType());
    Assertions.assertEquals(value, res.getOccurrence());
  }

  @Test
  public void testGenerateExactlyCardinality() throws OntologyException {
    String value = "5";
    Restriction resMock = mock(Restriction.class);
    RDFNode nodeMock = mock(RDFNode.class);
    Literal literalMock = mock(Literal.class);

    given(resMock.getPropertyValue(OWL2.qualifiedCardinality)).willReturn(nodeMock);
    given(resMock.getPropertyValue(OWL2.maxQualifiedCardinality)).willReturn(null);
    given(resMock.getPropertyValue(OWL2.minQualifiedCardinality)).willReturn(null);
    given(nodeMock.asLiteral()).willReturn(literalMock);
    given(literalMock.getString()).willReturn(value);

    Mockito.doReturn(new ArrayList<>()).when(ont).generateDataRangeRestrictions(resMock);

    Cardinality res = ont.generateOwl2Cardinality(resMock);
    Assertions.assertNotNull(res);
    Assertions.assertEquals("exactly", res.getType());
    Assertions.assertEquals(value, res.getOccurrence());
  }

  @Test
  public void testGenerateRestrictLessIndividual() throws OntologyException {
    String ontClassMockName = "Sound";
    String propertyMockName = "hasAssetPath";

    OntClass ontClassMock = mock(OntClass.class);

    List<IndividualProperty> props = new ArrayList<>();
    IndividualProperty prop = generateDataProperty(propertyMockName, "asdf");
    props.add(prop);

    Mockito.doReturn(ontClassMock).when(ont).getOntClass(ontClassMockName);
    Mockito.doReturn(props).when(ont).generateDeclaredProperties(ontClassMock);
    Mockito.doReturn(new ArrayList<>()).when(ont).generateRestrictions(ontClassMock);
    Mockito.doReturn(new ArrayList<>()).when(ont).generateEqualityRestrictions(ontClassMock);

    Individual result = ont.generateIndividual(ontClassMockName);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(ontClassMockName, result.getClassName());
    List<IndividualProperty> propResults = result.getProperties();
    Assertions.assertEquals(1, propResults.size());
    Assertions.assertEquals(propertyMockName, propResults.get(0).getName());

  }

  @Test
  public void testGenerateRestrictFullIndividual() throws OntologyException {
    String ontClassMockName = "Sound";
    String propertyMockName = "hasAssetPath";

    OntClass ontClassMock = mock(OntClass.class);

    IndividualProperty prop = generateDataProperty(propertyMockName, "asdf");
    List<IndividualProperty> props = new ArrayList<>();
    props.add(prop);

    IndividualProperty objProp = generateObjectProperty("restriction", "className");
    IndividualRestriction res = new IndividualRestriction();
    res.setOnIndividualProperty(objProp);
    List<IndividualRestriction> restrictions = new ArrayList<>();
    restrictions.add(res);

    Mockito.doReturn(ontClassMock).when(ont).getOntClass(ontClassMockName);
    Mockito.doReturn(props).when(ont).generateDeclaredProperties(ontClassMock);
    Mockito.doReturn(restrictions).when(ont).generateRestrictions(ontClassMock);
    Mockito.doReturn(new ArrayList<>()).when(ont).generateEqualityRestrictions(ontClassMock);

    Individual result = ont.generateIndividual(ontClassMockName);
    Assertions.assertNotNull(result);
    Assertions.assertEquals(ontClassMockName, result.getClassName());
    List<IndividualProperty> propsRes = result.getProperties();
    Assertions.assertEquals(1, propsRes.size());
    Assertions.assertEquals(propertyMockName, propsRes.get(0).getName());
  }

  @Test
  public void testNonRecursiveGenerateEqualityRestriction() throws OntologyException {
    List<IndividualRestriction> result = new ArrayList<>();

    IndividualRestriction test = new IndividualRestriction();
    test.setType("some");

    Resource intersectionOf = mock(Resource.class);
    Resource first = mock(Resource.class);
    OntClass firstClass = mock(OntClass.class);
    Restriction res = mock(Restriction.class);

    given(intersectionOf.getPropertyResourceValue(eq(RDF.first))).willReturn(first);
    given(first.canAs(OntClass.class)).willReturn(true);
    given(first.as(OntClass.class)).willReturn(firstClass);
    given(firstClass.isRestriction()).willReturn(true);
    given(firstClass.asRestriction()).willReturn(res);
    given(intersectionOf.getPropertyResourceValue(eq(RDF.rest))).willReturn(null);

    Mockito.doReturn(test).when(ont).generateRestriction(res);

    ont.generateEqualityRestriction(intersectionOf, result);

    Assertions.assertEquals(1, result.size());
    Assertions.assertEquals(test.getType(), result.get(0).getType());
  }

  @Test
  public void testRecursiveGenerateEqualityRestriction() throws OntologyException {
    List<IndividualRestriction> result = new ArrayList<>();

    IndividualRestriction test = new IndividualRestriction();
    test.setType("some");

    Resource intersectionOf = mock(Resource.class);
    Resource first = mock(Resource.class);
    OntClass firstClass = mock(OntClass.class);
    Resource second = mock(Resource.class);
    OntClass secondClass = mock(OntClass.class);
    Restriction res = mock(Restriction.class);

    given(intersectionOf.getPropertyResourceValue(eq(RDF.first))).willReturn(first);
    given(first.canAs(OntClass.class)).willReturn(true);
    given(first.as(OntClass.class)).willReturn(firstClass);
    given(firstClass.isRestriction()).willReturn(true);
    given(firstClass.asRestriction()).willReturn(res);
    given(intersectionOf.getPropertyResourceValue(eq(RDF.rest))).willReturn(second);
    given(second.getPropertyResourceValue(eq(RDF.first))).willReturn(second);
    given(second.canAs(OntClass.class)).willReturn(true);
    given(second.as(OntClass.class)).willReturn(secondClass);
    given(secondClass.isRestriction()).willReturn(true);
    given(secondClass.asRestriction()).willReturn(res);

    Mockito.doReturn(test).when(ont).generateRestriction(res);

    ont.generateEqualityRestriction(intersectionOf, result);

    Assertions.assertEquals(2, result.size());
    Assertions.assertEquals(test.getType(), result.get(0).getType());
  }


  @Test
  @Disabled("Exploring the Jena API")
  public void test() throws OntologyException {
    ont.setupModel();
    String className = "EasyFindTheSound";

    ont.generateIndividual(className);

  }

  private IndividualProperty generateDataProperty (String name, String className) {
    IndividualProperty obj = new IndividualProperty();
    obj.setName(name);
    obj.setType("DataTypeProperty");
    obj.setRange(className);
    return obj;
  }

  private IndividualProperty generateObjectProperty (String name, String className) {
    IndividualProperty obj = new IndividualProperty();
    obj.setName(name);
    obj.setType("ObjectProperty");
    obj.setRange(className);
    return obj;
  }
}

