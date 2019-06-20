package com.aegean.icsd.mci.ontology;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.Restriction;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.tdb.TDBFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aegean.icsd.mci.generator.beans.DatasetProperties;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TestMciOntology {

  @InjectMocks
  private MciOntology ont = new MciOntology();

  @Mock(lenient = true)
  private DatasetProperties ds;

  @Test
  public void testSetupDataset() throws MciOntologyException {
    given(ds.getOntologyName()).willReturn("games");
    given(ds.getDatasetLocation()).willReturn("../../ontology/dataset");
    given(ds.getOntologyLocation()).willReturn("../../MciOntology/games.owl");
    given(ds.getOntologyType()).willReturn("ttl");

    ont.setupDataset();
    Assertions.assertTrue(TDBFactory.inUseLocation("../../dataset"));
  }

  @Test
  public void getTriplesForClass() throws MciOntologyException {
    given(ds.getOntologyName()).willReturn("games");
    given(ds.getDatasetLocation()).willReturn("../../ontology/dataset");
    given(ds.getOntologyLocation()).willReturn("../../MciOntology/games.owl");
    given(ds.getOntologyType()).willReturn("ttl");
    given(ds.getNamespace()).willReturn("http://www.semanticweb.org/iigou/diplomatiki/ontologies/Games#");
    ont.setupDataset();
    OntClass entity = ont.getOntClass("EasyObservation");

    Assertions.assertNotNull(entity);

    var superClassesIt = entity.listSuperClasses();
    List<String> restrictions = new ArrayList<>();
    while(superClassesIt.hasNext()) {
      var sc = superClassesIt.next();
      if(sc.isRestriction()) {
        Restriction res = sc.asRestriction();
        var pr = res.getOnProperty();
        if(res.isSomeValuesFromRestriction() && pr.isDatatypeProperty()) {
          var some = res.asSomeValuesFromRestriction();
          Resource hm = some.getSomeValuesFrom();
          //listProperties().toList().get(0).getList().asJavaList().get(0).asResource().listProperties().toList()
          //hm.listProperties().toList().get(0).getObject().asResource().listProperties().toList().get(1).getObject().asResource().listProperties().toList().get(0).getObject().asLiteral().getString()
          var it = hm.listProperties();
          while(it.hasNext()) {
            var l = it.next();
            if(l.isReified()) {
              var k = l.listReifiedStatements();
            }
          }
        }
        restrictions.add(pr.getLocalName() + ": " + pr.getRange().getLocalName());
      }
    }
    List<String> props = new ArrayList<>();
    List<String> objProps = new ArrayList<>();
    List<String> dataProps = new ArrayList<>();
    var it = entity.listDeclaredProperties(false);
    while(it.hasNext()) {
      var prop = it.next();
      if(prop.isDatatypeProperty()) {
        dataProps.add(prop.getLocalName());
      }
      if(prop.isObjectProperty()) {
        objProps.add(prop.getLocalName());
      }
      if(!prop.isAnnotationProperty()) {
        props.add(prop.getLocalName());
      }
    }
    Assertions.assertTrue(props.size() == 6);
    Assertions.assertTrue(objProps.size() == 0);
    Assertions.assertTrue(dataProps.size() == 6);
  }
}
