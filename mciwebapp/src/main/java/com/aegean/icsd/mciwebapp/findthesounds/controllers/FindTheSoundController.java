package com.aegean.icsd.mciwebapp.findthesounds.controllers;

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
import com.aegean.icsd.mciwebapp.findthesounds.beans.FindTheSound;
import com.aegean.icsd.mciwebapp.findthesounds.beans.FindTheSoundRequest;
import com.aegean.icsd.mciwebapp.findthesounds.beans.FindTheSoundResponse;
import com.aegean.icsd.mciwebapp.findthesounds.interfaces.IFindTheSoundSvc;
import com.aegean.icsd.mciwebapp.memorycards.beans.MemoryCards;
import com.aegean.icsd.mciwebapp.memorycards.beans.MemoryCardsRequest;
import com.aegean.icsd.mciwebapp.memorycards.beans.MemoryCardsResponse;

/**
 * https://localhost:8443/mci/findTheSounds
 */
@RestController
@RequestMapping("findTheSounds")
public class FindTheSoundController {

  private static final Logger LOGGER = LogManager.getLogger(FindTheSoundController.class);

  @Autowired
  private IFindTheSoundSvc findTheSoundSvc;

  @GetMapping(value = "",
    produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Response<List<ServiceResponse<FindTheSound>>> getGames(@RequestParam(name = "difficulty", required = false) String difficulty,
                                                                @RequestParam(name = "completed", required = false) Boolean completed,
                                                                @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    List<ServiceResponse<FindTheSound>> responses = findTheSoundSvc.getGames(player, FindTheSound.class);
    List<ServiceResponse<FindTheSound>> filtered = FilterResponse.by(responses, difficulty, completed);
    return new Response<>(filtered);
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public Response<FindTheSoundResponse> createGame(@RequestBody FindTheSoundRequest req,
                                                   @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    LOGGER.info("createRecall request received");
    Difficulty dif = Difficulty.valueOf(req.getDifficulty().toUpperCase());
    FindTheSoundResponse resp = findTheSoundSvc.createGame(player, dif, FindTheSound.class);
    return new Response<>(resp);
  }

  @GetMapping(value = "/{id}",
          produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Response<FindTheSoundResponse> getGame(@PathVariable("id") String id,
                                          @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    FindTheSoundResponse resp = findTheSoundSvc.getGame(id, player, FindTheSound.class);
    return new Response<>(resp);
  }

  @PutMapping(value = "/{id}",
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Response<FindTheSoundResponse> solveGame(@PathVariable("id") String id,
                                            @RequestBody FindTheSoundRequest req,
                                            @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    FindTheSoundResponse response = findTheSoundSvc.solveGame(id, player, req.getCompletionTime(), req.getSolution(), FindTheSound.class);
    return new Response<>(response);
  }
}
