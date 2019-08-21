package com.aegean.icsd.mciobjects.common.daos;

import java.util.List;

import com.aegean.icsd.engine.generator.beans.BaseGameObject;
import com.aegean.icsd.engine.rules.beans.EntityProperty;
import com.aegean.icsd.mciobjects.common.beans.ProviderException;

public interface IObjectsDao {

  <T extends BaseGameObject> List<String> getObjectIds(Class<T> object) throws ProviderException;

  <T extends BaseGameObject> List<String> getNewObjectIdsFor(String forEntity, Class<T> object) throws ProviderException;

  <T extends BaseGameObject> List<String> getAssociatedObjectOfId(String id, Class<T> object) throws ProviderException;

  <T extends BaseGameObject> List<String> getAssociatedIdOnProperty(String id, EntityProperty onProperty, Class<T> object) throws ProviderException;

  <T extends BaseGameObject> boolean areObjectsAssociatedOn(T thisObj, T thatObj, EntityProperty onProperty) throws ProviderException;

  List<String> getIdAssociatedWithOtherOnProperty(String otherId, EntityProperty onProperty) throws ProviderException;

  List<String> getIdAssociatedWithOtherOnProperty(String thisType, String otherType, String otherId, EntityProperty onProperty) throws ProviderException;

}
