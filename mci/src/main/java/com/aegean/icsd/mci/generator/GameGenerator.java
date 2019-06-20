package com.aegean.icsd.mci.generator;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.apache.jena.ontology.CardinalityRestriction;
import org.apache.jena.ontology.MaxCardinalityRestriction;
import org.apache.jena.ontology.MinCardinalityRestriction;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.Restriction;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.springframework.beans.factory.annotation.Autowired;

import com.aegean.icsd.mci.generator.beans.IndividualDescriptor;
import com.aegean.icsd.mci.generator.dao.IGameGeneratorDao;
import com.aegean.icsd.mci.generator.beans.Game;
import com.aegean.icsd.mci.generator.beans.Difficulty;
import com.aegean.icsd.mci.ontology.MciOntologyException;
import com.aegean.icsd.mci.ontology.IMciOntology;
import com.aegean.icsd.mci.generator.beans.PropertyDescriptor;
import com.aegean.icsd.mci.generator.beans.PropertyType;

public class GameGenerator implements IGameGenerator{

  @Autowired
  private IMciOntology ont;

  @Autowired
  private IGameGeneratorDao ggDao;

  @Override
  public IndividualDescriptor generateGame(Game game, Difficulty difficulty, String playerName) throws MciOntologyException {
    OntClass entity = ont.getOntClass(generateGameIRI(difficulty, game));
    IndividualDescriptor desc = generateIndividual(entity);
    return desc;
  }

  IndividualDescriptor generateIndividual(OntClass ontClass) {
    IndividualDescriptor desc = new IndividualDescriptor();
    String id = UUID.randomUUID().toString();
    String className = ontClass.getLocalName();
    desc.setIndividualClass(className);
    desc.setIndividualName(id);

    List<PropertyDescriptor> properties = new ArrayList<>();
    ExtendedIterator<OntProperty> propIt = ontClass.listDeclaredProperties();
    while (propIt.hasNext()) {
      PropertyDescriptor propertyDesc = generateProperty(propIt.next());
      properties.add(propertyDesc);
    }

    ExtendedIterator<OntClass> superClassesIt = ontClass.listSuperClasses();
    while (superClassesIt.hasNext()) {
      OntClass superClass = superClassesIt.next();
      if (superClass.isRestriction()) {
        Restriction resClass = superClass.asRestriction();
        List<PropertyDescriptor> restrictions = generateClassRestriction(resClass);
        properties.addAll(restrictions);
      }
    }
    desc.setProperties(properties);
    return desc;
  }

  PropertyDescriptor generateProperty(OntProperty property) {
    PropertyDescriptor descriptor = new PropertyDescriptor();
    descriptor.setName(property.getLocalName());
    if(property.isObjectProperty()) {
      descriptor.setType(PropertyType.ObjectProperty);
      IndividualDescriptor range = generateIndividual(property.getRange().asClass());
      descriptor.setRangeIndividual(range);
    } else {
      descriptor.setType(PropertyType.DataTypeProperty);
      String value = generateDataTypeValue(property);
      descriptor.setRangeValue(value);
    }
    return descriptor;
  }

  String generateDataTypeValue(OntProperty property) {
    String type = property.getRange().asClass().getLocalName();

    String result = null;
    //https://www.ibm.com/support/knowledgecenter/en/SSAW57_8.5.5/com.ibm.websphere.nd.multiplatform.doc/ae/txml_mapping.html
    switch (type) {
      //todo impossible to find the correct value range, based on the type. the type alone doesn't bring any meaning
      //todo need to use datatypeproperties somehow different in ontology
      //todo perhaps to be used only as restrictions
      case "string":
        result = generateStringValue(property.getLocalName());
        break;
      case "decimal":
        BigDecimal decimalValue = generateDecimalValue(property.getLocalName());
        result = decimalValue.toString();
        break;
      case "positiveInteger":
        BigInteger positiveIntValue = generatePositiveIntegerValue(property.getLocalName());
        result = positiveIntValue.toString();
        break;
      default:
          break;
    }

    return result;
  }

  private BigInteger generatePositiveIntegerValue(String forProperty) {
    return BigInteger.ZERO;
  }

  private BigDecimal generateDecimalValue(String forProperty) {
    return null;
  }

  private String generateStringValue(String forProperty) {
    return null;
  }

  List<PropertyDescriptor> generateClassRestriction(Restriction restriction) {
    List<PropertyDescriptor> restrictions = null;
    if(restriction.isCardinalityRestriction()) {
      OntProperty resProp = restriction.getOnProperty();
      restrictions = generateCardinalityRestrictions(restriction.asCardinalityRestriction(), resProp);
    }
    if (restriction.isSomeValuesFromRestriction()) {

    }
    if (restriction.isMaxCardinalityRestriction()) {
      MaxCardinalityRestriction maxRes = restriction.asMaxCardinalityRestriction();
    }
    if (restriction.isMinCardinalityRestriction()) {
      MinCardinalityRestriction minRes = restriction.asMinCardinalityRestriction();
    }
    return restrictions;
  }

  List<PropertyDescriptor> generateCardinalityRestrictions(CardinalityRestriction cardinality, OntProperty onProp) {
    List<PropertyDescriptor> restrictions = new ArrayList<>();
    for (int i = 0; i < cardinality.getCardinality(); i++ ) {
      PropertyDescriptor restrictionDesc = generateProperty(onProp);
      if (PropertyType.ObjectProperty.equals(restrictionDesc.getType())) {
        restrictionDesc.setRangeIndividual(generateIndividual(onProp.getRange().asClass()));
      } else if (PropertyType.DataTypeProperty.equals(restrictionDesc.getType())) {
//        restrictionDesc.setRangeValue();
        String type = onProp.getRange().asClass().getLocalName();

      }
      restrictions.add(restrictionDesc);
    }
    return restrictions;
  }

  String getRestrictionRangeType() {
    return null;
  }

  private String generateGameIRI(Difficulty difficulty, Game game) {
    return ont.getMciNamespace() + difficulty.getName() + game.toString();
  }

  int randomNumber(int min, int max) {
    Random random = new Random();
    return random.nextInt((max - min) + 1) + min;

  }
}
