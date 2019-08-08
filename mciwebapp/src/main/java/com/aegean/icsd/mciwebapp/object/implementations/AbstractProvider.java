package com.aegean.icsd.mciwebapp.object.implementations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.core.interfaces.IAnnotationReader;
import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.engine.rules.beans.EntityProperty;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.EntityRules;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.mciwebapp.object.beans.ProviderException;
import com.aegean.icsd.mciwebapp.object.interfaces.IObjectProvider;
import com.aegean.icsd.mciwebapp.object.interfaces.IProvider;

abstract class AbstractProvider implements IProvider {

  private IAnnotationReader ano;
  private IRules rules;
  private IGenerator generator;
  private IObjectProvider provider;

  protected abstract Map<EntityProperty, List<String>> handleRestrictions(Object forObject, List<EntityRestriction> restrictions) throws ProviderException;

  protected AbstractProvider(IAnnotationReader ano, IRules rules, IGenerator generator, IObjectProvider provider) {
    this.ano = ano;
    this.rules = rules;
    this.generator = generator;
    this.provider = provider;
  }

  List<String> generateObjects(List<?> objects) throws ProviderException {
    List<String> ids = new ArrayList<>();
    EntityRules er;
    String objId;
    String entityName = "";
    try {
      objId = ano.getEntityId(objects.get(0));
      entityName = ano.getEntityValue(objects.get(0));
      er = rules.getEntityRules(entityName);
    } catch (RulesException | EngineException e) {
      throw Exceptions.UnableToRetrieveRules(entityName, e);
    }
    List<EntityRestriction> simplifiedRestrictions = generator.calculateExactCardinality(er.getRestrictions());

    for (Object object : objects) {
      Map<EntityProperty, List<String>> relations = handleRestrictions(object, simplifiedRestrictions);
      for (EntityRestriction res : simplifiedRestrictions) {
        if (res.getOnProperty().isObjectProperty()) {
          List<String> objIds = provider.getObjectsIds(res.getOnProperty().getRange(), res.getCardinality());
          relations.put(res.getOnProperty(), objIds);
        }
      }
      try {
        generator.upsertObj(object);
        for (Map.Entry<EntityProperty, List<String>> relation : relations.entrySet()) {
          for (String relatedObjId : relation.getValue()) {
            generator.createObjRelation(objId, relation.getKey(), relatedObjId);
          }
        }

        ids.add(objId);
      } catch (EngineException e) {
        throw Exceptions.GenerationError(e);
      }
    }
    return ids;
  }
}
