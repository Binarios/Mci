package com.aegean.icsd.mciwebapp.object.implementations;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.aegean.icsd.mciwebapp.common.beans.ImageData;
import com.aegean.icsd.mciwebapp.object.beans.Image;
import com.aegean.icsd.mciwebapp.object.beans.ProviderException;
import com.aegean.icsd.mciwebapp.object.beans.Word;
import com.aegean.icsd.mciwebapp.object.configurations.ImageConfiguration;
import com.aegean.icsd.mciwebapp.object.dao.IImageDao;
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
  private IImageDao imageDao;

  @Autowired
  private IObjectFileProvider fileProvider;

  @Autowired
  private IMciModelReader model;


  @Override
  public List<String> getImageIds() throws ProviderException {
    return dao.getObjectIds(Image.class);
  }

  @Override
  public List<Image> getNewOrderedImagesFor(String entityName, int cardinality) throws ProviderException {
    List<Image> images = new ArrayList<>();

    Map<String, List<String>> orderedImages = imageDao.getOrderedImages(cardinality);
    for (Map.Entry<String, List<String>> entry : orderedImages.entrySet()) {
      String rootId = entry.getKey();
      boolean exists = imageDao.rootOrderImageExistsFor(rootId, entityName);
      if (exists) {
        continue;
      }
      Image criteria = new Image();
      criteria.setId(rootId);
      try {
        Image orderedImage = generator.selectGameObject(criteria).get(0);
        images.add(orderedImage);
        for (String nextImageId : entry.getValue()) {
          criteria.setId(nextImageId);
          Image next = generator.selectGameObject(criteria).get(0);
          images.add(next);
        }

      } catch (EngineException e) {
        throw ProviderExceptions.UnableToGetObject(Image.NAME, e);
      }
      if (images.size() == cardinality) {
        break;
      }
    }

    if (images.isEmpty()) {
      throw ProviderExceptions.UnableToGetObject("Unable to retrieve new ordered Images for this level");
    }
    return images;
  }

  @Override
  public List<Image> selectNewImagesForEntity(String entityName, int count) throws ProviderException {
    List<Image> images = new ArrayList<>();
    try {
      List<String> imageIds = dao.getNewObjectIdsFor(entityName, Image.class);
      for(String imageId : imageIds) {
        if (images.size() == count) {
          break;
        }
        Image criteria = new Image();
        criteria.setId(imageId);
        List<Image> result = generator.selectGameObject(criteria);
        if (!result.isEmpty()) {
          images.addAll(result);
        }
      }
    } catch (ProviderException | EngineException e) {
      throw ProviderExceptions.UnableToGetObject(Image.NAME, e);
    }

    return images;
  }

  @Override
  public boolean isSolutionCorrect(List<ImageData> solution) throws ProviderException {
    solution.sort(Comparator.comparing(ImageData::getOrder));
    return imageDao.isOrderCorrect(solution);
  }

  @Override
  public Image selectImageByNode(String nodeName) throws ProviderException {
    String id = model.removePrefix(nodeName);
    Image criteria = new Image();
    criteria.setId(id);
    try {
      List<Image> images = generator.selectGameObject(criteria);
      if (images.size() != 1) {
        throw ProviderExceptions.UnableToGetObject("No image or more than one image found with the same id :" + id);
      }
      return images.get(0);
    } catch (EngineException e) {
      throw ProviderExceptions.UnableToGetObject("No image or more than one image found with the same id :" + id, e);
    }
  }

  @Override
  public List<Image> selectImagesByEntityId(String entityId) throws ProviderException {
    List<String> ids = dao.getAssociatedObjectOfId(entityId, Image.class);
    List<Image> images = new ArrayList<>();
    for (String id : ids) {
      Image criteria = new Image();
      criteria.setId(id);
      try {
        List<Image> results = generator.selectGameObject(criteria);
        if (results.size() != 1) {
          throw ProviderExceptions.UnableToGetObject("No image or more than one image found with the same id :" + id);
        }
        images.add(results.get(0));
      } catch (EngineException e) {
        throw ProviderExceptions.UnableToGetObject("No image or more than one image found with the same id :" + id, e);
      }
    }
    return images;
  }

  @Override
  public String selectAssociatedSubject(String imageId) throws ProviderException {
    EntityRestriction imageSubjRes;
    try {
      imageSubjRes = rules.getEntityRestriction(Image.NAME, "hasSubject");
    } catch (RulesException e) {
      throw ProviderExceptions.GenerationError(Image.NAME, e);
    }
    List<String> wordId = dao.getAssociatedIdOnProperty(imageId, imageSubjRes.getOnProperty(), Word.class);
    return wordId.get(0);
  }

  @PostConstruct
  void readImages() throws ProviderException {
    List<String> lines = fileProvider.getLines(config.getLocation() + "/" + config.getFilename());
    EntityRestriction imageSubjRes;
    EntityRestriction imageTitleRes;
    EntityRestriction hasPreviousImage;
    try {
      imageSubjRes = rules.getEntityRestriction(Image.NAME, "hasSubject");
      imageTitleRes = rules.getEntityRestriction(Image.NAME, "hasTitle");
      hasPreviousImage = rules.getEntityRestriction("OrderedImage", "hasPreviousImage");
    } catch (RulesException e) {
      throw ProviderExceptions.GenerationError(Image.NAME, e);
    }

    for (String line : lines) {
      createImages(line, lines, imageSubjRes, imageTitleRes, hasPreviousImage);
    }
  }

  Image createImages(String currentLine, List<String> originalLines,
                     EntityRestriction imageSubjRes,
                     EntityRestriction imageTitleRes,
                     EntityRestriction hasPreviousImage) throws ProviderException {

    List<String> lines = new ArrayList<>(originalLines);
    lines.remove(currentLine);
    Image criteria = new Image();
    Image parentImage = null;

    String[] currentFragments = currentLine.split(config.getDelimiter());
    String currentUrl = currentFragments[config.getUrlIndex()];
    String currentTitle = currentFragments[config.getTitleIndex()];
    String currentSubject = currentFragments[config.getSubjectIndex()];

    if (currentFragments.length > config.getParentIndex()) {
      String currentParentImage = currentFragments[config.getParentIndex()];
      String foundParentLine = lines.stream()
        .filter(line -> {
          String[] fragments = line.split(config.getDelimiter());
          String url = fragments[config.getUrlIndex()];
          return !StringUtils.isEmpty(url) && url.equals(currentParentImage);
        })
        .findFirst()
        .orElse(null);

      if (!StringUtils.isEmpty(foundParentLine)) {
        parentImage = createImages(foundParentLine, originalLines, imageSubjRes, imageTitleRes, hasPreviousImage );
      }
    }

    try {
      criteria.setOrdered(parentImage != null && !StringUtils.isEmpty(parentImage.getId()));
      criteria.setPath(currentUrl);
      Image image = getOrUpsertImage(criteria);
      Word titleWord = wordProvider.getWordWithValue(currentTitle);
      Word subjectWord = wordProvider.getWordWithValue(currentSubject);
      generator.createObjRelation(image.getId(), imageTitleRes.getOnProperty(), titleWord.getId());
      generator.createObjRelation(image.getId(), imageSubjRes.getOnProperty(), subjectWord.getId());
      if (image.isOrdered()) {
        generator.createObjRelation(image.getId(), hasPreviousImage.getOnProperty(), parentImage.getId());
      }
      return image;
    } catch (EngineException e) {
      throw ProviderExceptions.GenerationError(Image.NAME, e);
    }
  }

  Image getOrUpsertImage(Image image) throws ProviderException {
    try {
      List<Image> results = generator.selectGameObject(image);
      if (results.isEmpty()) {
        generator.upsertGameObject(image);
        return image;
      } else {
        List<Image> found = results.stream()
          .filter(x -> x.getPath().equals(image.getPath()))
          .collect(Collectors.toList());
        return found.get(0);
      }
    } catch (EngineException e) {
      throw ProviderExceptions.GenerationError(Image.NAME, e);
    }
  }
}
