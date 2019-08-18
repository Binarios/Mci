package com.aegean.icsd.mciwebapp.wordpuzzle.implementations;

import java.util.Arrays;
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
import com.aegean.icsd.mciwebapp.common.GameExceptions;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.common.implementations.AbstractGameSvc;
import com.aegean.icsd.mciwebapp.object.beans.ProviderException;
import com.aegean.icsd.mciwebapp.object.beans.Word;
import com.aegean.icsd.mciwebapp.object.interfaces.IWordProvider;
import com.aegean.icsd.mciwebapp.wordpuzzle.beans.WordPuzzle;
import com.aegean.icsd.mciwebapp.wordpuzzle.beans.WordPuzzleResponse;
import com.aegean.icsd.mciwebapp.wordpuzzle.dao.IWordPuzzleDao;
import com.aegean.icsd.mciwebapp.wordpuzzle.interfaces.IWordPuzzleSvc;

@Service
public class WordPuzzleSvc extends AbstractGameSvc<WordPuzzle, WordPuzzleResponse> implements IWordPuzzleSvc {

  private static Logger LOGGER = Logger.getLogger(WordPuzzleSvc.class);

  @Autowired
  private IRules rules;

  @Autowired
  private IGenerator generator;

  @Autowired
  private IWordProvider wordProvider;

  @Autowired
  private IWordPuzzleDao dao;

  @Override
  protected boolean isValid(Object solution) {
    return !StringUtils.isEmpty(solution.toString());
  }

  @Override
  protected boolean checkSolution(WordPuzzle game, Object solution) throws MciException {
    return dao.solveGame(game.getId(), game.getPlayerName(), solution.toString());
  }

  @Override
  protected void handleRestrictions(String fullName, WordPuzzle toCreate) throws MciException {
    Word word;
    try {
      generator.upsertGame(toCreate);
      word = wordProvider.getNewWordFor(fullName, toCreate.getWordLength());
    } catch (EngineException | ProviderException e) {
      throw GameExceptions.GenerationError(WordPuzzle.NAME, e);
    }

    EntityRestriction hasWordRes;
    try {
      hasWordRes = rules.getEntityRestriction(fullName, "hasWord");
      createObjRelation(toCreate, word, hasWordRes.getOnProperty());
    } catch (RulesException e) {
      throw GameExceptions.UnableToRetrieveGameRules(WordPuzzle.NAME, e);
    }
  }

  @Override
  protected WordPuzzleResponse toResponse(WordPuzzle toCreate) throws MciException {
    Word word;
    try{
      word = wordProvider.selectWordsByEntityId(toCreate.getId()).get(0);
    } catch (ProviderException e) {
      throw GameExceptions.FailedToRetrieveWord(WordPuzzle.NAME, toCreate.getId(), e);
    }

    String[] letters = word.getValue().split("");
    List<String> shuffled = Arrays.asList(letters);
    Collections.shuffle(shuffled, new Random(System.currentTimeMillis()));
    WordPuzzleResponse res = new WordPuzzleResponse(toCreate);
    res.setLetters(shuffled);
    return res;
  }

  @Override
  protected void handleDataTypeRestrictions(String fullName, WordPuzzle toCreate) throws MciException {
    EntityRestriction wordLengthRes;
    try {
      wordLengthRes = rules.getEntityRestriction(fullName, "hasWordLength");
    } catch (RulesException e) {
      throw GameExceptions.UnableToRetrieveGameRules(WordPuzzle.NAME, e);
    }

    toCreate.setWordLength(Integer.parseInt(generator.generateLongDataValue(wordLengthRes.getDataRange()).toString()));
  }
}
