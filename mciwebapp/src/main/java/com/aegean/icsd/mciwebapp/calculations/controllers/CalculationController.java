package com.aegean.icsd.mciwebapp.calculations.controllers;

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
import com.aegean.icsd.mciwebapp.calculations.beans.Calculation;
import com.aegean.icsd.mciwebapp.calculations.beans.CalculationRequest;
import com.aegean.icsd.mciwebapp.calculations.beans.CalculationResponse;
import com.aegean.icsd.mciwebapp.calculations.interfaces.ICalculationSvc;
import com.aegean.icsd.mciwebapp.common.FilterResponse;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.common.beans.Response;
import com.aegean.icsd.mciwebapp.common.beans.ServiceResponse;
import com.aegean.icsd.mciwebapp.hidingblocks.beans.HidingBlocks;
import com.aegean.icsd.mciwebapp.hidingblocks.beans.HidingBlocksRequest;
import com.aegean.icsd.mciwebapp.hidingblocks.beans.HidingBlocksResponse;

/**
 * https://localhost:8443/mci/api/calculations
 */
@RestController
@RequestMapping("api/calculations")
public class CalculationController {

  private static final Logger LOGGER = LogManager.getLogger(CalculationController.class);

  @Autowired
  private ICalculationSvc calculationSvc;

  @GetMapping(value = "",
    produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Response<List<ServiceResponse<Calculation>>> getGames(@RequestParam(name = "difficulty", required = false) String difficulty,
                                                               @RequestParam(name = "completed", required = false) Boolean completed,
                                                               @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    List<ServiceResponse<Calculation>> responses = calculationSvc.getGames(player, Calculation.class);
    List<ServiceResponse<Calculation>> filtered = FilterResponse.by(responses, difficulty, completed);
    return new Response<>(filtered);
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public Response<CalculationResponse> createGame(@RequestBody CalculationRequest req,
                                                  @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    LOGGER.info("createRecall request received");
    Difficulty dif = Difficulty.valueOf(req.getDifficulty().toUpperCase());
    CalculationResponse resp = calculationSvc.createGame(player, dif, Calculation.class);
    return new Response<>(resp);
  }

  @GetMapping(value = "/{id}",
          produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Response<CalculationResponse> getGame(@PathVariable("id") String id,
                                          @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    CalculationResponse resp = calculationSvc.getGame(id, player, Calculation.class);
    return new Response<>(resp);
  }

  @PutMapping(value = "/{id}",
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Response<CalculationResponse> solveGame(@PathVariable("id") String id,
                                            @RequestBody CalculationRequest req,
                                            @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    CalculationResponse response = calculationSvc.solveGame(id, player, req.getCompletionTime(), req.getSolution(), Calculation.class);
    return new Response<>(response);
  }
}
