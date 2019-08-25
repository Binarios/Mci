package com.aegean.icsd.mciwebapp.logicalorder.controllers;

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
import com.aegean.icsd.mciwebapp.logicalorder.beans.LogicalOrder;
import com.aegean.icsd.mciwebapp.logicalorder.beans.LogicalOrderRequest;
import com.aegean.icsd.mciwebapp.logicalorder.beans.LogicalOrderResponse;
import com.aegean.icsd.mciwebapp.logicalorder.interfaces.ILogicalOrderSvc;

/**
 * https://localhost:8443/mci/logicalOrder
 */
@RestController
@RequestMapping("logicalOrder")
public class LogicalOrderController {

  private static final Logger LOGGER = LogManager.getLogger(LogicalOrderController.class);

  @Autowired
  private ILogicalOrderSvc logicalOrderSvc;

  @GetMapping(value = "",
    produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Response<List<ServiceResponse<LogicalOrder>>> getGames(@RequestParam(name = "difficulty", required = false) String difficulty,
                                                                @RequestParam(name = "completed", required = false) Boolean completed,
                                                                @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    List<ServiceResponse<LogicalOrder>> responses = logicalOrderSvc.getGames(player, LogicalOrder.class);
    List<ServiceResponse<LogicalOrder>> filtered = FilterResponse.by(responses, difficulty, completed);
    return new Response<>(filtered);
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public Response<LogicalOrderResponse> createGame(@RequestBody LogicalOrderRequest req,
                                                  @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    LOGGER.info("createRecall request received");
    Difficulty dif = Difficulty.valueOf(req.getDifficulty().toUpperCase());
    LogicalOrderResponse resp = logicalOrderSvc.createGame(player, dif, LogicalOrder.class);
    return new Response<>(resp);
  }

  @GetMapping(value = "/{id}",
          produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Response<LogicalOrderResponse> getGame(@PathVariable("id") String id,
                                          @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    LogicalOrderResponse resp = logicalOrderSvc.getGame(id, player, LogicalOrder.class);
    return new Response<>(resp);
  }

  @PutMapping(value = "/{id}",
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Response<LogicalOrderResponse> solveGame(@PathVariable("id") String id,
                                            @RequestBody LogicalOrderRequest req,
                                            @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    LogicalOrderResponse response = logicalOrderSvc.solveGame(id, player, req.getCompletionTime(), req.getSolution(), LogicalOrder.class);
    return new Response<>(response);
  }
}
