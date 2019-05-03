package com.github.daggerok.rest;

import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@ApplicationScoped
public class ErrorMapper implements ExceptionMapper<Exception> {

  @Context
  UriInfo uriInfo;

  @Override
  public Response toResponse(Exception e) {
    String message = e == null || e.getLocalizedMessage() == null
        ? "oops, unknown error..." : e.getLocalizedMessage();
    return Response.ok(Json.createObjectBuilder()
                           .add("message", message)
                           .add("_links", Json.createObjectBuilder()
                                              .add("greeting", buildUrl(uriInfo)))
                           .build())
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .build();
  }

  private static String buildUrl(UriInfo uriInfo) {
    return uriInfo.getBaseUriBuilder()
                  .path(GreetingResource.class)
                  .path(GreetingResource.class, "hello")
                  .build()
                  .toString();
  }
}
