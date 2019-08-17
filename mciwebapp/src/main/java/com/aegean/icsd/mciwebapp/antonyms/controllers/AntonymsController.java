package com.aegean.icsd.mciwebapp.antonyms.controllers;

import java.util.List;
import java.util.Locale;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.mciwebapp.antonyms.beans.AntonymResponse;
import com.aegean.icsd.mciwebapp.antonyms.beans.Antonyms;
import com.aegean.icsd.mciwebapp.antonyms.interfaces.IAntonymsSvc;
import com.aegean.icsd.mciwebapp.common.FilterResponse;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.common.beans.Response;
import com.aegean.icsd.mciwebapp.common.beans.ServiceResponse;
import com.aegean.icsd.mciwebapp.synonyms.beans.SynonymRequest;
import com.aegean.icsd.mciwebapp.synonyms.beans.SynonymResponse;

/**
 * https://localhost:8443/mci/antonyms
 */
@RestController
@RequestMapping("antonyms")
public class AntonymsController {

  private static final Logger LOGGER = LogManager.getLogger(AntonymsController.class);

  @Autowired
  private IAntonymsSvc antonymsSvc;

  @GetMapping(value = "",
    produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Response<List<ServiceResponse<Antonyms>>> getGames(@RequestParam(name = "difficulty", required = false) String difficulty,
                                                            @RequestParam(name = "completed", required = false) Boolean completed,
                                                            @RequestHeader("X-INFO-PLAYER") String player) throws MciException {

    List<ServiceResponse<Antonyms>> responses = antonymsSvc.getGames(player, Antonyms.class);
    List<ServiceResponse<Antonyms>> filtered = FilterResponse.by(responses, difficulty, completed);

    return new Response<>(filtered);
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public Response<AntonymResponse> createGame(@RequestBody SynonymRequest req,
                                              @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    LOGGER.info("createWordPuzzle request received");
    Difficulty dif = Difficulty.valueOf(req.getDifficulty().toUpperCase(Locale.ENGLISH));
    AntonymResponse resp = antonymsSvc.createGame(player, dif, Antonyms.class);
    return new Response<>(resp);
  }

  @GetMapping(value = "/{id}",
    produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Response<AntonymResponse> getGame(@PathVariable("id") String id,
                                           @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    AntonymResponse obs = antonymsSvc.getGame(id, player, Antonyms.class);
    return new Response<>(obs);
  }

  @PutMapping(value = "/{id}",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Response<AntonymResponse> solveGame(@PathVariable("id") String id,
                                             @RequestBody SynonymRequest req,
                                             @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    AntonymResponse response = antonymsSvc.solveGame(id, player, req.getCompletionTime(), req.getSolution(), Antonyms.class);
    return new Response<>(response);
  }
}
