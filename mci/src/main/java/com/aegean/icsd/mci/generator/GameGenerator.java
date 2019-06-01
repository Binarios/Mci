package com.aegean.icsd.mci.generator;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.apache.jena.ontology.CardinalityRestriction;
import org.apache.jena.ontology.MaxCardinalityRestriction;
import org.apache.jena.ontology.MinCardinalityRestriction;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.ontology.Restriction;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.springframework.beans.factory.annotation.Autowired;

import com.aegean.icsd.mci.ontology.beans.Game;
import com.aegean.icsd.mci.ontology.beans.IndividualDescriptor;
import com.aegean.icsd.mci.ontology.beans.Difficulty;
import com.aegean.icsd.mci.ontology.MciOntologyException;
import com.aegean.icsd.mci.ontology.IMciOntology;
import com.aegean.icsd.mci.ontology.beans.PropertyDescriptor;
import com.aegean.icsd.mci.ontology.beans.PropertyType;

public class GameGenerator implements IGameGenerator{

  @Autowired
  private IMciOntology ont;

  @Override
  public IndividualDescriptor generateGame(Game game, Difficulty difficulty, String level) throws MciOntologyException {
    OntClass entity = ont.getOntClass(generateIRI(game.toString()));
    IndividualDescriptor desc = new IndividualDescriptor();
    String id = UUID.randomUUID().toString();
    desc.setId(id);
    desc.setIndividualName(id);

    List<PropertyDescriptor> restrictions = new ArrayList<>();
    List<IndividualDescriptor> parents = new ArrayList<>();
    List<PropertyDescriptor> properties = new ArrayList<>();
    ExtendedIterator<OntClass> superClassesIt = entity.listSuperClasses(true);
    while (superClassesIt.hasNext()) {
      OntClass superClass = superClassesIt.next();
      if (superClass.isRestriction()) {
        PropertyDescriptor restrictionDesc = generateRestriction(superClass.asRestriction());
        restrictions.add(restrictionDesc);
      } else {
        IndividualDescriptor parent = getOrGenerateIndividual(superClass);
        parents.add(parent);
      }
    }

    ExtendedIterator<OntProperty> propIt = entity.listDeclaredProperties(true);
    while (propIt.hasNext()) {
      PropertyDescriptor propertyDesc = generateProperty(propIt.next());
      properties.add(propertyDesc);
    }
    desc.setProperties(properties);
    desc.setParents(parents);
    desc.setRestrictions(restrictions);
    return desc;
  }

  IndividualDescriptor getOrGenerateIndividual(OntClass ontClass) {
    IndividualDescriptor individual = getIndividualWithClass(ontClass.getLocalName());
    return individual == null ? generateIndividual(ontClass) : individual ;
  }

  IndividualDescriptor generateIndividual(OntClass superClass) {
    IndividualDescriptor desc = new IndividualDescriptor();

    return desc;
  }

  PropertyDescriptor generateRestriction(Restriction resClass) {
    OntProperty restrictionProp = resClass.getOnProperty();

    PropertyDescriptor descriptor = generateProperty(restrictionProp);

    descriptor.setCardinalityMax(Integer.MIN_VALUE);
    descriptor.setCardinalityMin(Integer.MAX_VALUE);

    if(resClass.isSomeValuesFromRestriction()) {
      descriptor.setCardinalityMin(1);
    }
    if (resClass.isMaxCardinalityRestriction()) {
      MaxCardinalityRestriction maxRes = resClass.asMaxCardinalityRestriction();
      descriptor.setCardinalityMax(maxRes.getMaxCardinality());
    }
    if (resClass.isMinCardinalityRestriction()) {
      MinCardinalityRestriction minRes = resClass.asMinCardinalityRestriction();
      descriptor.setCardinalityMin(minRes.getMinCardinality());
    }
    if(resClass.isCardinalityRestriction()) {
      CardinalityRestriction cardinalityRes = resClass.asCardinalityRestriction();
      descriptor.setCardinalityMin(cardinalityRes.getCardinality());
      descriptor.setCardinalityMax(cardinalityRes.getCardinality());
    }

    return descriptor;
  }

  PropertyDescriptor generateProperty(OntProperty property) {
    PropertyDescriptor descriptor = new PropertyDescriptor();
    descriptor.setName(property.getLocalName());

    if(property.isDatatypeProperty()) {
      descriptor.setType(PropertyType.DatatypeProperty);
      OntResource rangeType = property.getRange();
      String valueToUse = "1";
      descriptor.setRangeValue(valueToUse +"^^" + rangeType.getLocalName());
    }

    if(property.isObjectProperty()) {
      descriptor.setType(PropertyType.ObjectProperty);
      descriptor.setRangeIndividual(generateIndividual(property.getRange().asClass()));
    }

    return descriptor;
  }

  IndividualDescriptor getIndividualWithClass(String className) {
    return null;
  }


  String generateIRI(String element) {
    return ont.getNamespace() + element;
  }

  int randomNumber(int min, int max) {
    Random random = new Random();
    return random.nextInt((max - min) + 1) + min;

  }
}
