package com.aegean.icsd.mci.ontology;

import java.io.FileNotFoundException;

import org.apache.jena.tdb.TDBFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aegean.icsd.mci.ontology.beans.DatasetProperties;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TestMciOntology {

  @InjectMocks
  private MciOntology ont = new MciOntology();

  @Mock(lenient = true)
  private DatasetProperties ds;

  @Test
  public void testSetupDataset() throws FileNotFoundException {
    given(ds.getOntologyName()).willReturn("games");
    given(ds.getDatasetLocation()).willReturn("../../dataset");
    given(ds.getOntologyLocation()).willReturn("../../MciOntology/games.owl");
    given(ds.getOntologyType()).willReturn("ttl");

    ont.setupDataset();
    Assertions.assertTrue(TDBFactory.inUseLocation("../../dataset"));
  }
}
