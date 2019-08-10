package com.aegean.icsd.mciwebapp.object.implementations;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.mciwebapp.common.Utils;
import com.aegean.icsd.mciwebapp.object.beans.Image;
import com.aegean.icsd.mciwebapp.object.beans.ProviderException;
import com.aegean.icsd.mciwebapp.object.configurations.ImageConfiguration;
import com.aegean.icsd.mciwebapp.object.interfaces.IImageProvider;
import com.aegean.icsd.mciwebapp.object.interfaces.IWordProvider;

@Service
public class ImageProvider implements IImageProvider {

  @Autowired
  private ImageConfiguration config;

  @Autowired
  private IGenerator generator;

  @Autowired
  private IRules rules;

  @Autowired
  private IWordProvider wordProvider;

  @Autowired
  private Utils utils;

  @Override
  public String getImageId() throws ProviderException {
    Image img = new Image();

    String line;
    try {
      line = utils.getFileLine(config.getLocation());
    } catch (IOException e) {
      throw Exceptions.UnableToReadFile(config.getLocation(), e);
    }

    String[] fragments = line.split(config.getDelimiter());
    String url = fragments[config.getUrlIndex()];
    img.setPath(url);

    EntityRestriction imageSubjRes;
    EntityRestriction imageTitleRes;
    try {
      imageSubjRes = rules.getEntityRestriction(Image.NAME, "hasImageSubject");
      imageTitleRes = rules.getEntityRestriction(Image.NAME, "hasImageTitle");
    } catch (RulesException e) {
      throw Exceptions.GenerationError(e);
    }

    String title = fragments[config.getTitleIndex()];
    String subject = fragments[config.getSubjectIndex()];

    try {
      String id = generator.selectObjectId(img);
      if (id == null) {
        generator.upsertObj(img);
      } else {
        img.setId(id);
      }

      String titleId = wordProvider.getWordFromValue(title);
      String subjectId = wordProvider.getWordFromValue(subject);
      generator.createObjRelation(img.getId(), imageTitleRes.getOnProperty(), titleId);
      generator.createObjRelation(img.getId(), imageSubjRes.getOnProperty(), subjectId);
      return img.getId();
    } catch (EngineException e) {
      throw Exceptions.GenerationError(e);
    }
  }

  private String extractNameFromUrl(String url) {
    String unixFormat = url.replace("\\", "/");
    String[] fragments = unixFormat.split("/");
    String name = fragments[fragments.length - 1];
    if (name.contains(".")) {
      String[] nameTypeFragments = name.split("\\.");
      List<String> nameFragments = Arrays.asList(nameTypeFragments).subList(0, nameTypeFragments.length - 1);
      name = String.join(".", nameFragments);
    }
    return name;
  }
}
