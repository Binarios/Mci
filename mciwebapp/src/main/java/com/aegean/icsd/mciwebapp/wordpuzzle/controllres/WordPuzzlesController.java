package com.aegean.icsd.mciwebapp.wordpuzzle.controllres;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
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
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.common.beans.Response;
import com.aegean.icsd.mciwebapp.observations.beans.ObservationRequest;
import com.aegean.icsd.mciwebapp.observations.beans.ObservationResponse;
import com.aegean.icsd.mciwebapp.observations.interfaces.IObservationSvc;
import com.aegean.icsd.mciwebapp.wordpuzzle.beans.WordPuzzleResponse;
import com.aegean.icsd.mciwebapp.wordpuzzle.beans.WorldPuzzleRequest;
import com.aegean.icsd.mciwebapp.wordpuzzle.interfaces.IWordPuzzleSvc;

/**
 * https://localhost:8443/mci/observations
 */
@RestController
@RequestMapping("observations")
public class WordPuzzlesController {

  private static final Logger LOGGER = LogManager.getLogger(WordPuzzlesController.class);

  @Autowired
  private IWordPuzzleSvc wordPuzzleSvc;

  @GetMapping(value = "",
    produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Response<List<WordPuzzleResponse>> getObservation(@RequestParam(name = "difficulty", required = false) String difficulty,
                                                            @RequestParam(name = "completed", required = false) Boolean completed,
                                                            @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    List<WordPuzzleResponse> responses = wordPuzzleSvc.getWordPuzzles(player);
    List<WordPuzzleResponse> filtered = responses.stream().filter(x -> {
      boolean choose = true;
      if (!StringUtils.isEmpty(difficulty)) {
        Difficulty diff = Difficulty.valueOf(difficulty.toUpperCase());
        choose = x.getPuzzle().getDifficulty().equals(diff);
      }

      if (completed != null) {
        if (completed) {
          choose &= x.getPuzzle().getCompletedDate() != null;
        } else {
          choose &= x.getPuzzle().getCompletedDate() == null;
        }
      }
      return choose;
    }).collect(Collectors.toList());


    return new Response<>(filtered);
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public Response<WordPuzzleResponse> createObservation(@RequestBody WorldPuzzleRequest req,
                                                         @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    LOGGER.info("createObservation request received");
    Difficulty dif = Difficulty.valueOf(req.getDifficulty().toUpperCase());
    WordPuzzleResponse resp = wordPuzzleSvc.createWordPuzzle(player, dif);
    return new Response<>(resp);
  }

  @GetMapping(value = "/{id}",
          produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Response<WordPuzzleResponse> getObservation(@PathVariable("id") String id,
                                                      @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    WordPuzzleResponse obs = wordPuzzleSvc.getWordPuzzle(id, player);
    return new Response<>(obs);
  }

  @PutMapping(value = "/{id}",
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Response<WordPuzzleResponse> updateObservation(@PathVariable("id") String id,
                                                         @RequestBody WorldPuzzleRequest req,
                                                         @RequestHeader("X-INFO-PLAYER") String player) throws MciException {
    WordPuzzleResponse response = wordPuzzleSvc.solveGame(id, player, req.getCompletionTime(), req.getSolution());
    return new Response<>(response);
  }
}