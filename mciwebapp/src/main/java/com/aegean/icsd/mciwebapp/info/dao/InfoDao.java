package com.aegean.icsd.mciwebapp.info.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aegean.icsd.ontology.interfaces.IMciModelReader;

@Repository
public class InfoDao implements IInfoDao {

  @Autowired
  private IMciModelReader model;

  @Override
  public List<String> getGames() {
    return model.getClassChildren("Game");
  }
}
