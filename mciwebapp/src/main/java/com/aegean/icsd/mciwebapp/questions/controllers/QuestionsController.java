package com.aegean.icsd.mciwebapp.questions.controllers;

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
import com.aegean.icsd.mciwebapp.common.beans.ServiceResponse;
import com.aegean.icsd.mciwebapp.observations.beans.Observation;
import com.aegean.icsd.mciwebapp.observations.beans.ObservationRequest;
import com.aegean.icsd.mciwebapp.observations.beans.ObservationResponse;
import com.aegean.icsd.mciwebapp.questions.beans.Questions;
import com.aegean.icsd.mciwebapp.questions.beans.QuestionsRequest;
import com.aegean.icsd.mciwebapp.questions.beans.QuestionsResponse;
import com.aegean.icsd.mciwebapp.questions.interfaces.IQuestionsSvc;

/**
 * https://localhost:8443/mci/api/questions
 */
@RestController
@RequestMapping("api/questions")
public class QuestionsController {

  private static final Logger LOGGER = LogManager.getLogger(QuestionsController.class);

  @Autowired
  private IQuestionsSvc questionsSvc;

  @GetMapping(value = "",
    produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Response<List<ServiceResponse<Questions>>> getGames(@RequestParam(name = "difficulty", required = false) String difficulty,
                                                             @RequestParam(name = "completed", required = false) Boolean completed,
                                                             @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    List<ServiceResponse<Questions>> responses = questionsSvc.getGames(player, Questions.class);
    List<ServiceResponse<Questions>> filtered = FilterResponse.by(responses, difficulty, completed);
    return new Response<>(filtered);
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public Response<QuestionsResponse> createGame(@RequestBody QuestionsRequest req,
                                                  @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    LOGGER.info("createObservation request received");
    Difficulty dif = Difficulty.valueOf(req.getDifficulty().toUpperCase());
    QuestionsResponse resp = questionsSvc.createGame(player, dif, Questions.class);
    return new Response<>(resp);
  }

  @GetMapping(value = "/{id}",
          produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Response<QuestionsResponse> getGame(@PathVariable("id") String id,
                                               @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    QuestionsResponse question = questionsSvc.getGame(id, player, Questions.class);
    return new Response<>(question);
  }

  @PutMapping(value = "/{id}",
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Response<QuestionsResponse> solveGame(@PathVariable("id") String id,
                                                 @RequestBody QuestionsRequest req,
                                                 @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    QuestionsResponse response = questionsSvc.solveGame(id, player, req.getCompletionTime(), req.getSolution(), Questions.class);
    return new Response<>(response);
  }
}
