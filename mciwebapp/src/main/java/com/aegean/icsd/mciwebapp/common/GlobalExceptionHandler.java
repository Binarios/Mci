package com.aegean.icsd.mciwebapp.common;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.aegean.icsd.mciwebapp.common.beans.AppError;
import com.aegean.icsd.mciwebapp.common.beans.MciException;
import com.aegean.icsd.mciwebapp.common.beans.Response;

@ControllerAdvice("com.aegean.icsd.mciwebapp")
public class GlobalExceptionHandler {

  private static Logger LOGGER = Logger.getLogger(GlobalExceptionHandler.class);

  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  @ExceptionHandler(value = MciException.class)
  public Response businessErrorHandling(HttpServletRequest req, MciException e) {
    LOGGER.error(e.getCodeMessage(), e);
    AppError error = new AppError();
    error.setCode(e.getCode());
    error.setMessage(e.getCodeMessage());

    Response res = new Response(null);
    res.setError(error);

    return res;
  }

  @ResponseBody
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(value = Exception.class)
  public Response TechnicalErrorHandling(HttpServletRequest req, Exception e) {
    LOGGER.error(e.getMessage(), e);
    AppError error = new AppError();
    error.setCode("SYS.9999");
    error.setMessage("Internal Server Error.");

    Response res = new Response(null);
    res.setError(error);

    return res;
  }

}
