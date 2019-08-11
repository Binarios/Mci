package com.aegean.icsd.mciwebapp.object.implementations;

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
import com.aegean.icsd.mciwebapp.object.interfaces.IImageProvider;
import com.aegean.icsd.mciwebapp.object.interfaces.IObservationProvider;
import com.aegean.icsd.ontology.queries.SelectQuery;

@Service
public class ObservationProvider implements IObservationProvider {

  private static Logger LOGGER = Logger.getLogger(ObservationProvider.class);

  @Autowired
  private IImageProvider imageProvider;

  @Autowired
  private IGenerator generator;

  @Autowired
  private IRules rules;

  @Override
  public String getObservationId(int totalImageNumber) throws ProviderException {
    LOGGER.info("Creating Observation Object");
    if (totalImageNumber < 0) {
      totalImageNumber = 0;
    }
    ObservationObj obs = new ObservationObj();
    obs.setNbOfImages(totalImageNumber);

    EntityRestriction imageRes;
    try {
      imageRes = rules.getEntityRestriction(ObservationObj.NAME, "hasImage");
    } catch (RulesException e) {
      throw Exceptions.UnableToRetrieveRules(ObservationObj.NAME, e);
    }
    String imageId = imageProvider.getImageId();
    try {
      generator.upsertObj(obs);
      generator.createObjRelation(obs.getId(), imageRes.getOnProperty(), imageId);
    } catch (EngineException e) {
      throw Exceptions.GenerationError(e);
    }

    return obs.getId();
  }
}
