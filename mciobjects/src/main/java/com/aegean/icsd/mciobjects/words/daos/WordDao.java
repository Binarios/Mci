package com.aegean.icsd.mciobjects.words.daos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aegean.icsd.mciobjects.common.beans.ProviderException;
import com.aegean.icsd.mciobjects.common.daos.Exceptions;
import com.aegean.icsd.mciobjects.words.beans.Word;
import com.aegean.icsd.ontology.beans.OntologyException;
import com.aegean.icsd.ontology.interfaces.IMciModelReader;
import com.aegean.icsd.ontology.interfaces.IOntologyConnector;
import com.aegean.icsd.ontology.queries.AskQuery;

@Repository
public class WordDao implements IWordDao {

  @Autowired
  private IMciModelReader model;

  @Autowired
  private IOntologyConnector ont;

  @Override
  public boolean areSynonyms(Word thisWord, Word otherWord) throws ProviderException {
    AskQuery ask = new AskQuery.Builder()
      .is("this", "hasId", "thisId")
      .is("this", "hasSynonym", "other")
      .is("other", "hasId", "otherId")
      .addIriParam("hasId", model.getPrefixedEntity("hasId"))
      .addIriParam("hasSynonym", model.getPrefixedEntity("hasSynonym"))
      .addLiteralParam("thisId", thisWord.getId())
      .addLiteralParam("otherId", otherWord.getId())
      .build();

    try {
      return ont.ask(ask);
    } catch (OntologyException e) {
      throw Exceptions.FailedToAsk(String.format("%s is not synonym with %s", thisWord.getValue(), otherWord.getValue() ), e);
    }
  }

  @Override
  public boolean areAntonyms(Word thisWord, Word otherWord) throws ProviderException {
    AskQuery ask = new AskQuery.Builder()
      .is("this", "hasId", "thisId")
      .is("this", "hasAntonym", "other")
      .is("other", "hasId", "otherId")
      .addIriParam("hasId", model.getPrefixedEntity("hasId"))
      .addIriParam("hasAntonym", model.getPrefixedEntity("hasAntonym"))
      .addLiteralParam("thisId", thisWord.getId())
      .addLiteralParam("otherId", otherWord.getId())
      .build();

    try {
      return ont.ask(ask);
    } catch (OntologyException e) {
      throw Exceptions.FailedToAsk(String.format("%s is not antonym with %s", thisWord.getValue(), otherWord.getValue() ), e);
    }
  }
}
