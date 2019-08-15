package com.aegean.icsd.mciwebapp.synonyms.dao;

import com.aegean.icsd.mciwebapp.common.beans.MciException;

public interface ISynonymsDao {
  String getMainWord(String id) throws MciException;
}
