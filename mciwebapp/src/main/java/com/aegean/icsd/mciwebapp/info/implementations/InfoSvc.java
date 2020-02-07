package com.aegean.icsd.mciwebapp.info.implementations;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.mciwebapp.info.dao.IInfoDao;
import com.aegean.icsd.mciwebapp.info.interfaces.IInfoSvc;

@Service
public class InfoSvc implements IInfoSvc {

  @Autowired
  private IInfoDao dao;

  @Override
  public List<String> getGames() {
    return dao.getGames();
  }
}
