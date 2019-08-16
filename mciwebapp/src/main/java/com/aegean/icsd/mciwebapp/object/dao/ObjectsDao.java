package com.aegean.icsd.mciwebapp.object.dao;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.core.interfaces.IAnnotationReader;
import com.aegean.icsd.engine.generator.beans.BaseGameObject;
import com.aegean.icsd.engine.rules.beans.EntityProperty;
import com.aegean.icsd.mciwebapp.object.beans.ProviderException;
import com.aegean.icsd.mciwebapp.object.beans.Word;
import com.aegean.icsd.ontology.beans.OntologyException;
import com.aegean.icsd.ontology.interfaces.IMciModelReader;
import com.aegean.icsd.ontology.interfaces.IOntologyConnector;
import com.aegean.icsd.ontology.queries.AskQuery;
import com.aegean.icsd.ontology.queries.SelectQuery;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

@Repository
public class ObjectsDao implements IObjectsDao {

  @Autowired
  private IOntologyConnector ont;

  @Autowired
  private IMciModelReader model;

  @Autowired
  private IAnnotationReader ano;

  @Override
  public  <T extends BaseGameObject> List<String> getObjectIds(Class<T> object) throws ProviderException {
    String objectName;
    try {
      objectName = ano.getEntityValue(object);
    } catch (EngineException e) {
      throw Exceptions.FailedToRetrieveObjects(object.getSimpleName(), e);
    }

    SelectQuery q = new SelectQuery.Builder()
      .select("objId")
      .whereHasType("obj", model.getPrefixedEntity(objectName))
      .where("obj", "hasId", "objId")
      .addIriParam("hasId", model.getPrefixedEntity("hasId"))
      .build();

    try {
      JsonArray results = ont.select(q);
      List<String> ids = new ArrayList<>();
      for (JsonElement result : results) {
        ids.add(result.getAsJsonObject().get("objId").getAsString());
      }
      return ids;
    } catch (OntologyException e) {
      throw Exceptions.FailedToRetrieveObjects(objectName, e);
    }
  }

  @Override
  public <T extends BaseGameObject> List<String> getNewObjectIdsFor(String forEntity, Class<T> object)
    throws ProviderException {

    String objectName;
    try {
      objectName = ano.getEntityValue(object);
    } catch (EngineException e) {
      throw Exceptions.FailedToRetrieveObjects(object.getSimpleName(), e);
    }

    SelectQuery q = new SelectQuery.Builder()
      .select("objId")
      .whereHasType("s", model.getPrefixedEntity(forEntity))
      .whereHasType("obj", model.getPrefixedEntity(objectName))
      .where("obj", "hasId", "objId")
      .minus("s", "p", "word")
      .addIriParam("hasId", model.getPrefixedEntity("hasId"))
      .build();

    try {
      JsonArray results = ont.select(q);
      List<String> ids = new ArrayList<>();
      for (JsonElement result : results) {
        ids.add(result.getAsJsonObject().get("objId").getAsString());
      }
      return ids;
    } catch (OntologyException e) {
      throw Exceptions.FailedToRetrieveObjects(Word.NAME, e);
    }
  }

  @Override
  public <T extends BaseGameObject> List<String> getAssociatedObjectOfId(String id, Class<T> object)
    throws ProviderException {

    String objectName;
    try {
      objectName = ano.getEntityValue(object);
    } catch (EngineException e) {
      throw Exceptions.FailedToRetrieveObjects(object.getSimpleName(), e);
    }

    SelectQuery q = new SelectQuery.Builder()
      .select("associatedId")
      .where("s", "p", "id")
      .where("s", "pAll", "obJ")
      .whereHasType("obJ",  model.getPrefixedEntity(objectName))
      .where("obJ", "hasId", "associatedId")
      .addIriParam("hasId", model.getPrefixedEntity("hasId"))
      .addLiteralParam("id", id)
      .build();

    try {
      JsonArray results = ont.select(q);
      List<String> ids = new ArrayList<>();
      for (JsonElement result : results) {
        ids.add(result.getAsJsonObject().get("associatedId").getAsString());
      }
      return ids;
    } catch (OntologyException e) {
      throw Exceptions.FailedToRetrieveObjects(id, e);
    }
  }

  @Override
  public List<String> getIdAssociatedWithOtherOnProperty(String otherId, EntityProperty onProperty) throws ProviderException {
    SelectQuery q = new SelectQuery.Builder()
      .select("thisId")
      .where("thisObj", "hasId", "thisId")
      .where("thisId", onProperty.getName(), "otherObj")
      .where("otherObj", "hasId", "otherId")
      .addIriParam(onProperty.getName(), model.getPrefixedEntity(onProperty.getName()))
      .addIriParam("hasId", model.getPrefixedEntity("hasId"))
      .addLiteralParam("otherId", otherId)
      .build();

    try {
      JsonArray results = ont.select(q);
      if (results.size() == 0) {
        throw Exceptions.FailedToRetrieveObjects("associated object");
      }
      List<String> ids = new ArrayList<>();
      for (JsonElement elem : results) {
        ids.add(elem.getAsJsonObject().get("thisId").getAsString());
      }
      return ids;
    } catch (OntologyException e) {
      throw Exceptions.FailedToRetrieveObjects("associated object", e);
    }
  }

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
