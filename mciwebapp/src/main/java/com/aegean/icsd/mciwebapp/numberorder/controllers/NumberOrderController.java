package com.aegean.icsd.mciwebapp.numberorder.controllers;

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
import com.aegean.icsd.mciwebapp.numberorder.beans.NumberOrder;
import com.aegean.icsd.mciwebapp.numberorder.beans.NumberOrderRequest;
import com.aegean.icsd.mciwebapp.numberorder.beans.NumberOrderResponse;
import com.aegean.icsd.mciwebapp.numberorder.interfaces.INumberOrderSvc;
import com.aegean.icsd.mciwebapp.recall.beans.Recall;
import com.aegean.icsd.mciwebapp.recall.beans.RecallRequest;
import com.aegean.icsd.mciwebapp.recall.beans.RecallResponse;

/**
 * https://localhost:8443/mci/api/numberOrder
 */
@RestController
@RequestMapping("api/numberOrder")
public class NumberOrderController {

  private static final Logger LOGGER = LogManager.getLogger(NumberOrderController.class);

  @Autowired
  private INumberOrderSvc numberOrderSvc;

  @GetMapping(value = "",
    produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Response<List<ServiceResponse<NumberOrder>>> getGames(@RequestParam(name = "difficulty", required = false) String difficulty,
                                                          @RequestParam(name = "completed", required = false) Boolean completed,
                                                          @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    List<ServiceResponse<NumberOrder>> responses = numberOrderSvc.getGames(player, NumberOrder.class);
    List<ServiceResponse<NumberOrder>> filtered = FilterResponse.by(responses, difficulty, completed);
    return new Response<>(filtered);
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public Response<NumberOrderResponse> createGame(@RequestBody NumberOrderRequest req,
                                                  @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    LOGGER.info("createRecall request received");
    Difficulty dif = Difficulty.valueOf(req.getDifficulty().toUpperCase());
    NumberOrderResponse resp = numberOrderSvc.createGame(player, dif, NumberOrder.class);
    return new Response<>(resp);
  }

  @GetMapping(value = "/{id}",
          produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Response<NumberOrderResponse> getGame(@PathVariable("id") String id,
                                          @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    NumberOrderResponse resp = numberOrderSvc.getGame(id, player, NumberOrder.class);
    return new Response<>(resp);
  }

  @PutMapping(value = "/{id}",
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Response<NumberOrderResponse> solveGame(@PathVariable("id") String id,
                                            @RequestBody NumberOrderRequest req,
                                            @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    NumberOrderResponse response = numberOrderSvc.solveGame(id, player, req.getCompletionTime(), req.getSolution(), NumberOrder.class);
    return new Response<>(response);
  }
}
