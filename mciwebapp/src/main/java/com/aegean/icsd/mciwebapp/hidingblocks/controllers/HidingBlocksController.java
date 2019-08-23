package com.aegean.icsd.mciwebapp.hidingblocks.controllers;

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
import com.aegean.icsd.mciwebapp.hidingblocks.beans.HidingBlocks;
import com.aegean.icsd.mciwebapp.hidingblocks.beans.HidingBlocksRequest;
import com.aegean.icsd.mciwebapp.hidingblocks.beans.HidingBlocksResponse;
import com.aegean.icsd.mciwebapp.hidingblocks.interfaces.IHidingBlocksSvc;
import com.aegean.icsd.mciwebapp.memorycards.beans.MemoryCards;
import com.aegean.icsd.mciwebapp.memorycards.beans.MemoryCardsRequest;
import com.aegean.icsd.mciwebapp.memorycards.beans.MemoryCardsResponse;

/**
 * https://localhost:8443/mci/hidingBlocks
 */
@RestController
@RequestMapping("hidingBlocks")
public class HidingBlocksController {

  private static final Logger LOGGER = LogManager.getLogger(HidingBlocksController.class);

  @Autowired
  private IHidingBlocksSvc hidingBlocksSvc;

  @GetMapping(value = "",
    produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Response<List<ServiceResponse<HidingBlocks>>> getGames(@RequestParam(name = "difficulty", required = false) String difficulty,
                                                                @RequestParam(name = "completed", required = false) Boolean completed,
                                                                @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    List<ServiceResponse<HidingBlocks>> responses = hidingBlocksSvc.getGames(player, HidingBlocks.class);
    List<ServiceResponse<HidingBlocks>> filtered = FilterResponse.by(responses, difficulty, completed);
    return new Response<>(filtered);
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public Response<HidingBlocksResponse> createGame(@RequestBody HidingBlocksRequest req,
                                                  @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    LOGGER.info("createRecall request received");
    Difficulty dif = Difficulty.valueOf(req.getDifficulty().toUpperCase());
    HidingBlocksResponse resp = hidingBlocksSvc.createGame(player, dif, HidingBlocks.class);
    return new Response<>(resp);
  }

  @GetMapping(value = "/{id}",
          produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Response<HidingBlocksResponse> getGame(@PathVariable("id") String id,
                                          @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    HidingBlocksResponse resp = hidingBlocksSvc.getGame(id, player, HidingBlocks.class);
    return new Response<>(resp);
  }

  @PutMapping(value = "/{id}",
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Response<HidingBlocksResponse> solveGame(@PathVariable("id") String id,
                                            @RequestBody HidingBlocksRequest req,
                                            @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    HidingBlocksResponse response = hidingBlocksSvc.solveGame(id, player, req.getCompletionTime(), req.getSolution(), HidingBlocks.class);
    return new Response<>(response);
  }
}
