package com.aegean.icsd.mciwebapp.observations.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

import com.aegean.icsd.mciwebapp.common.beans.Response;
import com.aegean.icsd.mciwebapp.observations.beans.Observation;
import com.aegean.icsd.mciwebapp.observations.beans.ObservationsException;
import com.aegean.icsd.mciwebapp.observations.interfaces.IObservationSvc;

@RestController
@RequestMapping("observations")
public class ObservationsController {

  @Autowired
  private IObservationSvc observationSvc;

  @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Response<List<Observation>> getObservations() {
    List<Observation> obs = new ArrayList<>();
    return new Response<>(obs);
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
          produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public Response<Observation> createObservation(@RequestBody Observation observation) throws ObservationsException {
    Observation obs = observationSvc.createObservation(observation);
    return new Response<>(obs);
  }

  @GetMapping(value = "/{id}",
          produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Response<Observation> getObservation(@PathVariable("id") UUID id) {
    return new Response<>(new Observation());
  }

  @PutMapping(value = "/{id}",
          produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public Response<Observation> updateObservation(@PathVariable("id") UUID id, @RequestBody Observation observation) {
    return new Response<>(new Observation());
  }
}
