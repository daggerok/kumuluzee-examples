package com.github.daggerok.rest;

import javax.json.Json;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v1/hello")
@Produces(MediaType.APPLICATION_JSON)
public class GreetingResource {

  @GET
  @Path("")
  public Response hello() {
    return Response.ok(Json.createObjectBuilder()
                           .add("hello", "world")
                           .build()).build();
  }
}
