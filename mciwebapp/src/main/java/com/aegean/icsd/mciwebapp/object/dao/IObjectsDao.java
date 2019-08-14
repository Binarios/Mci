package com.aegean.icsd.mciwebapp.object.dao;

import com.aegean.icsd.mciwebapp.object.beans.ProviderException;

public interface IObjectsDao {
  String getNewWordIdFor(String forEntity) throws ProviderException;
}
