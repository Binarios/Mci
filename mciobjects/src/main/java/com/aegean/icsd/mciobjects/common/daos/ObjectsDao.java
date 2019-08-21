package com.aegean.icsd.mciobjects.common.daos;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.core.annotations.Id;
import com.aegean.icsd.engine.core.interfaces.IAnnotationReader;
import com.aegean.icsd.engine.generator.beans.BaseGameObject;
import com.aegean.icsd.engine.rules.beans.EntityProperty;
import com.aegean.icsd.mciobjects.common.beans.ProviderException;
import com.aegean.icsd.mciobjects.words.beans.Word;
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
      .setDistinct(true)
      .whereHasType("s", model.getPrefixedEntity(forEntity))
      .whereHasType("obj", model.getPrefixedEntity(objectName))
      .where("obj", "hasId", "objId")
      .minus("s", "p", "obj")
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
  public <T extends BaseGameObject> List<String> getAssociatedIdOnProperty(String id, EntityProperty onProperty, Class<T> object) throws ProviderException {
    String objectName;
    try {
      objectName = ano.getEntityValue(object);
    } catch (EngineException e) {
      throw Exceptions.FailedToRetrieveObjects(object.getSimpleName(), e);
    }

    SelectQuery.Builder qBuilder = new SelectQuery.Builder()
      .select("associatedId")
      .where("s", "p", "id")
      .where("s", onProperty.getName(), "obJ")
      .whereHasType("obJ",  model.getPrefixedEntity(objectName))
      .where("obJ", "hasId", "associatedId")
      .addIriParam("hasId", model.getPrefixedEntity("hasId"))
      .addIriParam(onProperty.getName(), model.getPrefixedEntity(onProperty.getName()));


    if (!StringUtils.isEmpty(id)) {
      qBuilder.addLiteralParam("id", id);
    }

    try {
      JsonArray results = ont.select(qBuilder.build());
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
  public <T extends BaseGameObject> boolean areObjectsAssociatedOn(T thisObj, T thatObj, EntityProperty onProperty) throws ProviderException {
    AskQuery query = new AskQuery.Builder()
      .is(thisObj.getId(), onProperty.getName(), thatObj.getId())
      .addIriParam(thisObj.getId(), model.getPrefixedEntity(thisObj.getId()))
      .addIriParam(thatObj.getId(), model.getPrefixedEntity(thatObj.getId()))
      .addIriParam(onProperty.getName(), model.getPrefixedEntity(onProperty.getName()))
      .build();

    try {
      return ont.ask(query);
    } catch (OntologyException e) {
      throw Exceptions.FailedToAsk(String.format("Id %s is associated with id %s on property %s",
        thisObj.getId(),
        thatObj.getId(),
        onProperty.getName()), e);
    }
  }

  @Override
  public List<String> getIdAssociatedWithOtherOnProperty(String otherId, EntityProperty onProperty) throws ProviderException {
    SelectQuery q = new SelectQuery.Builder()
      .select("thisId")
      .where("thisObj", "hasId", "thisId")
      .where("thisObj", onProperty.getName(), "otherObj")
      .where("otherObj", "hasId", "otherId")
      .addIriParam(onProperty.getName(), model.getPrefixedEntity(onProperty.getName()))
      .addIriParam("hasId", model.getPrefixedEntity("hasId"))
      .addLiteralParam("otherId", otherId)
      .build();

    try {
      JsonArray results = ont.select(q);
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
  public List<String> getIdAssociatedWithOtherOnProperty(String thisType, String otherType, String otherId, EntityProperty onProperty) throws ProviderException {
    SelectQuery q = new SelectQuery.Builder()
      .select("thisId")
      .whereHasType("thisObj", model.getPrefixedEntity(thisType))
      .where("thisObj", "hasId", "thisId")
      .where("thisObj", onProperty.getName(), "otherObj")
      .whereHasType("otherObj", model.getPrefixedEntity(otherType))
      .where("otherObj", "hasId", "otherId")
      .addIriParam(onProperty.getName(), model.getPrefixedEntity(onProperty.getName()))
      .addIriParam("hasId", model.getPrefixedEntity("hasId"))
      .addLiteralParam("otherId", otherId)
      .build();

    try {
      JsonArray results = ont.select(q);
      List<String> ids = new ArrayList<>();
      for (JsonElement elem : results) {
        ids.add(elem.getAsJsonObject().get("thisId").getAsString());
      }
      return ids;
    } catch (OntologyException e) {
      throw Exceptions.FailedToRetrieveObjects("associated object", e);
    }
  }

}
