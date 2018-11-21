package com.github.daggerok.rest;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class MyResource {

  @GET
  @Path("")
  public JsonObject index() {
    return Json.createObjectBuilder()
               .add("hello", "world")
               .build();
  }
}
