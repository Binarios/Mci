package com.aegean.icsd.mciwebapp.chronorder.implementations;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.mciwebapp.chronorder.beans.ChronologicalOrder;
import com.aegean.icsd.mciwebapp.chronorder.beans.ChronologicalOrderResponse;
import com.aegean.icsd.mciwebapp.chronorder.interfaces.IChronologicalOrderSvc;
import com.aegean.icsd.mciwebapp.common.GameExceptions;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.common.implementations.AbstractGameSvc;
import com.aegean.icsd.mciwebapp.object.beans.Image;
import com.aegean.icsd.mciwebapp.common.beans.ImageData;
import com.aegean.icsd.mciwebapp.object.beans.ProviderException;
import com.aegean.icsd.mciwebapp.object.interfaces.IImageProvider;

@Service
public class ChronologicalOrderSvc extends AbstractGameSvc<ChronologicalOrder, ChronologicalOrderResponse>
  implements IChronologicalOrderSvc {

  @Autowired
  private IRules rules;

  @Autowired
  private IImageProvider imageProvider;

  @Override
  protected void handleDataTypeRestrictions(String fullName, ChronologicalOrder toCreate) throws MciException {
    // no datatype restrictions
  }

  @Override
  protected void handleObjectRestrictions(String fullName, ChronologicalOrder toCreate) throws MciException {
    EntityRestriction hasImageRes;
    try {
      hasImageRes = rules.getEntityRestriction(fullName, "hasOrderedImage");
    } catch (RulesException e) {
      throw GameExceptions.GenerationError(ChronologicalOrder.NAME, e);
    }

    List<Image> images;
    try {
      images = imageProvider.getNewOrderedImagesFor(fullName, hasImageRes.getCardinality());
    } catch (ProviderException e) {
      throw GameExceptions.GenerationError(ChronologicalOrder.NAME, e);
    }

    createObjRelation(toCreate, images, hasImageRes.getOnProperty());
  }

  @Override
  protected boolean isValid(Object solution) {
    List<ImageData> starts = null;
    if (solution != null) {
      starts = ((List<ImageData>) solution).stream()
        .filter(x -> x.isStart() != null && x.isStart())
        .collect(Collectors.toList());
    }
    return starts != null && starts.size() == 1;
  }

  @Override
  protected boolean checkSolution(ChronologicalOrder game, Object solution) throws MciException {
    try {
      List<Image> images = imageProvider.selectImagesByEntityId(game.getId());
      List<String> existingIds = images.stream().map(Image::getId).collect(Collectors.toList());

      List<ImageData> castedSolution = (List<ImageData>)solution;

      List<ImageData> notFound = castedSolution.stream()
        .filter(x -> !existingIds.contains(x.getId()))
        .collect(Collectors.toList());

      if (!notFound.isEmpty()) {
        throw GameExceptions.UnableToSolve(ChronologicalOrder.NAME, "Solution contains item not belonging to" +
          "game with id: " + game.getId());
      }

      return  imageProvider.isSolutionCorrect(castedSolution);
    } catch (ProviderException e) {
      throw GameExceptions.UnableToSolve(ChronologicalOrder.NAME, e);
    }
  }

  @Override
  protected ChronologicalOrderResponse toResponse(ChronologicalOrder toCreate) throws MciException {

    ChronologicalOrderResponse response = new ChronologicalOrderResponse(toCreate);
    List<Image> images;
    try {
      images = imageProvider.selectImagesByEntityId(toCreate.getId());
    } catch (ProviderException e) {
      throw GameExceptions.GenerationError(ChronologicalOrder.NAME, e);
    }
    List<ImageData> imageData = images.stream().map(x -> {
      ImageData res = new ImageData();
      res.setId(x.getId());
      res.setPath(x.getPath());
      return res;
    }).collect(Collectors.toList());
    Collections.shuffle(imageData, new Random(System.currentTimeMillis()));
    response.setImages(imageData);

    return response;
  }
}
