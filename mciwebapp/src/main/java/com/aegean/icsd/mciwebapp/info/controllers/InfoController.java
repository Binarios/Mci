package com.aegean.icsd.mciwebapp.info.controllers;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.common.beans.Response;
import com.aegean.icsd.mciwebapp.info.beans.GetGamesResponse;
import com.aegean.icsd.mciwebapp.info.interfaces.IInfoSvc;

/**
 * https://localhost:8443/mci/api/info
 */
@RestController
@RequestMapping("api/info")
public class InfoController {
  private static final Logger LOGGER = LogManager.getLogger(InfoController.class);

  @Autowired
  private IInfoSvc infoSvc;

  @GetMapping(value = "/games",
    produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Response<GetGamesResponse> getGames()
    throws MciException {
    GetGamesResponse resp = new GetGamesResponse();
    resp.setGames(infoSvc.getGames());
    return new Response<>(resp);
  }
}
