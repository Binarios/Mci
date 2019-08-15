package com.aegean.icsd.mciwebapp.antonyms.dao;

import com.aegean.icsd.mciwebapp.common.beans.MciException;

public interface IAntonymsDao {
  String getMainWord(String id) throws MciException;
}
