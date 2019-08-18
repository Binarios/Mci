package com.aegean.icsd.mciwebapp.recall.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aegean.icsd.mciwebapp.common.GameExceptions;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.recall.beans.Recall;
import com.aegean.icsd.ontology.beans.OntologyException;
import com.aegean.icsd.ontology.interfaces.IMciModelReader;
import com.aegean.icsd.ontology.interfaces.IOntologyConnector;
import com.aegean.icsd.ontology.queries.AskQuery;
import com.aegean.icsd.ontology.queries.SelectQuery;

import com.google.gson.JsonArray;

@Repository
public class RecallDao implements IRecallDao{

  @Autowired
  private IOntologyConnector ont;

  @Autowired
  private IMciModelReader model;

  @Override
  public boolean existsWithRecallNumber(Long recallNumber) throws MciException {
    AskQuery q = new AskQuery.Builder()
      .is("s", "hasRecallNumberValue",  "value")
      .addIriParam("hasRecallNumberValue", model.getPrefixedEntity("hasRecallNumberValue"))
      .addLiteralParam("value", recallNumber)
      .build();

    try {
      return ont.ask(q);
    } catch (OntologyException e) {
      throw GameExceptions.GenerationError(Recall.NAME, e);
    }
  }

  @Override
  public Long getRecallNumber(String id) throws MciException {
    SelectQuery q = new SelectQuery.Builder()
      .select("recallNumber")
      .where("s", "hasId", "id")
      .where("s", "hasRecallNumberValue", "recallNumber")
      .addIriParam("hasId", model.getPrefixedEntity("hasId"))
      .addIriParam("hasRecallNumberValue", model.getPrefixedEntity("hasRecallNumberValue"))
      .addLiteralParam("id", id)
      .build();

    try {
      JsonArray resultRaw = ont.select(q);
      Long recallNumber = null;
      if (resultRaw.size() != 0) {
        recallNumber = resultRaw.get(0).getAsJsonObject().get("recallNumber").getAsLong();
      }
      return recallNumber;
    } catch (OntologyException e) {
      throw GameExceptions.GenerationError(Recall.NAME, e);
    }
  }
}
