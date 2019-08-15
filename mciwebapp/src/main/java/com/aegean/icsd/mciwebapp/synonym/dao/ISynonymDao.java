package com.aegean.icsd.mciwebapp.synonym.dao;

import com.aegean.icsd.mciwebapp.common.beans.MciException;

public interface ISynonymDao {
  String getMainWord(String id) throws MciException;
}
