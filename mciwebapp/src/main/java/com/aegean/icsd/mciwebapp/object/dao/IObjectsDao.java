package com.aegean.icsd.mciwebapp.object.dao;

import com.aegean.icsd.mciwebapp.object.beans.ProviderException;

public interface IObjectsDao {
  String getWordIdsWithLength(String forEntity, Integer length) throws ProviderException;

  String getWordValue(String wordId) throws ProviderException;
}
