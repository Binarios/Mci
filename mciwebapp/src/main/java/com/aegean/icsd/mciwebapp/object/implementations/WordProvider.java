package com.aegean.icsd.mciwebapp.object.implementations;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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
  private IRules rules;

  @Autowired
  private IOntology ont;

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
    List<String> availableIds = dao.getNewWordIdsFor(entityName);
    if (availableIds.size() == 0) {
      throw Exceptions.UnableToGenerateObject(Word.NAME);
    }
    List<Word> availableWords = new ArrayList<>();
    Collections.shuffle(availableIds, new Random(System.currentTimeMillis()));
    for (String id : availableIds) {
      Word cp = copy(criteria);
      cp.setId(id);
      try {
        generator.selectObj(cp);
      } catch (EngineException e) {
        throw Exceptions.GenerationError(Word.NAME, e);
      }
      if (cp.getId() != null) {
        availableWords.add(cp);
      }

      if(availableWords.size() == count) {
        break;
      }
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
    String id = ont.removePrefix(nodeName);
    Word word = new Word();
    word.setId(id);
    try {
      generator.selectObj(word);
    } catch (EngineException e) {
      throw Exceptions.UnableToGetWord("node name = " + nodeName, e);
    }
    return word;
  }

  @Override
  public Word selectWordByWordId(String wordId) throws ProviderException {
    Word word = new Word();
    word.setId(wordId);
    try {
      generator.selectObj(word);
    } catch (EngineException e) {
      throw Exceptions.UnableToGetWord("word id  = " + wordId, e);
    }
    return word;
  }

  @Override
  public List<Word> selectWordsByEntityId(String entityId) throws ProviderException {
    List<String> ids = dao.getAssociatedWordOfId(entityId);
    List<Word> words = new ArrayList<>();
    for (String id : ids) {
      Word word = new Word();
      word.setId(id);
      try {
        generator.selectObj(word);
        words.add(word);
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
    EntityRestriction antonymRes;
    try {
      antonymRes = rules.getEntityRestriction("AntonymWord", "hasAntonym");
    } catch (RulesException e) {
      throw Exceptions.UnableToRetrieveRules("AntonymWord", e);
    }
    EntityRestriction synonymRes;
    try {
      synonymRes = rules.getEntityRestriction("SynonymWord", "hasSynonym");
    } catch (RulesException e) {
      throw Exceptions.UnableToRetrieveRules("SynonymWord", e);
    }

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
        for (String antonymValue : antonymsRaw) {
          Word antonymWord = toWord(antonymValue);
          antonymWord.setAntonym(true);
          getOrUpsertWord(antonymWord);
          if (antonymWord.getId() != null) {
            try {
              generator.createObjRelation(value.getId(), antonymRes.getOnProperty(), antonymWord.getId());
              if (!value.isAntonym()) {
                value.setAntonym(true);
                generator.upsertGameObject(value);
              }
            } catch (EngineException e) {
              throw Exceptions.GenerationError(Word.NAME, e);
            }
          }
        }
      }

      if (!StringUtils.isEmpty(synonymRaw)) {
        String[] synonymsRaw = synonymRaw.split(config.getSynonymDelimiter());
        for (String synonymValue : synonymsRaw) {
          Word synonymWord = toWord(synonymValue);
          synonymWord.setSynonym(true);
          getOrUpsertWord(synonymWord);
          if (synonymWord.getId() != null) {
            try {
              generator.createObjRelation(value.getId(), synonymRes.getOnProperty(), synonymWord.getId());
              if (!value.isSynonym()) {
                value.setSynonym(true);
                generator.upsertGameObject(value);
              }
            } catch (EngineException e) {
              throw Exceptions.GenerationError(Word.NAME, e);
            }
          }
        }
      }
    }
  }

  Word getOrUpsertWord(Word word) throws ProviderException {
    try {
      generator.selectObj(word);
      if (word.getId() == null) {
        generator.upsertGameObject(word);
      }
      return word;
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
