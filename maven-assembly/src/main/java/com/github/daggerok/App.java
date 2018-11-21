package com.github.daggerok;

import com.kumuluz.ee.EeApplication;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationScoped
@ApplicationPath("")
public class App extends Application {
  public static void main(String[] args) {
    new EeApplication();
  }
}
