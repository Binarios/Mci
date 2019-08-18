package com.aegean.icsd.mciwebapp.recall.dao;

import com.aegean.icsd.mciwebapp.common.beans.MciException;

public interface IRecallDao {
  boolean existsWithRecallNumber(Long recallNumber) throws MciException;

  Long getRecallNumber(String id) throws MciException;
}
