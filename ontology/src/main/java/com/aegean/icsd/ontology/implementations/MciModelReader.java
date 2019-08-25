package com.aegean.icsd.ontology.implementations;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.jena.ontology.AllValuesFromRestriction;
import org.apache.jena.ontology.HasValueRestriction;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.ontology.Restriction;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ModelMaker;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
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
import com.aegean.icsd.ontology.beans.OntologyException;
import com.aegean.icsd.ontology.beans.PropertySchema;
import com.aegean.icsd.ontology.beans.RestrictionSchema;
import com.aegean.icsd.ontology.interfaces.IMciModelReader;

import openllet.jena.PelletReasonerFactory;

@Service
public class MciModelReader implements IMciModelReader {
  private static Logger LOGGER = Logger.getLogger(MciModelReader.class);

  @Autowired
  private DatasetProperties ontologyProps;

  private OntModel model;


  @Override
  public ClassSchema getClassSchema(String className) throws OntologyException {
    LOGGER.info(String.format("Reading ontology schema for class %s", className));
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

  @Override
  public Class<?> getJavaClassFromOwlType(String owlType) {
    Class<?> rangeClass = null;
    switch (owlType) {
      case "string" :
      case "anyURI" :
        rangeClass = String.class;
        break;
      case "positiveInteger":
        rangeClass = Long.class;
        break;
      case "boolean":
        rangeClass = Boolean.class;
        break;
      default:
        if (owlType.contains(";")) {
          rangeClass = String.class;
        }
        break;
    }
    return rangeClass;
  }

  @Override
  public String getPrefixedEntity(String entityName) {
    return ontologyProps.getPrefix() + ":" + entityName;
  }

  @Override
  public String removePrefix(String prefixedEntity) {
    String[] chunks = prefixedEntity.split(":");
    String entity = "";
    if (chunks.length == 1) {
      entity = prefixedEntity;
    } else {
      entity = chunks[1].replace(">", "");
    }
    return entity;
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
      } else if (superClass.isClass()) {
        restrictions.addAll(getRestrictionSchemas(superClass));
      }
    }
    return restrictions;
  }

  List<RestrictionSchema> getEqualityRestrictionSchemas(OntClass entity) throws OntologyException {
    List<RestrictionSchema> equalityRestrictions = new ArrayList<>();
    ExtendedIterator<OntClass> eqIt = entity.listEquivalentClasses();
    while (eqIt.hasNext()) {
      OntClass eqClass = eqIt.next();
      if (eqClass != null && eqClass.isAnon()) {
        Resource intersectionOf = eqClass.getPropertyResourceValue(OWL2.intersectionOf);
        if (intersectionOf != null) {
          getEqualityRestrictionSchema(intersectionOf, equalityRestrictions);
        }
      }
    }
    return equalityRestrictions;
  }

  void getEqualityRestrictionSchema(Resource intersectionOf, List<RestrictionSchema> equalityRestrictions)
    throws OntologyException {
    Resource first = intersectionOf.getPropertyResourceValue(RDF.first);
    if (first != null && first.canAs(OntClass.class)) {
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

  RestrictionSchema getRestrictionSchema(Restriction restriction) throws OntologyException {
    RestrictionSchema result = new RestrictionSchema();
    OntProperty resProp = restriction.getOnProperty();
    result.setOnPropertySchema(getPropertySchema(resProp));

    if (resProp.isObjectProperty()) {
      Resource onClass = restriction.getPropertyResourceValue(OWL2.onClass);
      if (onClass == null) {
        onClass = restriction.getPropertyResourceValue(OWL2.someValuesFrom);
      }
      if (onClass != null) {
        result.getOnPropertySchema().setRange(onClass.getLocalName());
      }
    }

    if (restriction.isAllValuesFromRestriction()) {
      result.setType(RestrictionSchema.ONLY_TYPE);
      AllValuesFromRestriction allVal = restriction.asAllValuesFromRestriction();
      if (!allVal.getAllValuesFrom().isAnon()) {
        result.getOnPropertySchema().setRange(allVal.getAllValuesFrom().getLocalName());
      }
      CardinalitySchema cardinalitySchema = new CardinalitySchema();
      cardinalitySchema.setDataRangeRestrictions(generateDataRangeRestrictions(allVal.getAllValuesFrom()));
      result.setCardinalitySchema(cardinalitySchema);
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
    OntProperty parent = property.getSuperProperty();
    if (!parent.isAnon() && !parent.isOntLanguageTerm() && !property.getLocalName().equals(parent.getLocalName())) {
      descriptor.setParent(parent.getLocalName());
    }
    descriptor.setObjectProperty(property.isObjectProperty());
    OntResource rangeResource = property.getRange();
    OntClass rangeClass = rangeResource.asClass();
    if (rangeClass.isEnumeratedClass()) {
      ListIterator<RDFNode> possibleValues = rangeClass.asEnumeratedClass().getOneOf().asJavaList().listIterator();
      List<String> enumeratedValues = new ArrayList<>();
      String type = null;
      while (possibleValues.hasNext()) {
        Literal possibleValue = possibleValues.next().asLiteral();
        if (!enumeratedValues.contains(possibleValue.toString())) {
          enumeratedValues.add(possibleValue.toString());
        }
        type = removeNamespacePrefix(possibleValue.getDatatypeURI());
      }
      if (type != null) {
        descriptor.setRange(type);
      }
      descriptor.setEnumerations(enumeratedValues);
    } else {
      descriptor.setRange(rangeClass.getLocalName());
    }

    if (property.hasInverse()) {
      OntProperty inverseProp = property.getInverseOf();
      if (!inverseProp.getLocalName().equals(property.getLocalName())) {
        descriptor.setInverse(inverseProp.getLocalName());
      }
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
    Resource dataRangeResource = ont.getPropertyResourceValue(OWL2.onDataRange);
    return generateDataRangeRestrictions(dataRangeResource);
  }

  List<DataRangeRestrinctionSchema> generateDataRangeRestrictions(Resource resource) {
    List<DataRangeRestrinctionSchema> dataRanges = new ArrayList<>();
    if (resource != null) {
      Resource withRestrictionResource = resource.getPropertyResourceValue(OWL2.withRestrictions);
      if (withRestrictionResource != null) {
        getDataRanges(withRestrictionResource, dataRanges);
      }
    }
    return dataRanges;
  }

  void getDataRanges(Resource resource, List<DataRangeRestrinctionSchema> dataRanges) {
    Resource first = resource.getPropertyResourceValue(RDF.first);
    if (first != null ) {
      StmtIterator it = first.listProperties();
      while (it.hasNext()) {
        Statement stmt = it.nextStatement();
        DataRangeRestrinctionSchema eqRestriction = getDataRangeSchema(stmt);
        dataRanges.add(eqRestriction);
      }
      Resource rest = resource.getPropertyResourceValue(RDF.rest);
      if (rest != null) {
        getDataRanges(rest, dataRanges);
      }
    }
  }

  DataRangeRestrinctionSchema getDataRangeSchema(Statement stmt) {
    DataRangeRestrinctionSchema dataRange = new DataRangeRestrinctionSchema();
    dataRange.setPredicate(stmt.getPredicate().getLocalName());
    Literal value = stmt.getLiteral();
    dataRange.setValue(value.getString());
    dataRange.setDatatype(value.getDatatypeURI());
    return dataRange;
  }

  OntClass getOntClass(String className) {
    return this.model.getOntClass(ontologyProps.getNamespace() + className);
  }

  String removeNamespacePrefix (String uri) {
    String[] fragments = uri.split("#");
    if (fragments.length == 1) {
      return uri;
    }

    return fragments[1];
  }

  @PostConstruct
  void setupModel () {
    LOGGER.info("START: Setting up the model");
    String ontologyName = ontologyProps.getOntologyName();

    ModelMaker maker= ModelFactory.createMemModelMaker();
    OntModelSpec spec = new OntModelSpec(PelletReasonerFactory.THE_SPEC);
    spec.setBaseModelMaker(maker);
    spec.setImportModelMaker(maker);
    Model base = maker.createModel( ontologyName );
    model = ModelFactory.createOntologyModel(spec, base);
    LOGGER.info("START: Reading the model from :" + ontologyProps.getOntologyLocation());
    model.read(ontologyProps.getOntologyLocation(), ontologyProps.getOntologyType());
    LOGGER.info("END: Reading the model from :" + ontologyProps.getOntologyLocation());
  }
}
