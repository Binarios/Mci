package com.aegean.icsd.mciwebapp.recall.controllers;

import java.util.List;

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
import com.aegean.icsd.mciwebapp.common.FilterResponse;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.common.beans.Response;
import com.aegean.icsd.mciwebapp.observations.beans.ObservationRequest;
import com.aegean.icsd.mciwebapp.observations.beans.ObservationResponse;
import com.aegean.icsd.mciwebapp.recall.beans.RecallRequest;
import com.aegean.icsd.mciwebapp.recall.beans.RecallResponse;
import com.aegean.icsd.mciwebapp.recall.interfaces.IRecallSvc;

/**
 * https://localhost:8443/mci/recall
 */
@RestController
@RequestMapping("recall")
public class RecallController {

  private static final Logger LOGGER = LogManager.getLogger(RecallController.class);

  @Autowired
  private IRecallSvc recallSvc;

  @GetMapping(value = "",
    produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Response<List<RecallResponse>> getGames(@RequestParam(name = "difficulty", required = false) String difficulty,
                                                 @RequestParam(name = "completed", required = false) Boolean completed,
                                                 @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    List<RecallResponse> responses = recallSvc.getGames(player);
    List<RecallResponse> filtered = FilterResponse.by(responses, difficulty, completed);
    return new Response<>(filtered);
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public Response<RecallResponse> createGame(@RequestBody RecallRequest req,
                                             @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    LOGGER.info("createRecall request received");
    Difficulty dif = Difficulty.valueOf(req.getDifficulty().toUpperCase());
    RecallResponse resp = recallSvc.createGame(player, dif);
    return new Response<>(resp);
  }

  @GetMapping(value = "/{id}",
          produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Response<RecallResponse> getGame(@PathVariable("id") String id,
                                          @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    RecallResponse obs = recallSvc.getGame(id, player);
    return new Response<>(obs);
  }

  @PutMapping(value = "/{id}",
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Response<RecallResponse> solveGame(@PathVariable("id") String id,
                                            @RequestBody RecallRequest req,
                                            @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    RecallResponse response = recallSvc.solveGame(id, player, req.getCompletionTime(), req.getSolution());
    return new Response<>(response);
  }
}
