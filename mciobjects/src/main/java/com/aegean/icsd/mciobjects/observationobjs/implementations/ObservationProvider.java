package com.aegean.icsd.mciobjects.observationobjs.implementations;

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
import com.aegean.icsd.mciobjects.common.beans.ProviderException;
import com.aegean.icsd.mciobjects.common.daos.IObjectsDao;
import com.aegean.icsd.mciobjects.common.implementations.ProviderExceptions;
import com.aegean.icsd.mciobjects.images.interfaces.IImageProvider;
import com.aegean.icsd.mciobjects.observationobjs.beans.ObservationObj;
import com.aegean.icsd.mciobjects.observationobjs.interfaces.IObservationProvider;

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

    EntityRestriction imageRes;
    try {
      imageRes = rules.getEntityRestriction(ObservationObj.NAME, "hasImage");
    } catch (RulesException e) {
      throw ProviderExceptions.UnableToRetrieveRules(ObservationObj.NAME, e);
    }

    ObservationObj toCreate = null;
    List<String> imageIds = imageProvider.getImageIds();
    Collections.shuffle(imageIds, new Random(System.currentTimeMillis()));
    for (String imageId : imageIds) {
      List<String> associatedObsObjIds = dao.getIdAssociatedWithOtherOnProperty(imageId, imageRes.getOnProperty());
      if (associatedObsObjIds.isEmpty()) {
        toCreate = new ObservationObj();
        toCreate.setNbOfImages(imageTotalNb);
        try {
          generator.upsertGameObject(toCreate);
          generator.createObjRelation(toCreate.getId(), imageRes.getOnProperty(), imageId);
          break;
        } catch (EngineException e) {
          throw ProviderExceptions.GenerationError(ObservationObj.NAME, e);
        }
      } else {
        for (String id : associatedObsObjIds) {
          ObservationObj criteria = new ObservationObj();
          criteria.setNbOfImages(imageTotalNb);
          criteria.setId(id);
          try {
            List<ObservationObj> objs = generator.selectGameObject(criteria);
            if (objs.isEmpty()) {
              toCreate = new ObservationObj();
              toCreate.setNbOfImages(imageTotalNb);
              generator.upsertGameObject(toCreate);
              generator.createObjRelation(toCreate.getId(), imageRes.getOnProperty(), imageId);
            } else {
              toCreate = objs.get(0);
            }
            break;
          } catch (EngineException e) {
            throw ProviderExceptions.GenerationError(ObservationObj.NAME, e);
          }
        }
        if (toCreate == null) {
          throw ProviderExceptions.UnableToGetObject(String.format("Found association with imageId %s," +
            "but could not select the observationObj associated with", imageId));
        }
      }
    }

    return toCreate;
  }

  @Override
  public List<ObservationObj> selectObservationObjByEntityId(String entityId) throws ProviderException {
    List<String> ids = dao.getAssociatedObjectsOfEntityId(entityId, ObservationObj.class);
    List<ObservationObj> observationObjs = new ArrayList<>();
    for (String id : ids) {
      ObservationObj obj = new ObservationObj();
      obj.setId(id);
      try {
        List<ObservationObj> results = generator.selectGameObject(obj);
        observationObjs.add(results.get(0));
      } catch (EngineException e) {
        throw ProviderExceptions.UnableToGetObject("entityId = " + entityId, e);
      }
    }
    return observationObjs;
  }

}
