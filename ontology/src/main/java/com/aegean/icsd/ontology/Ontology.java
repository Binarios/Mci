package com.aegean.icsd.ontology;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.jena.ontology.HasValueRestriction;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.ontology.Restriction;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ModelMaker;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.reasoner.ValidityReport;
import org.apache.jena.tdb2.TDB2Factory;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.OWL2;
import org.apache.jena.vocabulary.RDF;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.ontology.beans.CardinalitySchema;
import com.aegean.icsd.ontology.beans.ClassSchema;
import com.aegean.icsd.ontology.beans.DataRangeRestrinctionSchema;
import com.aegean.icsd.ontology.beans.DatasetProperties;
import com.aegean.icsd.ontology.beans.RestrictionSchema;
import com.aegean.icsd.ontology.beans.PropertySchema;
import com.aegean.icsd.ontology.beans.OntologyException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import openllet.jena.PelletReasonerFactory;

@Service
public class Ontology implements IOntology {

  private static Logger LOGGER = Logger.getLogger(Ontology.class);

  @Autowired
  private DatasetProperties ontologyProps;

//  @Autowired
//  private ITdbConnection conProvider;

  private OntModel model;
  private Dataset ds;

  @Override
  public JsonArray executeSelect(ParameterizedSparqlString sparql, List<String> colNames) throws OntologyException {
//    Connection con = null;
//    JsonArray result = new JsonArray();
//    try {
//      con = conProvider.connect(ontologyProps.getDatasetLocation());
//      java.sql.Statement sel = con.createStatement();
//      ResultSet raw = sel.executeQuery(sparql.asQuery().toString());
//      while (raw.next()) {
//        JsonObject row = new JsonObject();
//        for(String col : colNames) {
//          row.addProperty(col, raw.getString(col));
//        }
//      }
//      raw.close();
//      return result;
//    } catch (ConnectionException | SQLException e) {
//      throw new OntologyException("ONT.EX1", "Cannot execute sparql query", e);
//    } finally {
//      try {
//        if (con != null && !con.isClosed()) {
//          con.close();
//        }
//      } catch (SQLException e) {
//        //noinspection ThrowFromFinallyBlock
//        throw new OntologyException("ONT.EX2", "Cannot close connection", e);
//      }
//    }
    return null;
  }

  @Override
  public JsonObject selectTriplet(String subject, String predicate, String object) {
    JsonObject obj = new JsonObject();
    ParameterizedSparqlString sparql = getPrefixedSparql();
    sparql.setCommandText("SELECT ?s ?p ?o WHERE {?s ?p ?o .}");
//    sparql.setIri("s", getPrefixedEntity(subject));
//    sparql.setIri("p", getPrefixedEntity(predicate));
//    sparql.setLiteral("o", object);
    ds.begin(ReadWrite.READ);
    Query selectRequest = QueryFactory.create(sparql.asQuery().toString());
    QueryExecution queryProcessor = QueryExecutionFactory.create(selectRequest, ds);
    ResultSet resultSet = queryProcessor.execSelect();
    while(resultSet.hasNext()) {
      QuerySolution solution = resultSet.next();
      Iterator<String> vars = solution.varNames();
      while (vars.hasNext()) {
       String var = vars.next();
       RDFNode node = solution.get(var);
       if(node != null && node.isResource()) {
         obj.addProperty(var,node.asResource().getLocalName());
       }
       if(node != null && node.isLiteral()) {
        obj.addProperty(var,node.asLiteral().getString());
       }
      }
    }
    ds.commit();
    ds.close();

    return obj;
  }


  @Override
  public boolean insertTriplet(String subject, String predicate, String object) {
    ParameterizedSparqlString sparql = getPrefixedSparql();
    sparql.setCommandText("INSERT { ?s ?p ?o } WHERE {}");
    sparql.setIri("s", getPrefixedEntity(subject));
    sparql.setIri("p", getPrefixedEntity(predicate));
    sparql.setLiteral("o", object);
    ds.begin(ReadWrite.WRITE);
    UpdateRequest updateRequest = UpdateFactory.create(sparql.asUpdate().toString());
    UpdateProcessor updateProcessor = UpdateExecutionFactory.create(updateRequest, ds);
    updateProcessor.execute();
    ds.commit();
    ds.close();
    return true;
  }

  @Override
  public ClassSchema getClassSchema(String className) throws OntologyException {
    ClassSchema result = new ClassSchema();
    result.setClassName(className);
    OntClass entity = getOntClass(className);

    List<PropertySchema> properties = getDeclaredPropertiesSchemas(entity);
    List<RestrictionSchema> restrictions = getRestrictionSchemas(entity);
    List<RestrictionSchema> equalityRestrictions = getEqualityRestrictionSchemas(entity);

    result.setProperties(properties);
    result.setRestrictions(restrictions);
    result.setEqualityRestrictions(equalityRestrictions);
    return result;
  }

  List<PropertySchema> getDeclaredPropertiesSchemas(OntClass ontClass) {
    List<PropertySchema> properties = new ArrayList<>();

    ExtendedIterator<OntProperty> propIt = ontClass.listDeclaredProperties();
    while (propIt.hasNext()) {
      PropertySchema propertyDesc = getPropertySchema(propIt.next());
      if(propertyDesc.getName() != null) {
        properties.add(propertyDesc);
      }
    }
    return properties;
  }

  List<RestrictionSchema> getRestrictionSchemas(OntClass ontClass) throws OntologyException {
    List<RestrictionSchema> restrictions = new ArrayList<>();
    ExtendedIterator<OntClass> superClassesIt = ontClass.listSuperClasses();
    while (superClassesIt.hasNext()) {
      OntClass superClass = superClassesIt.next();
      if (superClass.isRestriction()) {
        Restriction resClass = superClass.asRestriction();
        RestrictionSchema restriction = getRestrictionSchema(resClass);
        restrictions.add(restriction);
      }
    }
    return restrictions;
  }

  List<RestrictionSchema> getEqualityRestrictionSchemas(OntClass entity) throws OntologyException {
    List<RestrictionSchema> equalityRestrictions = new ArrayList<>();
    OntClass equivalentClass = entity.getEquivalentClass();
    Resource intersectionOf = equivalentClass.getPropertyResourceValue(OWL2.intersectionOf);
    if (intersectionOf != null) {
      getEqualityRestrictionSchema(intersectionOf, equalityRestrictions);
    }
    return equalityRestrictions;
  }

  void getEqualityRestrictionSchema(Resource intersectionOf, List<RestrictionSchema> equalityRestrictions)
          throws OntologyException {
    Resource first = intersectionOf.getPropertyResourceValue(RDF.first);
    if (first != null ) {
      if (first.canAs(OntClass.class)) {
        OntClass firstAsClass = first.as(OntClass.class);
        if (firstAsClass.isRestriction()) {
          Restriction restriction = firstAsClass.asRestriction();
          RestrictionSchema eqRestriction = getRestrictionSchema(restriction);
          equalityRestrictions.add(eqRestriction);
        }
        Resource rest = intersectionOf.getPropertyResourceValue(RDF.rest);
        if (rest != null) {
          getEqualityRestrictionSchema(rest, equalityRestrictions);
        }
      }
    }
  }

  RestrictionSchema getRestrictionSchema(Restriction restriction) throws OntologyException {
    RestrictionSchema result = new RestrictionSchema();
    OntProperty resProp = restriction.getOnProperty();
    result.setOnPropertySchema(getPropertySchema(resProp));

    if (restriction.isAllValuesFromRestriction()) {
      result.setType(RestrictionSchema.ONLY_TYPE);
    } else if (restriction.isHasValueRestriction()) {
      result.setType(RestrictionSchema.VALUE_TYPE);
      HasValueRestriction valueRes = restriction.asHasValueRestriction();
      result.setExactValue(valueRes.getHasValue().asLiteral().getString());
    } else if (restriction.isSomeValuesFromRestriction()) {
      result.setType(RestrictionSchema.SOME_TYPE);
    } else {
      result.setType(getOwl2RestrictionType(restriction));
      result.setCardinalitySchema(getOwl2CardinalitySchema(restriction));
    }
    return result;
  }

  PropertySchema getPropertySchema(OntProperty property) {
    PropertySchema descriptor = new PropertySchema();
    if (property.isOntLanguageTerm()) {
      return descriptor;
    }
    descriptor.setName(property.getLocalName());
    descriptor.setType(property.isObjectProperty()? "ObjectProperty": "DataTypeProperty");
    OntResource rangeResource = property.getRange();
    OntClass rangeClass = rangeResource.asClass();
    if (rangeClass.isEnumeratedClass()) {
      ListIterator<RDFNode> possibleValues = rangeClass.asEnumeratedClass().getOneOf().asJavaList().listIterator();
      String possibleValue = "";
      while (possibleValues.hasNext()) {
        possibleValue += possibleValues.next().asLiteral().getString();
        if (possibleValues.hasNext()) {
          possibleValue += ";";
        }
      }
      descriptor.setRange(possibleValue);
    } else {
      descriptor.setRange(rangeClass.getLocalName());
    }
    descriptor.setMandatory(property.isFunctionalProperty());
    descriptor.setSymmetric(property.isSymmetricProperty());
    descriptor.setReflexive(property.hasRDFType(OWL2.ReflexiveProperty));
    descriptor.setIrreflexive(property.hasRDFType(OWL2.IrreflexiveProperty));
    return descriptor;
  }

  CardinalitySchema getOwl2CardinalitySchema(Restriction restriction) throws OntologyException {
    RDFNode qualifiedCardinality = restriction.getPropertyValue(OWL2.qualifiedCardinality);
    RDFNode maxQualifiedCardinality = restriction.getPropertyValue(OWL2.maxQualifiedCardinality);
    RDFNode minQualifiedCardinality = restriction.getPropertyValue(OWL2.minQualifiedCardinality);
    String occurrences;

    CardinalitySchema cardinalitySchema = new CardinalitySchema();

    if (qualifiedCardinality != null) {
      occurrences = qualifiedCardinality.asLiteral().getString();
    } else if (maxQualifiedCardinality != null) {
      occurrences = maxQualifiedCardinality.asLiteral().getString();
    } else if (minQualifiedCardinality !=null) {
      occurrences = minQualifiedCardinality.asLiteral().getString();
    } else {
      throw new OntologyException("CRDL.1", "Cannot calculate cardinalitySchema");
    }

    cardinalitySchema.setOccurrence(occurrences);
    cardinalitySchema.setDataRangeRestrictions(generateDataRangeRestrictions(restriction));

    return cardinalitySchema;
  }

  String getOwl2RestrictionType(Restriction restriction) throws OntologyException {
    RDFNode qualifiedCardinality = restriction.getPropertyValue(OWL2.qualifiedCardinality);
    RDFNode maxQualifiedCardinality = restriction.getPropertyValue(OWL2.maxQualifiedCardinality);
    RDFNode minQualifiedCardinality = restriction.getPropertyValue(OWL2.minQualifiedCardinality);
    String type;
    if (qualifiedCardinality != null) {
      type = RestrictionSchema.EXACTLY_TYPE;
    } else if (maxQualifiedCardinality != null) {
      type = RestrictionSchema.MAX_TYPE;
    } else if (minQualifiedCardinality !=null) {
      type = RestrictionSchema.MIN_TYPE;
    } else {
      throw new OntologyException("RESTYP.1", "Cannot calculate restriction type");
    }

    return type;
  }

  List<DataRangeRestrinctionSchema> generateDataRangeRestrictions(OntClass ont) {
    List<DataRangeRestrinctionSchema> dataRanges = new ArrayList<>();
    List<Statement> ranges = readDataRangeRestrictions(ont);
    for(Statement stmt : ranges) {
      DataRangeRestrinctionSchema dataRange = new DataRangeRestrinctionSchema();
      dataRange.setPredicate(stmt.getPredicate().getLocalName());
      Literal value = stmt.getLiteral();
      dataRange.setValue(value.getString());
      dataRange.setDatatype(value.getDatatypeURI());
      dataRanges.add(dataRange);
    }

    return dataRanges;
  }

  List<Statement> readDataRangeRestrictions (OntClass ont) {
    Resource dataRangeResource = ont.getPropertyResourceValue(OWL2.onDataRange);
    if(dataRangeResource != null) {
      Resource withRestrictionResource = dataRangeResource.getPropertyResourceValue(OWL2.withRestrictions);
      if (withRestrictionResource != null) {
        Resource restrictions = withRestrictionResource.getPropertyResourceValue(RDF.first);
        if (restrictions != null) {
          return  restrictions.listProperties().toList();
        }
      }
    }
    return new ArrayList<>();
  }

  OntClass getOntClass(String className) {
    OntClass result = this.model.getOntClass(ontologyProps.getNamespace() + className);
    return result;
  }

  String getPrefixedEntity(String entityName) {
    return ontologyProps.getPrefix() + ":" + entityName;
  }

  ParameterizedSparqlString getPrefixedSparql() {
    Map<String, String> prefixes = new HashMap<>();
    prefixes.put(ontologyProps.getPrefix(), ontologyProps.getNamespace());
    prefixes.put("owl", "http://www.w3.org/2002/07/owl#");
    prefixes.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
    prefixes.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
    prefixes.put("xsd", "http://www.w3.org/2001/XMLSchema#");

    ParameterizedSparqlString sparql = new ParameterizedSparqlString();
    sparql.setNsPrefixes(prefixes);
    return sparql;
  }

  @PostConstruct
  void setupModel () {
    String ontologyName = this.ontologyProps.getOntologyName();

    ModelMaker maker= ModelFactory.createMemModelMaker();
    OntModelSpec spec = new OntModelSpec(PelletReasonerFactory.THE_SPEC);
    spec.setBaseModelMaker(maker);
    spec.setImportModelMaker(maker);
    Model base = maker.createModel( ontologyName );
    this.model = ModelFactory.createOntologyModel(spec, base);
    this.model.read("file:" + this.ontologyProps.getOntologyLocation(), this.ontologyProps.getOntologyType());

    ds = TDB2Factory.connectDataset(this.ontologyProps.getDatasetLocation());
    ds.begin(ReadWrite.WRITE);
    ds.addNamedModel(ontologyProps.getOntologyName(), ModelFactory.createOntologyModel());
    ds.commit();
    ds.close();
  }

}
