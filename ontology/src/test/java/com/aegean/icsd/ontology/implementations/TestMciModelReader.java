package com.aegean.icsd.ontology.implementations;

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
import org.apache.jena.rdf.model.StmtIterator;
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

import com.aegean.icsd.ontology.beans.CardinalitySchema;
import com.aegean.icsd.ontology.beans.ClassSchema;
import com.aegean.icsd.ontology.beans.DataRangeRestrinctionSchema;
import com.aegean.icsd.ontology.beans.DatasetProperties;
import com.aegean.icsd.ontology.beans.OntologyException;
import com.aegean.icsd.ontology.beans.PropertySchema;
import com.aegean.icsd.ontology.beans.RestrictionSchema;
import com.aegean.icsd.ontology.queries.beans.InsertParam;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@Execution(ExecutionMode.CONCURRENT)
public class TestMciModelReader {

  @InjectMocks
  @Spy
  private MciModelReader model = new MciModelReader();

  @Mock(lenient = true)
  private DatasetProperties ds;

  @Mock
  private OntProperty ontPropertyMock;

  @Mock
  private OntResource resourceMock;

  @Mock
  private OntClass ontClassMock;

  @Mock
  private Literal literalMock;

  @Mock
  private List listMock;

  @Mock
  private ListIterator itMock;

  @Mock
  private  Restriction resMock;

  @Mock
  private RDFNode nodeMock;

  @BeforeEach
  public void setup() {
    given(ds.getOntologyName()).willReturn("games");
    given(ds.getDatasetLocation()).willReturn("../../dataset");
    given(ds.getOntologyLocation()).willReturn("../../MciOntology/games.owl");
    given(ds.getOntologyType()).willReturn("ttl");
    given(ds.getNamespace()).willReturn("http://www.semanticweb.org/iigou/diplomatiki/ontologies/Games#");
    given(ds.getPrefix()).willReturn("mci");
  }

  @Test
  public void testGenerateObjectProperty() {
    String objectPropertyMockName = "testPropName";
    String rangeMockName = "Sound";

    given(ontPropertyMock.getLocalName()).willReturn(objectPropertyMockName);
    given(ontPropertyMock.isObjectProperty()).willReturn(true);
    given(ontPropertyMock.getRange()).willReturn(resourceMock);
    given(resourceMock.asClass()).willReturn(ontClassMock);
    given(ontClassMock.isEnumeratedClass()).willReturn(false);
    given(ontClassMock.getLocalName()).willReturn(rangeMockName);

    PropertySchema prop = model.getPropertySchema(ontPropertyMock);

    Assertions.assertNotNull(prop);
    Assertions.assertEquals(objectPropertyMockName, prop.getName());
    Assertions.assertTrue(prop.isObjectProperty());
    Assertions.assertEquals(rangeMockName, prop.getRange());
  }

  @Test
  public void testGenerateDataTypeProperty() {
    String propertyMockName = "testPropName";
    String rangeMockName = "xsd:integer";

    given(ontPropertyMock.getLocalName()).willReturn(propertyMockName);
    given(ontPropertyMock.isObjectProperty()).willReturn(false);
    given(ontPropertyMock.getRange()).willReturn(resourceMock);
    given(resourceMock.asClass()).willReturn(ontClassMock);
    given(ontClassMock.isEnumeratedClass()).willReturn(false);
    given(ontClassMock.getLocalName()).willReturn(rangeMockName);

    PropertySchema prop = model.getPropertySchema(ontPropertyMock);

    Assertions.assertNotNull(prop);
    Assertions.assertEquals(propertyMockName, prop.getName());
    Assertions.assertFalse(prop.isObjectProperty());
    Assertions.assertEquals(rangeMockName, prop.getRange());
  }

  @Test
  public void testGenerateEnumeratedDataTypeProperty() {
    EnumeratedClass enumMock = mock(EnumeratedClass.class);
    RDFList rdfListMock = mock(RDFList.class);

    String propertyMockName = "testPropName";
    String rangeMockName = "testEnum";

    given(ontPropertyMock.getLocalName()).willReturn(propertyMockName);
    given(ontPropertyMock.isObjectProperty()).willReturn(false);
    given(ontPropertyMock.getRange()).willReturn(resourceMock);
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

    PropertySchema prop = model.getPropertySchema(ontPropertyMock);

    Assertions.assertNotNull(prop);
    Assertions.assertEquals(propertyMockName, prop.getName());
    Assertions.assertFalse(prop.isObjectProperty());
    Assertions.assertEquals(rangeMockName, prop.getRange());
  }

  @Test
  public void testGenerateDataRangeRestrictions() {
    String predicate = "maxExclusive";
    String value = "1800";
    String type = "string";

    Property predicateMock = mock(Property.class);
    Statement statementMock = mock(Statement.class);
    StmtIterator itMock = mock(StmtIterator.class);

    given(ontClassMock.getPropertyResourceValue(eq(OWL2.onDataRange))).willReturn(resourceMock);
    given(resourceMock.getPropertyResourceValue(eq(OWL2.withRestrictions))).willReturn(resourceMock);
    given(resourceMock.getPropertyResourceValue(eq(RDF.first))).willReturn(resourceMock);
    given(resourceMock.listProperties()).willReturn(itMock);
    given(itMock.hasNext()).willReturn(true, false);
    given(itMock.nextStatement()).willReturn(statementMock);
    given(statementMock.getPredicate()).willReturn(predicateMock);
    given(predicateMock.getLocalName()).willReturn(predicate);
    given(statementMock.getLiteral()).willReturn(literalMock);
    given(literalMock.getString()).willReturn(value);
    given(literalMock.getDatatypeURI()).willReturn(type);

    List<DataRangeRestrinctionSchema> res = model.generateDataRangeRestrictions(ontClassMock);

    Assertions.assertNotNull(res);
    Assertions.assertEquals(1,res.size() );
    Assertions.assertNotNull(res.get(0));
    Assertions.assertEquals(predicate, res.get(0).getPredicate());
    Assertions.assertEquals(value, res.get(0).getValue());
  }

  @Test
  public void testGenerateAllValuesRestrictions() throws OntologyException {
    given(resMock.getOnProperty()).willReturn(ontPropertyMock);
    given(resMock.isAllValuesFromRestriction()).willReturn(true);

    Mockito.doReturn(new PropertySchema()).when(model).getPropertySchema(ontPropertyMock);

    RestrictionSchema res = model.getRestrictionSchema(resMock);
    Assertions.assertNotNull(res);
    Assertions.assertEquals("only", res.getType());
  }

  @Test
  public void testGenerateHasValuesRestrictions() throws OntologyException {
    String value = "5";

    HasValueRestriction hasValueMock = mock(HasValueRestriction.class);

    given(resMock.getOnProperty()).willReturn(ontPropertyMock);
    given(resMock.isHasValueRestriction()).willReturn(true);
    given(resMock.asHasValueRestriction()).willReturn(hasValueMock);
    given(hasValueMock.getHasValue()).willReturn(nodeMock);
    given(nodeMock.asLiteral()).willReturn(literalMock);
    given(literalMock.getString()).willReturn(value);

    Mockito.doReturn(new PropertySchema()).when(model).getPropertySchema(ontPropertyMock);

    RestrictionSchema res = model.getRestrictionSchema(resMock);
    Assertions.assertNotNull(res);
    Assertions.assertEquals("value", res.getType());
    Assertions.assertEquals(value, res.getExactValue());
  }

  @Test
  public void testGenerateSomeValuesRestrictions() throws OntologyException {
    given(resMock.getOnProperty()).willReturn(ontPropertyMock);
    given(resMock.isSomeValuesFromRestriction()).willReturn(true);

    Mockito.doReturn(new PropertySchema()).when(model).getPropertySchema(ontPropertyMock);

    RestrictionSchema res = model.getRestrictionSchema(resMock);
    Assertions.assertNotNull(res);
    Assertions.assertEquals("some", res.getType());
  }

  @Test
  public void testGenerateCardinalityRestrictions() throws OntologyException {
    given(resMock.getOnProperty()).willReturn(ontPropertyMock);
    given(resMock.isHasValueRestriction()).willReturn(false);
    given(resMock.isSomeValuesFromRestriction()).willReturn(false);
    given(resMock.isAllValuesFromRestriction()).willReturn(false);

    Mockito.doReturn(new PropertySchema()).when(model).getPropertySchema(ontPropertyMock);
    Mockito.doReturn(new CardinalitySchema()).when(model).getOwl2CardinalitySchema(resMock);
    Mockito.doReturn(RestrictionSchema.EXACTLY_TYPE).when(model).getOwl2RestrictionType(resMock);

    RestrictionSchema res = model.getRestrictionSchema(resMock);
    Assertions.assertNotNull(res);
    Assertions.assertEquals(RestrictionSchema.EXACTLY_TYPE, res.getType());
  }

  @Test
  public void testGenerateMaxCardinality() throws OntologyException {
    String value = "5";

    given(resMock.getPropertyValue(OWL2.qualifiedCardinality)).willReturn(null);
    given(resMock.getPropertyValue(OWL2.maxQualifiedCardinality)).willReturn(nodeMock);
    given(resMock.getPropertyValue(OWL2.minQualifiedCardinality)).willReturn(null);
    given(nodeMock.asLiteral()).willReturn(literalMock);
    given(literalMock.getString()).willReturn(value);

    Mockito.doReturn(new ArrayList<>()).when(model).generateDataRangeRestrictions(resMock);

    CardinalitySchema res = model.getOwl2CardinalitySchema(resMock);
    Assertions.assertNotNull(res);
    Assertions.assertEquals(value, res.getOccurrence());
  }

  @Test
  public void testGenerateMinCardinality() throws OntologyException {
    String value = "5";

    given(resMock.getPropertyValue(OWL2.qualifiedCardinality)).willReturn(null);
    given(resMock.getPropertyValue(OWL2.maxQualifiedCardinality)).willReturn(null);
    given(resMock.getPropertyValue(OWL2.minQualifiedCardinality)).willReturn(nodeMock);
    given(nodeMock.asLiteral()).willReturn(literalMock);
    given(literalMock.getString()).willReturn(value);

    Mockito.doReturn(new ArrayList<>()).when(model).generateDataRangeRestrictions(resMock);

    CardinalitySchema res = model.getOwl2CardinalitySchema(resMock);
    Assertions.assertNotNull(res);
    Assertions.assertEquals(value, res.getOccurrence());
  }

  @Test
  public void testGenerateExactlyCardinality() throws OntologyException {
    String value = "5";
    given(resMock.getPropertyValue(OWL2.qualifiedCardinality)).willReturn(nodeMock);
    given(resMock.getPropertyValue(OWL2.maxQualifiedCardinality)).willReturn(null);
    given(resMock.getPropertyValue(OWL2.minQualifiedCardinality)).willReturn(null);
    given(nodeMock.asLiteral()).willReturn(literalMock);
    given(literalMock.getString()).willReturn(value);

    Mockito.doReturn(new ArrayList<>()).when(model).generateDataRangeRestrictions(resMock);

    CardinalitySchema res = model.getOwl2CardinalitySchema(resMock);
    Assertions.assertNotNull(res);
    Assertions.assertEquals(value, res.getOccurrence());
  }

  @Test
  public void testGenerateRestrictLessIndividual() throws OntologyException {
    String ontClassMockName = "Sound";
    String propertyMockName = "hasAssetPath";

    List<PropertySchema> props = new ArrayList<>();
    PropertySchema prop = generateDataProperty(propertyMockName, "asdf");
    props.add(prop);

    Mockito.doReturn(ontClassMock).when(model).getOntClass(ontClassMockName);
    Mockito.doReturn(props).when(model).getDeclaredPropertiesSchemas(ontClassMock);
    Mockito.doReturn(new ArrayList<>()).when(model).getRestrictionSchemas(ontClassMock);
    Mockito.doReturn(new ArrayList<>()).when(model).getEqualityRestrictionSchemas(ontClassMock);

    ClassSchema result = model.getClassSchema(ontClassMockName);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(ontClassMockName, result.getClassName());
    List<PropertySchema> propResults = result.getProperties();
    Assertions.assertEquals(1, propResults.size());
    Assertions.assertEquals(propertyMockName, propResults.get(0).getName());

  }

  @Test
  public void testGenerateRestrictFullIndividual() throws OntologyException {
    String ontClassMockName = "Sound";
    String propertyMockName = "hasAssetPath";

    PropertySchema prop = generateDataProperty(propertyMockName, "asdf");
    List<PropertySchema> props = new ArrayList<>();
    props.add(prop);

    PropertySchema objProp = generateObjectProperty("restriction", "className");
    RestrictionSchema res = new RestrictionSchema();
    res.setOnPropertySchema(objProp);
    List<RestrictionSchema> restrictions = new ArrayList<>();
    restrictions.add(res);

    Mockito.doReturn(ontClassMock).when(model).getOntClass(ontClassMockName);
    Mockito.doReturn(props).when(model).getDeclaredPropertiesSchemas(ontClassMock);
    Mockito.doReturn(restrictions).when(model).getRestrictionSchemas(ontClassMock);
    Mockito.doReturn(new ArrayList<>()).when(model).getEqualityRestrictionSchemas(ontClassMock);

    ClassSchema result = model.getClassSchema(ontClassMockName);
    Assertions.assertNotNull(result);
    Assertions.assertEquals(ontClassMockName, result.getClassName());
    List<PropertySchema> propsRes = result.getProperties();
    Assertions.assertEquals(1, propsRes.size());
    Assertions.assertEquals(propertyMockName, propsRes.get(0).getName());
  }

  @Test
  public void testNonRecursiveGenerateEqualityRestriction() throws OntologyException {
    List<RestrictionSchema> result = new ArrayList<>();

    RestrictionSchema test = new RestrictionSchema();
    test.setType("some");

    Resource intersectionOf = mock(Resource.class);
    Resource first = mock(Resource.class);

    given(intersectionOf.getPropertyResourceValue(eq(RDF.first))).willReturn(first);
    given(first.canAs(OntClass.class)).willReturn(true);
    given(first.as(OntClass.class)).willReturn(ontClassMock);
    given(ontClassMock.isRestriction()).willReturn(true);
    given(ontClassMock.asRestriction()).willReturn(resMock);
    given(intersectionOf.getPropertyResourceValue(eq(RDF.rest))).willReturn(null);

    Mockito.doReturn(test).when(model).getRestrictionSchema(resMock);

    model.getEqualityRestrictionSchema(intersectionOf, result);

    Assertions.assertEquals(1, result.size());
    Assertions.assertEquals(test.getType(), result.get(0).getType());
  }

  @Test
  public void testRecursiveGenerateEqualityRestriction() throws OntologyException {
    List<RestrictionSchema> result = new ArrayList<>();

    RestrictionSchema test = new RestrictionSchema();
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

    Mockito.doReturn(test).when(model).getRestrictionSchema(res);

    model.getEqualityRestrictionSchema(intersectionOf, result);

    Assertions.assertEquals(2, result.size());
    Assertions.assertEquals(test.getType(), result.get(0).getType());
  }

  @Test
  @Disabled("Exploring the Jena API")
  public void test() throws OntologyException {
    model.setupModel();
    String className = "Number";
    model.getClassSchema(className);
  }

  private PropertySchema generateDataProperty (String name, String className) {
    PropertySchema obj = new PropertySchema();
    obj.setName(name);
    obj.setObjectProperty(false);
    obj.setRange(className);
    return obj;
  }

  private PropertySchema generateObjectProperty (String name, String className) {
    PropertySchema obj = new PropertySchema();
    obj.setName(name);
    obj.setObjectProperty(true);
    obj.setRange(className);
    return obj;
  }

  private InsertParam generateInsertParam(String name, String value, boolean isIri) {
    InsertParam param = new InsertParam();
    param.setIriParam(isIri);
    param.setName(name);
    param.setValue(value);
    return param;
  }
}

