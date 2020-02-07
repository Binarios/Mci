package com.aegean.icsd.mciwebapp.puzzles.controllers;

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
import com.aegean.icsd.mciwebapp.puzzles.beans.Puzzle;
import com.aegean.icsd.mciwebapp.puzzles.beans.PuzzleRequest;
import com.aegean.icsd.mciwebapp.puzzles.beans.PuzzleResponse;
import com.aegean.icsd.mciwebapp.puzzles.interfaces.IPuzzleSvc;
import com.aegean.icsd.mciwebapp.recall.beans.Recall;
import com.aegean.icsd.mciwebapp.recall.beans.RecallRequest;
import com.aegean.icsd.mciwebapp.recall.beans.RecallResponse;

/**
 * https://localhost:8443/mci/api/puzzles
 */
@RestController
@RequestMapping("api/puzzles")
public class PuzzleController {

  private static final Logger LOGGER = LogManager.getLogger(PuzzleController.class);

  @Autowired
  private IPuzzleSvc puzzleSvc;

  @GetMapping(value = "",
    produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Response<List<ServiceResponse<Puzzle>>> getGames(@RequestParam(name = "difficulty", required = false) String difficulty,
                                                          @RequestParam(name = "completed", required = false) Boolean completed,
                                                          @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    List<ServiceResponse<Puzzle>> responses = puzzleSvc.getGames(player, Puzzle.class);
    List<ServiceResponse<Puzzle>> filtered = FilterResponse.by(responses, difficulty, completed);
    return new Response<>(filtered);
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public Response<PuzzleResponse> createGame(@RequestBody PuzzleRequest req,
                                             @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    LOGGER.info("createRecall request received");
    Difficulty dif = Difficulty.valueOf(req.getDifficulty().toUpperCase());
    PuzzleResponse resp = puzzleSvc.createGame(player, dif, Puzzle.class);
    return new Response<>(resp);
  }

  @GetMapping(value = "/{id}",
          produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Response<PuzzleResponse> getGame(@PathVariable("id") String id,
                                          @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    PuzzleResponse resp = puzzleSvc.getGame(id, player, Puzzle.class);
    return new Response<>(resp);
  }

  @PutMapping(value = "/{id}",
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Response<PuzzleResponse> solveGame(@PathVariable("id") String id,
                                            @RequestBody PuzzleRequest req,
                                            @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    PuzzleResponse response = puzzleSvc.solveGame(id, player, req.getCompletionTime(), req.getSolution(), Puzzle.class);
    return new Response<>(response);
  }
}
