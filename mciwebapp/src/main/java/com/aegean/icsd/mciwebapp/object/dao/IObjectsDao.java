package com.aegean.icsd.mciwebapp.object.dao;

import java.util.List;

import com.aegean.icsd.engine.generator.beans.BaseGameObject;
import com.aegean.icsd.engine.rules.beans.EntityProperty;
import com.aegean.icsd.mciwebapp.object.beans.ProviderException;
import com.aegean.icsd.mciwebapp.object.beans.Word;

public interface IObjectsDao {

  <T extends BaseGameObject> List<String> getObjectIds(Class<T> object) throws ProviderException;

  <T extends BaseGameObject> List<String> getNewObjectIdsFor(String forEntity, Class<T> object) throws ProviderException;

  <T extends BaseGameObject> List<String> getAssociatedObjectOfId(String id, Class<T> object) throws ProviderException;

  List<String> getIdAssociatedWithOtherOnProperty (String otherId, EntityProperty onProperty) throws ProviderException;

  boolean areSynonyms(Word thisWord, Word otherWord) throws ProviderException;

  boolean areAntonyms(Word thisWord, Word otherWord) throws ProviderException;
}
