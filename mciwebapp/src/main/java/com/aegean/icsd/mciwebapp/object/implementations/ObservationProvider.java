package com.aegean.icsd.mciwebapp.object.implementations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.mciwebapp.object.beans.Image;
import com.aegean.icsd.mciwebapp.object.beans.ObservationObj;
import com.aegean.icsd.mciwebapp.object.beans.ProviderException;
import com.aegean.icsd.mciwebapp.object.dao.IObjectsDao;
import com.aegean.icsd.mciwebapp.object.interfaces.IImageProvider;
import com.aegean.icsd.mciwebapp.object.interfaces.IObservationProvider;

@Service
public class ObservationProvider implements IObservationProvider {

  private static Logger LOGGER = Logger.getLogger(ObservationProvider.class);

  @Autowired
  private IImageProvider imageProvider;

  @Autowired
  private IGenerator generator;

  @Autowired
  private IRules rules;

  @Autowired
  private IObjectsDao dao;

  @Override
  public ObservationObj getObservation(int totalImageNumber) throws ProviderException {
    LOGGER.info("Creating Observation Object");
    int imageTotalNb = totalImageNumber;
    if (imageTotalNb < 0) {
      imageTotalNb = 0;
    }
    ObservationObj obs = new ObservationObj();
    obs.setNbOfImages(imageTotalNb);

    EntityRestriction imageRes;
    try {
      imageRes = rules.getEntityRestriction(ObservationObj.NAME, "hasImage");
    } catch (RulesException e) {
      throw Exceptions.UnableToRetrieveRules(ObservationObj.NAME, e);
    }
    Image image = imageProvider.getImage();
    try {
      generator.upsertGameObject(obs);
      generator.createObjRelation(obs.getId(), imageRes.getOnProperty(), image.getId());
    } catch (EngineException e) {
      throw Exceptions.GenerationError(ObservationObj.NAME, e);
    }

    return obs;
  }

  @Override
  public ObservationObj getNewObservationFor(String entityName, ObservationObj criteria)
      throws ProviderException {

    List<String> availableIds = dao.getNewObservationIdsFor(entityName);
    if (availableIds.isEmpty()) {
      throw Exceptions.UnableToGenerateObject(ObservationObj.NAME);
    }
    ObservationObj availableObject = new ObservationObj();
    Collections.shuffle(availableIds, new Random(System.currentTimeMillis()));
    for (String id : availableIds) {
      ObservationObj cp = copy(criteria);
      cp.setId(id);
      try {
        generator.selectObj(cp);
      } catch (EngineException e) {
        throw Exceptions.GenerationError(ObservationObj.NAME, e);
      }
      if (cp.getId() != null) {
        availableObjects.add(cp);
      }
    }

    return availableObject;
  }
}
