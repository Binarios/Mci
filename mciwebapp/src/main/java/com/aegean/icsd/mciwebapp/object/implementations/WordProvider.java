package com.aegean.icsd.mciwebapp.object.implementations;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.mciwebapp.object.beans.ProviderException;
import com.aegean.icsd.mciwebapp.object.beans.WordCriteria;
import com.aegean.icsd.mciwebapp.object.configurations.WordConfiguration;
import com.aegean.icsd.mciwebapp.object.beans.Word;
import com.aegean.icsd.mciwebapp.object.dao.IObjectsDao;
import com.aegean.icsd.mciwebapp.object.interfaces.IObjectFileProvider;
import com.aegean.icsd.mciwebapp.object.interfaces.IWordProvider;

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

  @Override
  public String getWordFromValue(String value) throws ProviderException {
    LOGGER.info(String.format("Requested word with value %s", value));
    Word word = toWord(value);
    try {
      String id = generator.selectObjectId(word);
      if (id == null) {
        generator.upsertObj(word);
      } else {
        word.setId(id);
      }
      return word.getId();
    } catch (EngineException e) {
      throw Exceptions.GenerationError(e);
    }
  }

  @Override
  public String getWordWithCriteria(WordCriteria criteria) throws ProviderException {
    String id;
    if (!StringUtils.isEmpty(criteria.getValue())) {
      id = getWordFromValue(criteria.getValue());
    } else {
      id = dao.getWordIdsWithLength(criteria.getForEntity(), criteria.getLength());
      if (id == null) {
        //todo read file and create
      }
    }
    return id;
  }

  @Override
  public String getWordValue(String wordId) throws ProviderException {
    return dao.getWordValue(wordId);
  }

  Word toWord(String value) {
    Word word = new Word();
    word.setValue(value);
    return word;
  }
}
