package com.aegean.icsd.mciobjects.words.implementations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.mciobjects.common.beans.ProviderException;
import com.aegean.icsd.mciobjects.common.daos.IObjectsDao;
import com.aegean.icsd.mciobjects.common.implementations.ProviderExceptions;
import com.aegean.icsd.mciobjects.words.beans.Word;
import com.aegean.icsd.mciobjects.words.daos.IWordDao;
import com.aegean.icsd.mciobjects.words.interfaces.IWordProvider;
import com.aegean.icsd.ontology.interfaces.IMciModelReader;

@Service
public class WordProvider implements IWordProvider {

  private static Logger LOGGER = Logger.getLogger(WordProvider.class);

  @Autowired
  private IObjectsDao dao;

  @Autowired
  private IWordDao wordDao;

  @Autowired
  private IGenerator generator;

  @Autowired
  private IRules rules;

  @Autowired
  private IMciModelReader model;

  @Override
  public Word getWordWithValue(String value) throws ProviderException {
    LOGGER.info(String.format("Requested word with value %s", value));
    Word word = toWord(value);
    try {
      List<Word> results = generator.selectGameObject(word);
      if (results.isEmpty()) {
        generator.upsertGameObject(word);
        return word;
      } else {
        List<Word> found = results.stream()
          .filter(x -> x.getValue().equals(word.getValue()))
          .collect(Collectors.toList());
        return found.get(0);
      }
    } catch (EngineException e) {
      throw ProviderExceptions.GenerationError(Word.NAME, e);
    }
  }

  @Override
  public List<Word> getNewWordsFor(String entityName, int count, Word criteria) throws ProviderException {
    List<String> availableIds = dao.getNewObjectIdsFor(entityName, Word.class);
    if (availableIds.isEmpty()) {
      throw ProviderExceptions.UnableToGenerateObject(Word.NAME);
    }
    List<Word> availableWords = new ArrayList<>();
    Collections.shuffle(availableIds, new Random(System.currentTimeMillis()));
    for (String id : availableIds) {
      Word cp = copy(criteria);
      cp.setId(id);
      try {
        List<Word> results = generator.selectGameObject(cp);
        if (!results.isEmpty()) {
          availableWords.add(results.get(0));
        }
      } catch (EngineException e) {
        throw ProviderExceptions.GenerationError(Word.NAME, e);
      }
      if(availableWords.size() == count) {
        break;
      }
    }

    if(availableWords.size() != count) {
      throw ProviderExceptions.UnableToGetObject(String.format("Unable to find %s new words for %s", count, entityName));
    }

    return availableWords;
  }

  @Override
  public Word getNewWordFor(String entityName, int length) throws ProviderException {
    Word criteria = new Word();
    criteria.setLength(length);
    return getNewWordsFor(entityName, 1, criteria).get(0);
  }

  @Override
  public Word selectWordByNode(String nodeName) throws ProviderException {
    String id = model.removePrefix(nodeName);
   return selectWordById(id);
  }

  @Override
  public Word selectWordById(String wordId) throws ProviderException {
    Word word = new Word();
    word.setId(wordId);
    try {
      List<Word> results = generator.selectGameObject(word);
      return results.get(0);
    } catch (EngineException e) {
      throw ProviderExceptions.UnableToGetWord("id = " + wordId, e);
    }
  }

  @Override
  public List<Word> selectWordsByEntityId(String entityId) throws ProviderException {
    List<String> ids = dao.getAssociatedObjectOfId(entityId, Word.class);
    List<Word> words = new ArrayList<>();
    for (String id : ids) {
      Word word = new Word();
      word.setId(id);
      try {
        List<Word> results = generator.selectGameObject(word);
        words.add(results.get(0));
      } catch (EngineException e) {
        throw ProviderExceptions.UnableToGetWord("entityId = " + entityId, e);
      }
    }
    return words;
  }

  @Override
  public boolean areSynonyms(Word thisWord, Word otherWord) throws ProviderException {
    return wordDao.areSynonyms(thisWord, otherWord);
  }

  @Override
  public boolean areAntonyms(Word thisWord, Word otherWord) throws ProviderException {
    return wordDao.areAntonyms(thisWord, otherWord);
  }

  Word toWord(String value) {
    Word word = new Word();
    word.setValue(value);
    word.setLength(value.length());
    return word;
  }

  Word copy(Word word) {
    Word cp = new Word();
    cp.setId(word.getId());
    cp.setLength(word.getLength());
    cp.setValue(word.getValue());
    cp.setAntonym(word.isAntonym());
    cp.setSynonym(word.isSynonym());
    return cp;
  }
}
