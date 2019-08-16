package com.aegean.icsd.mciwebapp.object.implementations;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.interfaces.IRules;
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

    EntityRestriction imageRes;
    try {
      imageRes = rules.getEntityRestriction(ObservationObj.NAME, "hasImage");
    } catch (RulesException e) {
      throw Exceptions.UnableToRetrieveRules(ObservationObj.NAME, e);
    }

    ObservationObj criteria = new ObservationObj();
    criteria.setNbOfImages(imageTotalNb);
    List<ObservationObj> existing;
    try {
      existing = generator.selectGameObject(criteria);
    } catch (EngineException e) {
      throw Exceptions.GenerationError(ObservationObj.NAME, e);
    }

    List<String> imageIds = imageProvider.getImageIds();
    ObservationObj obs = null;
    String newImageId = null;
    for (String imageId : imageIds) {
      ObservationObj found = existing.stream()
        .filter(obj -> imageId.equals(obj.getId()))
        .findFirst()
        .orElse(null);

      if (found == null) {
        obs = new ObservationObj();
        obs.setNbOfImages(totalImageNumber);
        newImageId = imageId;
        break;
      }
    }

    if (obs == null || StringUtils.isEmpty(newImageId)) {
      throw Exceptions.UnableToGenerateObject(ObservationObj.NAME);
    }

    try {
      generator.upsertGameObject(obs);
      generator.createObjRelation(obs.getId(), imageRes.getOnProperty(), newImageId);
    } catch (EngineException e) {
      throw Exceptions.GenerationError(ObservationObj.NAME, e);
    }

    return obs;
  }

}
