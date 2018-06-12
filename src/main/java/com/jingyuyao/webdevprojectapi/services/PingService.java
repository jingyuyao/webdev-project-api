package com.jingyuyao.webdevprojectapi.services;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingService {

  @GetMapping("/ping")
  public Pong ping() {
    return new Pong();
  }

  private class Pong {

    private final String data = "pong";

    public String getData() {
      return data;
    }
  }
}
