package com.aegean.icsd.mciwebapp.object.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aegean.icsd.mciwebapp.common.beans.ImageData;
import com.aegean.icsd.mciwebapp.object.beans.Image;
import com.aegean.icsd.mciwebapp.object.beans.ProviderException;
import com.aegean.icsd.ontology.beans.OntologyException;
import com.aegean.icsd.ontology.interfaces.IMciModelReader;
import com.aegean.icsd.ontology.interfaces.IOntologyConnector;
import com.aegean.icsd.ontology.queries.AskQuery;
import com.aegean.icsd.ontology.queries.SelectQuery;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Repository
public class ImageDao implements IImageDao {

  @Autowired
  private IMciModelReader model;

  @Autowired
  private IOntologyConnector ont;

  public Map<String, List<String>> getOrderedImages(int nb) throws ProviderException {
    SelectQuery.Builder qBuilder = new SelectQuery.Builder();

    String[] params = new String[nb];

    for (int i = 0; i <nb; i++) {
      if (i == 0) {
        params[i] = "rootId";
        qBuilder.where("s" + i,  "hasChronologicalOrder", "child");
        qBuilder.addIriParam("hasChronologicalOrder", model.getPrefixedEntity("hasChronologicalOrder"));
        qBuilder.addLiteralParam("child", false);
      } else {
        params[i] = "id" + i;
        qBuilder.where("s" + i,  "hasPreviousImage", "s" + (i -1));
        qBuilder.addIriParam("hasPreviousImage", model.getPrefixedEntity("hasPreviousImage"));
      }
      qBuilder.where("s" + i,  "hasId", params[i]);
      qBuilder.addIriParam("hasId", model.getPrefixedEntity("hasId"));
    }

    qBuilder.select(params);

    try {
      JsonArray result = ont.select(qBuilder.build());
      Map<String, List<String>> orderedImages = new HashMap<>();
      if (result.size() > 0) {
        for (JsonElement element : result) {
          List<String> imagesFlow = new ArrayList<>();
          JsonObject obj = element.getAsJsonObject();
          String rootId = null;
          for (String param : params) {
            if ("rootId".equals(param)) {
              rootId = obj.get(param).getAsString();
            } else {
              imagesFlow.add(obj.get(param).getAsString());
            }
          }
          orderedImages.put(rootId, imagesFlow);
        }
      }
      return orderedImages;
    } catch (OntologyException e) {
      throw Exceptions.FailedToRetrieveObjects(Image.NAME, e);
    }
  }

  @Override
  public boolean rootOrderImageExistsFor(String rootImageId, String entityName) throws ProviderException {
    AskQuery q = new AskQuery.Builder()
      .is("s", "hasId", "sId")
      .is("s", "type", "entityName")
      .is("s", "orderedImage", "orderedImage")
      .is("orderedImage", "hasId", "rootId")
      .is("orderedImage", "hasChronologicalOrder", "no")
      .addIriParam("hasId", model.getPrefixedEntity("hasId"))
      .addIriParam("hasChronologicalOrder", model.getPrefixedEntity("hasChronologicalOrder"))
      .addIriParam("entityName", model.getPrefixedEntity(entityName))
      .addLiteralParam("rootId", rootImageId)
      .addLiteralParam("no", false)
      .build();

    try {
      return ont.ask(q);
    } catch (OntologyException e) {
      throw Exceptions.FailedToAsk(String.format("Could not ask if image with id %s has been associated with " +
        "a game of type %s", rootImageId, entityName), e);
    }
  }

  @Override
  public boolean isOrderCorrect(List<ImageData> solution) throws ProviderException {
    AskQuery.Builder qBuilder = new AskQuery.Builder();
    int i = 0;
    for (ImageData img : solution) {
      qBuilder.is("s" + i, "hasId", "id" + i)
        .addIriParam("hasId", model.getPrefixedEntity("hasId"))
        .addLiteralParam("id" + i, img.getId());

      if (!img.isStart()) {
        qBuilder.is("s" + i, "hasPreviousImage", "s" + (i-1));
        qBuilder.addIriParam("hasPreviousImage", model.getPrefixedEntity("hasPreviousImage"));
      }

      i++;
    }

    try {
      return ont.ask(qBuilder.build());
    } catch (OntologyException e) {
      throw Exceptions.FailedToAsk(Image.NAME, e);
    }
  }
}
