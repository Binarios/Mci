package com.aegean.icsd.mciwebapp.chronorder.controllers;

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
import com.aegean.icsd.mciwebapp.chronorder.beans.ChronologicalOrder;
import com.aegean.icsd.mciwebapp.chronorder.beans.ChronologicalOrderRequest;
import com.aegean.icsd.mciwebapp.chronorder.beans.ChronologicalOrderResponse;
import com.aegean.icsd.mciwebapp.chronorder.interfaces.IChronologicalOrderSvc;
import com.aegean.icsd.mciwebapp.common.FilterResponse;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.common.beans.Response;
import com.aegean.icsd.mciwebapp.common.beans.ServiceResponse;
import com.aegean.icsd.mciwebapp.recall.beans.Recall;
import com.aegean.icsd.mciwebapp.recall.beans.RecallRequest;
import com.aegean.icsd.mciwebapp.recall.beans.RecallResponse;

/**
 * https://localhost:8443/mci/api/chronologicalOrders
 */
@RestController
@RequestMapping("api/chronologicalOrders")
public class ChronologicalOrderController {

  private static final Logger LOGGER = LogManager.getLogger(ChronologicalOrderController.class);

  @Autowired
  private IChronologicalOrderSvc chronoSvc;

  @GetMapping(value = "",
    produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Response<List<ServiceResponse<ChronologicalOrder>>> getGames(@RequestParam(name = "difficulty", required = false) String difficulty,
                                                          @RequestParam(name = "completed", required = false) Boolean completed,
                                                          @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    List<ServiceResponse<ChronologicalOrder>> responses = chronoSvc.getGames(player, ChronologicalOrder.class);
    List<ServiceResponse<ChronologicalOrder>> filtered = FilterResponse.by(responses, difficulty, completed);
    return new Response<>(filtered);
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public Response<ChronologicalOrderResponse> createGame(@RequestBody ChronologicalOrderRequest req,
                                                         @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    LOGGER.info("createRecall request received");
    Difficulty dif = Difficulty.valueOf(req.getDifficulty().toUpperCase());
    ChronologicalOrderResponse resp = chronoSvc.createGame(player, dif, ChronologicalOrder.class);
    return new Response<>(resp);
  }

  @GetMapping(value = "/{id}",
          produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Response<ChronologicalOrderResponse> getGame(@PathVariable("id") String id,
                                          @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    ChronologicalOrderResponse resp = chronoSvc.getGame(id, player, ChronologicalOrder.class);
    return new Response<>(resp);
  }

  @PutMapping(value = "/{id}",
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Response<ChronologicalOrderResponse> solveGame(@PathVariable("id") String id,
                                            @RequestBody ChronologicalOrderRequest req,
                                            @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    ChronologicalOrderResponse response = chronoSvc.solveGame(id, player, req.getCompletionTime(), req.getSolution(), ChronologicalOrder.class);
    return new Response<>(response);
  }
}
