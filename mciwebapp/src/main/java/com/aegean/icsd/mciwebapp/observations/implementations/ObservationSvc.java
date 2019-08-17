package com.aegean.icsd.mciwebapp.observations.implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.mciwebapp.common.GameExceptions;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.common.implementations.AbstractGameSvc;
import com.aegean.icsd.mciwebapp.object.beans.Image;
import com.aegean.icsd.mciwebapp.object.beans.ObservationObj;
import com.aegean.icsd.mciwebapp.object.beans.ProviderException;
import com.aegean.icsd.mciwebapp.object.beans.Word;
import com.aegean.icsd.mciwebapp.object.interfaces.IImageProvider;
import com.aegean.icsd.mciwebapp.object.interfaces.IObservationProvider;
import com.aegean.icsd.mciwebapp.object.interfaces.IWordProvider;
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

  @Autowired
  private IImageProvider imageProvider;

  @Autowired
  private IWordProvider wordProvider;

  @Override
  protected boolean isValid(Object solution) {
    return !((Map)solution).isEmpty();
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
  protected void handleRestrictions(String fullName, Observation toCreate)
      throws MciException {

    EntityRestriction hasObservationRes;
    try {
      hasObservationRes = rules.getEntityRestriction(fullName, "hasObservation");
    } catch (RulesException e) {
      throw GameExceptions.UnableToRetrieveGameRules(Observation.NAME, e);
    }

    List<ObservationObj> objects = new ArrayList<>();

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

    createObjRelation(toCreate, objects, hasObservationRes.getOnProperty());
  }

  @Override
  protected ObservationResponse toResponse(Observation toCreate) throws MciException {
    ObservationResponse resp = new ObservationResponse(toCreate);
    resp.setSolved(!StringUtils.isEmpty(toCreate.getCompletedDate()));
    List<String> words = new ArrayList<>();
    List<ObservationItem> items = new ArrayList<>();
    try {
      List<ObservationObj> observationObjs = observationProvider.selectObservationObjByEntityId(toCreate.getId());
      for (ObservationObj obj : observationObjs) {
        ObservationItem item = new ObservationItem();
        item.setTotalInstances(obj.getNbOfImages());
        List<Image> associatedImages = imageProvider.selectImagesByEntityId(obj.getId());
        item.setImage(associatedImages.get(0).getPath());
        String subjectId = imageProvider.selectAssociatedSubject(associatedImages.get(0).getId());
        Word associatedWord = wordProvider.selectWordById(subjectId);

        words.add(associatedWord.getValue());
        items.add(item);
      }
    } catch (ProviderException e) {
      throw GameExceptions.GenerationError(toCreate.getId(), "Error when constructing the response");
    }
    resp.setWords(words);
    resp.setItems(items);
    return resp;
  }

  @Override
  protected void handleDataTypeRestrictions(String fullName, Observation toCreate) throws MciException {
    EntityRestriction totalImages;
    try {
      totalImages = rules.getEntityRestriction(fullName, "hasTotalImages");
      toCreate.setTotalImages(generator.generateIntDataValue(totalImages.getDataRange()));
    } catch (RulesException e) {
      throw GameExceptions.UnableToRetrieveGameRules(Observation.NAME, e);
    }
  }
}
