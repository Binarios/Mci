package com.aegean.icsd.mciwebapp.observations.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.common.beans.Response;
import com.aegean.icsd.mciwebapp.observations.beans.Observation;
import com.aegean.icsd.mciwebapp.observations.beans.ObservationRequest;
import com.aegean.icsd.mciwebapp.observations.beans.ObservationResponse;
import com.aegean.icsd.mciwebapp.observations.interfaces.IObservationSvc;

/**
 * https://localhost:8443/mci/observations
 */
@RestController
@RequestMapping("observations")
public class ObservationsController {

  private static final Logger LOGGER = LogManager.getLogger(ObservationsController.class);

  @Autowired
  private IObservationSvc observationImpl;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Response<List<ObservationResponse>> getObservations() {
    List<ObservationResponse> obs = new ArrayList<>();
    return new Response<>(obs);
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public Response<ObservationResponse> createObservation(@RequestBody ObservationRequest req) throws MciException {
    LOGGER.info("createObservation request received");
    Difficulty dif = Difficulty.valueOf(req.getDifficulty().toUpperCase());
    ObservationResponse resp = observationImpl.createObservation(req.getPlayer(), dif);
    return new Response<>(resp);
  }

  @GetMapping(value = "/{id}",
          produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Response<ObservationResponse> getObservation(@PathVariable("id") UUID id) {
    return new Response<>(new ObservationResponse());
  }

  @PutMapping(value = "/{id}",
          produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public Response<ObservationResponse> updateObservation(@PathVariable("id") UUID id, @RequestBody Observation observation) {
    return new Response<>(new ObservationResponse());
  }
}
