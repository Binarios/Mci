package com.aegean.icsd.mciwebapp.common.implementations;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.aegean.icsd.engine.common.Utils;
import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.common.beans.EngineException;
import com.aegean.icsd.engine.core.interfaces.IAnnotationReader;
import com.aegean.icsd.engine.generator.beans.BaseGame;
import com.aegean.icsd.engine.generator.beans.BaseGameObject;
import com.aegean.icsd.engine.generator.interfaces.IGenerator;
import com.aegean.icsd.engine.rules.beans.EntityProperty;
import com.aegean.icsd.engine.rules.beans.EntityRestriction;
import com.aegean.icsd.engine.rules.beans.RulesException;
import com.aegean.icsd.engine.rules.interfaces.IRules;
import com.aegean.icsd.mciwebapp.common.GameExceptions;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.common.beans.ServiceResponse;
import com.aegean.icsd.mciwebapp.common.interfaces.IGameService;
import com.aegean.icsd.mciwebapp.synonyms.beans.Synonyms;

public abstract class AbstractGameSvc <T extends BaseGame, R extends ServiceResponse<T>>
    implements IGameService<T, R> {

  private static Logger LOGGER = Logger.getLogger(AbstractGameSvc.class);

  @Autowired
  private IGenerator generator;

  @Autowired
  private IAnnotationReader ano;

  @Autowired
  private IRules rules;

  protected abstract void handleDataTypeRestrictions(String fullName, T toCreate) throws MciException;
  protected abstract void handleRestrictions(String fullName, T toCreate) throws MciException;
  protected abstract boolean isValid(Object solution);
  protected abstract boolean checkSolution(T game, Object solution) throws MciException;
  protected abstract R toResponse(T toCreate) throws MciException;

  @Override
  public List<ServiceResponse<T>> getGames(String playerName, Class<T> gameClass)
      throws MciException {

    String gameName = getGameName(gameClass);

    if (StringUtils.isEmpty(playerName)) {
      throw GameExceptions.InvalidRequest(gameName);
    }

    List<T> games;
    try {
      games = generator.getGamesForPlayer(playerName, gameClass);
    } catch (EngineException e) {
      throw GameExceptions.FailedToRetrieveGames(gameClass.getSimpleName(), playerName, e);
    }
    List<ServiceResponse<T>> gameResponses = new ArrayList<>();
    for (T game : games) {
      ServiceResponse<T> respItem = new ServiceResponse<>(game);
      gameResponses.add(respItem);
    }
    return gameResponses;
  }

  @Override
  public R createGame(String playerName, Difficulty difficulty, Class<T> gameClass) throws MciException {

    String gameName = getGameName(gameClass);

    if (StringUtils.isEmpty(playerName)) {
      throw GameExceptions.InvalidRequest(gameName);
    }

    String fullName = Utils.getFullGameName(gameName, difficulty);
    int lastCompletedLevel;
    try {
      lastCompletedLevel = generator.getLastCompletedLevel(fullName, difficulty, playerName);
    } catch (EngineException e) {
      throw GameExceptions.FailedToRetrieveLastLevel(gameName, difficulty, playerName, e);
    }
    int newLevel = lastCompletedLevel + 1;

    EntityRestriction maxCompleteTimeRes;
    try {
      maxCompleteTimeRes = rules.getEntityRestriction(fullName, "maxCompletionTime");
    } catch (RulesException e) {
      throw GameExceptions.UnableToRetrieveGameRules(Synonyms.NAME, e);
    }

    T toCreate;
    try {
      toCreate = gameClass.getDeclaredConstructor().newInstance();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw GameExceptions.GenerationError(gameName, e);
    }

    toCreate.setMaxCompletionTime(generator.generateLongDataValue(maxCompleteTimeRes.getDataRange()));
    toCreate.setPlayerName(playerName);
    toCreate.setLevel(newLevel);
    toCreate.setDifficulty(difficulty);

    try {
      handleDataTypeRestrictions(fullName, toCreate);
      generator.upsertGame(toCreate);
    } catch (EngineException e) {
      throw GameExceptions.GenerationError(fullName, e);
    }

    handleRestrictions(fullName, toCreate);

    return toResponse(toCreate);
  }

  @Override
  public R getGame(String id, String player, Class<T> gameClass) throws MciException {
    String gameName = getGameName(gameClass);

    if (StringUtils.isEmpty(id)
        || StringUtils.isEmpty(player)) {
      throw GameExceptions.InvalidRequest(gameName);
    }

    T game;
    try {
      game = generator.getGameWithId(id, player, gameClass);
    } catch (EngineException e) {
      throw GameExceptions.UnableToRetrieveGame(gameName, id, player, e);
    }

    return toResponse(game);
  }

  @Override
  public R solveGame(String id, String player, Long completionTime,
      Object solution, Class<T> gameClass) throws MciException {
    String gameName = getGameName(gameClass);

    if (!isValid(solution)) {
      throw GameExceptions.InvalidRequest(gameName);
    }

    R gameResponse = getGame(id,player,gameClass);
    T game = gameResponse.getGame();

    if (completionTime > game.getMaxCompletionTime()) {
      throw GameExceptions.SurpassedMaxCompletionTime(gameName, id, game.getMaxCompletionTime());
    }
    if (!StringUtils.isEmpty(game.getCompletedDate())) {
      throw GameExceptions.GameIsAlreadySolvedAt(gameName, id, game.getCompletedDate());
    }

    boolean solved = checkSolution(game, solution);

    if (solved) {
      game.setCompletedDate(String.valueOf(System.currentTimeMillis()));
      game.setCompletionTime(completionTime);
      try {
        generator.upsertGame(game);
      } catch (EngineException e) {
        throw GameExceptions.GenerationError(gameName, e);
      }
    }

    return toResponse(game);
  }

  String getGameName (Class<T> gameClass) throws MciException {
    String gameName;
    try {
      gameName = ano.getEntityValue(gameClass);
    } catch (EngineException e) {
      throw GameExceptions.GenerationError(gameClass.getSimpleName(), e);
    }
    return gameName;
  }

  protected <U extends BaseGameObject> void createObjRelation (T toCreate, List<U> gameObjs, EntityProperty onProperty) throws MciException {
    for (U gameObj : gameObjs) {
      createObjRelation(toCreate, gameObj, onProperty);
    }
  }

  protected <U extends BaseGameObject> void createObjRelation (T toCreate, U gameObj, EntityProperty onProperty) throws MciException {
    try {
      generator.createObjRelation(toCreate.getId(), onProperty, gameObj.getId());
    } catch (EngineException e) {
      throw GameExceptions.GenerationError(toCreate.getId(), e);
    }
  }

}
