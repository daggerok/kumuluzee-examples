package com.github.daggerok.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.eclipse.jetty.http.MimeTypes.Type.APPLICATION_JSON;

@Provider
public class ErrorHandler implements ExceptionMapper<Exception> {

  private static final Logger log = LoggerFactory.getLogger(ErrorHandler.class);

  public Response toResponse(Exception e) {
    log.error("oops: {}", e.getLocalizedMessage(), e);
    return Response.status(BAD_REQUEST)
                   .header("error", e.getLocalizedMessage())
                   .header(ACCEPT, APPLICATION_JSON.asString())
                   .header(CONTENT_TYPE, APPLICATION_JSON.asString())
                   .entity(Json.createObjectBuilder()
                               .add("error", e.getLocalizedMessage())
                               .build())
                   .build();
  }
}
