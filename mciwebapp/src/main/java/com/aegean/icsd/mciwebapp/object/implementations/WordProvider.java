package com.aegean.icsd.mciwebapp.object.implementations;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.mciwebapp.object.beans.ProviderException;
import com.aegean.icsd.mciwebapp.object.configurations.WordConfiguration;
import com.aegean.icsd.mciwebapp.object.beans.Word;
import com.aegean.icsd.mciwebapp.object.dao.IObjectsDao;
import com.aegean.icsd.mciwebapp.object.interfaces.IObjectFileProvider;
import com.aegean.icsd.mciwebapp.object.interfaces.IWordProvider;
import com.aegean.icsd.ontology.IOntology;

@Service
public class WordProvider implements IWordProvider {

  private static Logger LOGGER = Logger.getLogger(WordProvider.class);

  @Autowired
  private WordConfiguration config;

  @Autowired
  private IObjectsDao dao;

  @Autowired
  private IGenerator generator;

  @Autowired
  private IObjectFileProvider fileProvider;

  @Autowired
  private IOntology ont;

  @Override
  public Word getWordWithValue(String value) throws ProviderException {
    LOGGER.info(String.format("Requested word with value %s", value));
    Word word = toWord(value);
    try {
      generator.selectObj(word);
      if (word.getId() == null) {
        generator.upsertObj(word);
      }
      return word;
    } catch (EngineException e) {
      throw Exceptions.GenerationError(e);
    }
  }

  @Override
  public Word getNewWordFor(String entityName, int length) throws ProviderException {
    String id = dao.getNewWordIdFor(entityName);
    Word word;
    if (StringUtils.isEmpty(id)) {
      word = readWord(length);
    } else {
      word = new Word();
      word.setId(id);
      try {
        generator.selectObj(word);
      } catch (EngineException e) {
        throw Exceptions.GenerationError(e);
      }
    }
    return word;
  }

  @Override
  public Word getWordFromNode(String wordNode) throws ProviderException {
    String id = ont.removePrefix(wordNode);
    Word word = new Word();
    word.setId(id);

    try {
      generator.selectObj(word);
    } catch (EngineException e) {
      throw Exceptions.GenerationError(e);
    }

    return word;
  }

  Word readWord(int length) throws ProviderException {
    Word word = null;
    List<String> lines = fileProvider.getLines(config.getLocation() + "/" + config.getFilename());
    for (String line : lines) {
      String[] fragments = line.split(config.getDelimiter());
      for (String value : fragments) {
        if (value.length() == length) {
          word = getWordWithValue(value);
          break;
        }
        if (word != null) {
          break;
        }
      }
    }
    if (word == null) {
      throw Exceptions.UnableToGenerateObject(Word.NAME);
    }
    return word;
  }

  Word toWord(String value) {
    Word word = new Word();
    word.setValue(value);
    word.setLength(value.length());
    return word;
  }
}
