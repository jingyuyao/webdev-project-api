package com.jingyuyao.webdevprojectapi.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Pipes to external Hearthstone API using our own API key.
 */
@RestController
public class HearthstoneService {

  private static final String API_HOST = "https://omgvamp-hearthstone-v1.p.mashape.com";
  private static final String MASHAPE_HEADER = "X-Mashape-Key";

  @Value("${MASHAPE_KEY}")
  private String mashapeKey;

  @GetMapping("/api/hs/info")
  public ResponseEntity info() {
    return get("/info");
  }

  @GetMapping("/api/hs/cards")
  public ResponseEntity<String> cards() {
    return get("/cards");
  }

  private ResponseEntity<String> get(String path) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(MASHAPE_HEADER, mashapeKey);
    HttpEntity<String> entity = new HttpEntity<>(null, headers);
    RestTemplate template = new RestTemplate();
    return template.exchange(API_HOST + path, HttpMethod.GET, entity, String.class);
  }
}
