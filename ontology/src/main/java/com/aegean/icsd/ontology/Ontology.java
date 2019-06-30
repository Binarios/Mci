package com.aegean.icsd.ontology;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.ontology.AllValuesFromRestriction;
import org.apache.jena.ontology.HasValueRestriction;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.Restriction;
import org.apache.jena.ontology.SomeValuesFromRestriction;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.OWL2;
import org.apache.jena.vocabulary.RDF;
import org.springframework.beans.factory.annotation.Autowired;

import com.aegean.icsd.connection.ConnectionException;
import com.aegean.icsd.connection.ITdbConnection;
import com.aegean.icsd.ontology.beans.DatasetProperties;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Ontology implements IOntology {

  @Autowired
  private DatasetProperties ontologyProps;

  @Autowired
  private ITdbConnection conProvider;

  private Dataset dataset;

  @Override
  public JsonArray executeSelect(ParameterizedSparqlString sparql, List<String> colNames) throws OntologyException {
    Connection con = null;
    JsonArray result = new JsonArray();
    try {
      con = conProvider.connect(ontologyProps.getDatasetLocation());
      java.sql.Statement sel = con.createStatement();
      ResultSet raw = sel.executeQuery(sparql.asQuery().toString());
      while (raw.next()) {
        JsonObject row = new JsonObject();
        for(String col : colNames) {
          row.addProperty(col, raw.getString(col));
        }
      }
      raw.close();
      return result;
    } catch (ConnectionException | SQLException e) {
      throw new OntologyException("ONT.EX1", "Cannot execute sparql query", e);
    } finally {
      try {
        if (con != null && !con.isClosed()) {
          con.close();
        }
      } catch (SQLException e) {
        //noinspection ThrowFromFinallyBlock
        throw new OntologyException("ONT.EX2", "Cannot close connection", e);
      }
    }
  }

  @Override
  public boolean executeUpdate(ParameterizedSparqlString sparql) throws OntologyException {
    Connection con = null;
    try{
      con = conProvider.connect(ontologyProps.getDatasetLocation());
      java.sql.Statement sel = con.createStatement();
      int res = sel.executeUpdate(sparql.asUpdate().toString());
      return res == 0;
    } catch (SQLException | ConnectionException e) {
      throw new OntologyException("ONT.EX3", "Cannot execute sparql query", e);
    } finally {
      try {
        if (con != null && !con.isClosed()) {
          con.close();
        }
      } catch (SQLException e) {
        //noinspection ThrowFromFinallyBlock
        throw new OntologyException("ONT.EX4", "Cannot close connection", e);
      }
    }
  }

  @Override
  public JsonObject generateIndividual(String className) throws OntologyException {
    JsonObject result = new JsonObject();
    String id = UUID.randomUUID().toString();
    result.addProperty("class", className);
    result.addProperty("id", id);
    OntClass entity = getOntClass(className);

    JsonArray properties = new JsonArray();
    JsonArray restrictions = new JsonArray();

    //entity.getEquivalentClass().getPropertyResourceValue(OWL2.intersectionOf).getPropertyResourceValue(RDF.rest).getPropertyResourceValue(RDF.first).listProperties().toList()
    //entity.getEquivalentClass().getPropertyResourceValue(OWL2.intersectionOf).getPropertyResourceValue(RDF.rest).getPropertyResourceValue(RDF.rest).listProperties().toList()


    ExtendedIterator<OntProperty> propIt = entity.listDeclaredProperties();
    while (propIt.hasNext()) {
      JsonObject propertyDesc = generateProperty(propIt.next());
      properties.add(propertyDesc);
    }

    ExtendedIterator<OntClass> superClassesIt = entity.listSuperClasses();
    while (superClassesIt.hasNext()) {
      OntClass superClass = superClassesIt.next();
      if (superClass.isRestriction()) {
        Restriction resClass = superClass.asRestriction();
        JsonObject restriction = generateRestriction(resClass);
        restrictions.add(restriction);
      }
    }

    result.add("properties", properties);
    result.add("restrictions", restrictions);
    return result;
  }


  JsonObject generateRestriction(Restriction restriction) {
    JsonObject obj = new JsonObject();
    OntProperty resProp = restriction.getOnProperty();
    obj.add("restrictionProperty", generateProperty(resProp));

    if (restriction.isAllValuesFromRestriction()) {
      obj.addProperty("restrictionType", "only");
    } else if (restriction.isHasValueRestriction()) {
      obj.addProperty("restrictionType", "value");
      HasValueRestriction valueRes = restriction.asHasValueRestriction();
      obj.addProperty("restrictionCardinality", valueRes.getHasValue().asLiteral().getString());
    } else if (restriction.isSomeValuesFromRestriction()) {
      obj.addProperty("restrictionType", "some");
    } else {
      obj.addProperty("restrictionType", "cardinality");
      obj.add("restrictionCardinality", generateOwl2Cardinality(restriction));
    }
    return obj;
  }

  JsonObject generateProperty(OntProperty property) {
    JsonObject descriptor = new JsonObject();
    descriptor.addProperty("name", property.getLocalName());
    descriptor.addProperty("type", property.isObjectProperty()? "ObjectProperty": "DataTypeProperty");
    descriptor.addProperty("range", property.getRange().asClass().getLocalName());
    return descriptor;
  }

  JsonObject generateOwl2Cardinality(Restriction restriction) {
    RDFNode qualifiedCardinality = restriction.getPropertyValue(OWL2.qualifiedCardinality);
    RDFNode maxQualifiedCardinality = restriction.getPropertyValue(OWL2.maxQualifiedCardinality);
    RDFNode minQualifiedCardinality = restriction.getPropertyValue(OWL2.minQualifiedCardinality);
    JsonObject cardinality = new JsonObject();
    String type = null;
    String occurrences = null;

    if (qualifiedCardinality != null) {
      type = "exactly";
      occurrences = qualifiedCardinality.asLiteral().getString();
    } else if (maxQualifiedCardinality != null) {
      type = "max";
      occurrences = maxQualifiedCardinality.asLiteral().getString();
    } else if (minQualifiedCardinality !=null) {
      type = "min";
      occurrences = minQualifiedCardinality.asLiteral().getString();
    }
    cardinality.addProperty("type", type);
    cardinality.addProperty("occurrences", occurrences);
    cardinality.add("dataRangeRestrictions", generateDataRangeRestrictions(restriction));

    return cardinality;
  }

  JsonArray generateDataRangeRestrictions(OntClass ont) {
    JsonArray dataRanges = new JsonArray();
    List<Statement> ranges = readDataRangeRestrictions(ont);
    for(Statement stmt : ranges) {
      JsonObject dataRange = new JsonObject();
      dataRange.addProperty("predicate", stmt.getPredicate().getLocalName());
      Literal value = stmt.getLiteral();
      dataRange.addProperty("value", value.getString());
      dataRange.addProperty("dataType",  value.getDatatypeURI());
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

  OntClass getOntClass(String className) throws OntologyException {
    OntModel model = ModelFactory.createOntologyModel();
    try {
      FileInputStream inputStream = new FileInputStream(ontologyProps.getOntologyLocation());
      model.read(inputStream, null, this.ontologyProps.getOntologyType());
      return  model.getOntClass(ontologyProps.getNamespace() + className);
    } catch (FileNotFoundException e) {
      throw new OntologyException("ONT.GETCLASS.1", "Cannot load ontology model", e);
    }
  }

  @PostConstruct
  void setupDataset() throws OntologyException {
    String ontologyName = this.ontologyProps.getOntologyName();
    if (this.dataset == null) {
      this.dataset = TDBFactory.createDataset(this.ontologyProps.getDatasetLocation());
    }
    this.dataset.begin(ReadWrite.READ);
    boolean init = !this.dataset.containsNamedModel(ontologyName) || this.dataset.isEmpty();
    this.dataset.end();
    if (init) {
      this.dataset.begin(ReadWrite.WRITE);
      OntModel model = ModelFactory.createOntologyModel();
      try {
        FileInputStream is = new FileInputStream(this.ontologyProps.getOntologyLocation());
        model.read(is, null, this.ontologyProps.getOntologyType());
      } catch (FileNotFoundException e) {
        throw new OntologyException("ONT.LOAD.1", "Cannot load ontology model", e);
      }
      dataset.addNamedModel(ontologyName, model);
      dataset.commit();
      dataset.end();
    }
  }

}
