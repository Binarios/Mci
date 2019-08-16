package com.aegean.icsd.mciwebapp.observations.implementations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.generator.beans.BaseGameObject;
import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.mciwebapp.common.GameExceptions;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.common.implementations.AbstractGameSvc;
import com.aegean.icsd.mciwebapp.object.beans.ObservationObj;
import com.aegean.icsd.mciwebapp.object.beans.ProviderException;
import com.aegean.icsd.mciwebapp.object.interfaces.IObservationProvider;
import com.aegean.icsd.mciwebapp.observations.beans.Observation;
import com.aegean.icsd.mciwebapp.observations.beans.ObservationItem;
import com.aegean.icsd.mciwebapp.observations.beans.ObservationResponse;
import com.aegean.icsd.mciwebapp.observations.dao.IObservationDao;
import com.aegean.icsd.mciwebapp.observations.interfaces.IObservationSvc;

@Service
public class ObservationSvc extends AbstractGameSvc<Observation, ObservationResponse> implements IObservationSvc {

  private static Logger LOGGER = Logger.getLogger(ObservationSvc.class);

  @Autowired
  private IObservationDao dao;

  @Autowired
  private IRules rules;

  @Autowired
  private IGenerator generator;

  @Autowired
  private IObservationProvider observationProvider;

  @Override
  protected boolean isValid(Object solution) {
    return ((Map)solution).isEmpty();
  }

  @Override
  protected boolean checkSolution(Observation game, Object solution) throws MciException {
    Map<String, Integer> sol = (Map<String, Integer>) solution;
    boolean solved = true;
    for (Map.Entry<String, Integer> subSolution : sol.entrySet()) {
      solved &= dao.solveGame(game.getId(), game.getPlayerName(), subSolution.getKey(), subSolution.getValue());
    }
    return solved;
  }

  @Override
  protected Map<EntityRestriction, List<BaseGameObject>> getRestrictions(String fullName, Observation toCreate)
      throws MciException {

    Map<EntityRestriction, List<BaseGameObject>> restrictions = new HashMap<>();

    EntityRestriction totalImages;
    try {
      totalImages = rules.getEntityRestriction(fullName, "hasTotalImages");
    } catch (RulesException e) {
      throw GameExceptions.UnableToRetrieveGameRules(Observation.NAME, e);
    }

    EntityRestriction hasObservationRes;
    try {
      hasObservationRes = rules.getEntityRestriction(fullName, "hasObservation");
    } catch (RulesException e) {
      throw GameExceptions.UnableToRetrieveGameRules(Observation.NAME, e);
    }

    toCreate.setTotalImages(generator.generateIntDataValue(totalImages.getDataRange()));

    try {
      generator.upsertGame(toCreate);
    } catch (EngineException e) {
      throw GameExceptions.GenerationError(fullName, e);
    }

    List<BaseGameObject> objects = new ArrayList<>();

    int remaining = toCreate.getTotalImages();
    for (int i = 0; i < hasObservationRes.getCardinality(); i++) {
      int nbOfOccurrences;
      if (remaining < 1) {
        nbOfOccurrences = 0;
      } else if (i + 1 >= hasObservationRes.getCardinality()) {
        nbOfOccurrences = remaining;
      } else {
        nbOfOccurrences = ThreadLocalRandom.current().nextInt(0, remaining + 1);
      }
      try {
        ObservationObj obs = observationProvider.getObservation(nbOfOccurrences);
        objects.add(obs);
        remaining -= nbOfOccurrences;
      } catch (ProviderException e) {
        throw  GameExceptions.GenerationError(Observation.NAME, e);
      }
    }
    restrictions.put(hasObservationRes, objects);

    return restrictions;
  }

  @Override
  protected ObservationResponse toResponse(Observation toCreate) throws MciException {
    ObservationResponse resp = new ObservationResponse(toCreate);
    resp.setSolved(!StringUtils.isEmpty(toCreate.getCompletedDate()));
    List<String> words = dao.getAssociatedSubjects(toCreate.getId());
    List<ObservationItem> images = dao.getObservationItems(toCreate.getId());
    if (images != null) {
      resp.setItems(images);
    }
    if (words != null) {
      resp.setWords(words);
    }
    return resp;
  }
}
