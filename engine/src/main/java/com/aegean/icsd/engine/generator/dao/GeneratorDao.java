package com.aegean.icsd.engine.generator.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aegean.icsd.engine.common.Utils;
import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.generator.beans.GameInfo;
import com.aegean.icsd.ontology.IOntology;
import com.aegean.icsd.ontology.beans.OntologyException;
import com.aegean.icsd.ontology.queries.InsertQuery;
import com.aegean.icsd.ontology.queries.beans.InsertParam;

@Repository
public class GeneratorDao implements IGeneratorDao {

  static final String HAS_PLAYER = "hasPlayer";
  static final String HAS_LEVEL = "hasLevel";
  static final String HAS_GAME_ID = "hasGameId";

  @Autowired
  private IOntology ontology;

  @Override
  public boolean generateBasicGame(GameInfo info) throws EngineException {
    String prefixedFullGameName = ontology.getPrefixedEntity(Utils.getFullGameName(info.getGameName(), info.getDifficulty()));

    InsertQuery ins = new InsertQuery.Builder()
      .insertEntry(ontology.getPrefixedEntity(info.getId()), prefixedFullGameName)
      .addRelation(InsertParam.createObj(ontology.getPrefixedEntity(HAS_PLAYER)),
        InsertParam.createValue(info.getPlayerName(), info.getPlayerName().getClass()))
      .addRelation(InsertParam.createObj(ontology.getPrefixedEntity(HAS_GAME_ID)),
        InsertParam.createValue(info.getId(), info.getId().getClass()))
      .build();

    try {
      return ontology.insert(ins);
    } catch (OntologyException e) {
      throw DaoExceptions.InsertQuery("Game: " + prefixedFullGameName, e);
    }
  }

  @Override
  public boolean createStringValueRelation(String id, String name, String rangeValue) throws EngineException {
    return createRelation(id, name, rangeValue,false, String.class);
  }

  @Override
  public boolean createValueRelation(String id, String name, Object rangeValue, Class<?> valueClass)
    throws EngineException {
    return createRelation(id, name, rangeValue,false, valueClass);
  }

  @Override
  public boolean createObjRelation(String id, String name, String objId) throws EngineException {
    return createRelation(id, name, objId,true, null);
  }

  @Override
  public boolean instantiateObject(String id, String type) throws EngineException {
    InsertQuery ins = new InsertQuery.Builder()
      .insertEntry(ontology.getPrefixedEntity(id), ontology.getPrefixedEntity(type))
      .build();

    try {
      return ontology.insert(ins);
    } catch (OntologyException e) {
      throw DaoExceptions.InsertQuery("Object: " + id, e);
    }
  }

  @Override
  public Class<?> getJavaClass(String range) {
    return ontology.getJavaClassFromOwlType(range);
  }

  <T> boolean createRelation(String id, String name, Object rangeValue, boolean isObject, Class<T> rangeClass)
    throws EngineException {
    InsertParam rangeParam;
    if (isObject) {
      rangeParam = InsertParam.createObj(ontology.getPrefixedEntity(rangeValue.toString()));
    } else {
      rangeParam = InsertParam.createValue(rangeValue, rangeClass);
    }

    InsertQuery ins = new InsertQuery.Builder()
      .forSubject(InsertParam.createObj(ontology.getPrefixedEntity(id)))
      .addRelation(InsertParam.createObj(ontology.getPrefixedEntity(name)), rangeParam)
      .build();
    try {
      return ontology.insert(ins);
    } catch (OntologyException e) {
      throw DaoExceptions.InsertQuery("Game: " + id, e);
    }
  }
}
