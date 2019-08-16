package com.aegean.icsd.mciwebapp.object.implementations;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.mciwebapp.object.beans.Image;
import com.aegean.icsd.mciwebapp.object.beans.ProviderException;
import com.aegean.icsd.mciwebapp.object.beans.Word;
import com.aegean.icsd.mciwebapp.object.configurations.ImageConfiguration;
import com.aegean.icsd.mciwebapp.object.dao.IObjectsDao;
import com.aegean.icsd.mciwebapp.object.interfaces.IImageProvider;
import com.aegean.icsd.mciwebapp.object.interfaces.IObjectFileProvider;
import com.aegean.icsd.mciwebapp.object.interfaces.IWordProvider;
import com.aegean.icsd.ontology.interfaces.IMciModelReader;

@Service
public class ImageProvider implements IImageProvider {

  private static Logger LOGGER = Logger.getLogger(ImageProvider.class);

  @Autowired
  private ImageConfiguration config;

  @Autowired
  private IGenerator generator;

  @Autowired
  private IRules rules;

  @Autowired
  private IWordProvider wordProvider;

  @Autowired
  private IObjectsDao dao;

  @Autowired
  private IObjectFileProvider fileProvider;

  @Autowired
  private IMciModelReader model;

  @Override
  public Image selectImageByNode(String nodeName) throws ProviderException {
    String id = model.removePrefix(nodeName);
    Image img = new Image();
    img.setId(id);
    try {
      generator.selectObj(img);
    } catch (EngineException e) {
      throw Exceptions.UnableToGetWord("node name = " + nodeName, e);
    }
    return img;
  }

  @Override
  public List<Image> selectImagesByEntityId(String entityId) throws ProviderException {
    List<String> ids = dao.getAssociatedImagesOfId(entityId);
    List<Image> images = new ArrayList<>();
    for (String id : ids) {
      Image image = new Image();
      image.setId(id);
      try {
        generator.selectObj(image);
        images.add(image);
      } catch (EngineException e) {
        throw Exceptions.UnableToGetWord("entityId = " + entityId, e);
      }
    }
    return images;
  }


  @PostConstruct
  void readImages() throws ProviderException {
    List<String> lines = fileProvider.getLines(config.getLocation() + "/" + config.getFilename());
    EntityRestriction imageSubjRes;
    EntityRestriction imageTitleRes;
    try {
      imageSubjRes = rules.getEntityRestriction(Image.NAME, "hasImageSubject");
      imageTitleRes = rules.getEntityRestriction(Image.NAME, "hasImageTitle");
    } catch (RulesException e) {
      throw Exceptions.GenerationError(Image.NAME, e);
    }

    for (String line : lines) {
      String[] fragments = line.split(config.getDelimiter());
      String url = fragments[config.getUrlIndex()];
      Image img = new Image();
      img.setPath(url);
      String title = fragments[config.getTitleIndex()];
      String subject = fragments[config.getSubjectIndex()];

      try {
        generator.selectObj(img);
        if (img.getId() == null) {
          generator.upsertGameObject(img);
        }

        Word titleWord = wordProvider.getWordWithValue(title);
        Word subjectWord = wordProvider.getWordWithValue(subject);
        generator.createObjRelation(img.getId(), imageTitleRes.getOnProperty(), titleWord.getId());
        generator.createObjRelation(img.getId(), imageSubjRes.getOnProperty(), subjectWord.getId());
      } catch (EngineException e) {
        throw Exceptions.GenerationError(Image.NAME, e);
      }
    }
  }
}
