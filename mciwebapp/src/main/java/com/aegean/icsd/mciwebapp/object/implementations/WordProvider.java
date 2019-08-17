package com.aegean.icsd.mciwebapp.object.implementations;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.mciwebapp.object.beans.ProviderException;
import com.aegean.icsd.mciwebapp.object.beans.Word;
import com.aegean.icsd.mciwebapp.object.configurations.WordConfiguration;
import com.aegean.icsd.mciwebapp.object.dao.IObjectsDao;
import com.aegean.icsd.mciwebapp.object.interfaces.IObjectFileProvider;
import com.aegean.icsd.mciwebapp.object.interfaces.IWordProvider;
import com.aegean.icsd.ontology.interfaces.IMciModelReader;

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
  private IRules rules;

  @Autowired
  private IMciModelReader model;

  @Autowired
  private IObjectFileProvider fileProvider;


  @Override
  public Word getWordWithValue(String value) throws ProviderException {
    LOGGER.info(String.format("Requested word with value %s", value));
    Word word = toWord(value);
    return getOrUpsertWord(word);
  }

  @Override
  public List<Word> getNewWordsFor(String entityName, int count, Word criteria) throws ProviderException {
    List<String> availableIds = dao.getNewObjectIdsFor(entityName, Word.class);
    if (availableIds.isEmpty()) {
      throw Exceptions.UnableToGenerateObject(Word.NAME);
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
        throw Exceptions.GenerationError(Word.NAME, e);
      }
      if(availableWords.size() == count) {
        break;
      }
    }

    if(availableWords.size() != count) {
      throw Exceptions.UnableToGetObject(String.format("Unable to find %s new words for %s", count, entityName));
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
      throw Exceptions.UnableToGetWord("id = " + wordId, e);
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
        throw Exceptions.UnableToGetWord("entityId = " + entityId, e);
      }
    }
    return words;
  }

  @Override
  public boolean areSynonyms(Word thisWord, Word otherWord) throws ProviderException {
    return dao.areSynonyms(thisWord, otherWord);
  }

  @Override
  public boolean areAntonyms(Word thisWord, Word otherWord) throws ProviderException {
    return dao.areAntonyms(thisWord, otherWord);
  }

  @PostConstruct
  void readWords() throws ProviderException {
    List<String> lines = fileProvider.getLines(config.getLocation() + "/" + config.getFilename());

    for (String line : lines) {
      String[] fragments = line.split(config.getDelimiter());
      String valueRaw = fragments[config.getValueIndex()];

      if (fragments.length == 1) {
        continue;
      }

      String antonymRaw = fragments[config.getAntonymIndex()];
      String synonymRaw = fragments[config.getSynonymIndex()];

      Word value = getWordWithValue(valueRaw);

      if (!StringUtils.isEmpty(antonymRaw)) {
        String[] antonymsRaw = antonymRaw.split(config.getAntonymDelimiter());
        handleAntonyms(value, antonymsRaw);
      }

      if (!StringUtils.isEmpty(synonymRaw)) {
        String[] synonymsRaw = synonymRaw.split(config.getSynonymDelimiter());
        handleSynonyms(value, synonymsRaw);
      }
    }
  }

  void handleAntonyms(Word value, String... antonyms) throws ProviderException {
    EntityRestriction antonymRes;
    try {
      antonymRes = rules.getEntityRestriction("AntonymWord", "hasAntonym");
    } catch (RulesException e) {
      throw Exceptions.UnableToRetrieveRules("AntonymWord", e);
    }

    for (String antonym : antonyms) {
      Word antonymWord = toWord(antonym);
      antonymWord.setAntonym(true);
      getOrUpsertWord(antonymWord);
      if (antonymWord.getId() != null) {
        try {
          generator.createObjRelation(value.getId(), antonymRes.getOnProperty(), antonymWord.getId());
          if (value.isAntonym() == null || !value.isAntonym()) {
            value.setAntonym(true);
            generator.upsertGameObject(value);
          }
        } catch (EngineException e) {
          throw Exceptions.GenerationError(Word.NAME, e);
        }
      }
    }
  }

  void handleSynonyms(Word value, String... synonyms) throws ProviderException {
    EntityRestriction synonymRes;
    try {
      synonymRes = rules.getEntityRestriction("SynonymWord", "hasSynonym");
    } catch (RulesException e) {
      throw Exceptions.UnableToRetrieveRules("SynonymWord", e);
    }

    for (String synonym : synonyms) {
      Word synonymWord = toWord(synonym);
      synonymWord.setSynonym(true);
      synonymWord = getOrUpsertWord(synonymWord);
      if (synonymWord.getId() != null) {
        try {
          generator.createObjRelation(value.getId(), synonymRes.getOnProperty(), synonymWord.getId());
          if (value.isSynonym() == null || !value.isSynonym()) {
            value.setSynonym(true);
            generator.upsertGameObject(value);
          }
        } catch (EngineException e) {
          throw Exceptions.GenerationError(Word.NAME, e);
        }
      }
    }
  }

  Word getOrUpsertWord(Word word) throws ProviderException {
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
      throw Exceptions.GenerationError(Word.NAME, e);
    }
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
