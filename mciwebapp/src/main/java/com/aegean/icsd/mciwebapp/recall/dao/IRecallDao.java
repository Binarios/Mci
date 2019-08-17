package com.aegean.icsd.mciwebapp.recall.dao;

import com.aegean.icsd.mciwebapp.common.beans.MciException;

public interface IRecallDao {
  String getRecallNumberNode(String id) throws MciException;
}
