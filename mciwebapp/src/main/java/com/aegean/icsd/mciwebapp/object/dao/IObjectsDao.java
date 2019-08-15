package com.aegean.icsd.mciwebapp.object.dao;

import java.util.List;

import com.aegean.icsd.mciwebapp.object.beans.ProviderException;

public interface IObjectsDao {

  List<String> getNewWordIdsFor(String forEntity) throws ProviderException;

  List<String> getAssociatedWordOfId(String id) throws ProviderException;
}
