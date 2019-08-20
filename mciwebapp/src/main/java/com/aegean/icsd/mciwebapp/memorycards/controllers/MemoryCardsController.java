package com.aegean.icsd.mciwebapp.memorycards.controllers;

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
import com.aegean.icsd.mciwebapp.memorycards.beans.MemoryCards;
import com.aegean.icsd.mciwebapp.memorycards.beans.MemoryCardsRequest;
import com.aegean.icsd.mciwebapp.memorycards.beans.MemoryCardsResponse;
import com.aegean.icsd.mciwebapp.memorycards.interfaces.IMemoryCardsSvc;
import com.aegean.icsd.mciwebapp.numberorder.beans.NumberOrder;
import com.aegean.icsd.mciwebapp.numberorder.beans.NumberOrderRequest;
import com.aegean.icsd.mciwebapp.numberorder.beans.NumberOrderResponse;

/**
 * https://localhost:8443/mci/memoryCards
 */
@RestController
@RequestMapping("memoryCards")
public class MemoryCardsController {

  private static final Logger LOGGER = LogManager.getLogger(MemoryCardsController.class);

  @Autowired
  private IMemoryCardsSvc memoryCardsSvc;

  @GetMapping(value = "",
    produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Response<List<ServiceResponse<MemoryCards>>> getGames(@RequestParam(name = "difficulty", required = false) String difficulty,
                                                               @RequestParam(name = "completed", required = false) Boolean completed,
                                                               @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    List<ServiceResponse<MemoryCards>> responses = memoryCardsSvc.getGames(player, MemoryCards.class);
    List<ServiceResponse<MemoryCards>> filtered = FilterResponse.by(responses, difficulty, completed);
    return new Response<>(filtered);
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public Response<MemoryCardsResponse> createGame(@RequestBody MemoryCardsRequest req,
                                                  @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    LOGGER.info("createRecall request received");
    Difficulty dif = Difficulty.valueOf(req.getDifficulty().toUpperCase());
    MemoryCardsResponse resp = memoryCardsSvc.createGame(player, dif, MemoryCards.class);
    return new Response<>(resp);
  }

  @GetMapping(value = "/{id}",
          produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Response<MemoryCardsResponse> getGame(@PathVariable("id") String id,
                                          @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    MemoryCardsResponse resp = memoryCardsSvc.getGame(id, player, MemoryCards.class);
    return new Response<>(resp);
  }

  @PutMapping(value = "/{id}",
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Response<MemoryCardsResponse> solveGame(@PathVariable("id") String id,
                                            @RequestBody MemoryCardsRequest req,
                                            @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    MemoryCardsResponse response = memoryCardsSvc.solveGame(id, player, req.getCompletionTime(), req.getSolution(), MemoryCards.class);
    return new Response<>(response);
  }
}
