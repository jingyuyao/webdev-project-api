package com.jingyuyao.webdevprojectapi.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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
  public ResponseEntity<String> findAllBy(
      @RequestParam(required = false) String search,
      @RequestParam(required = false) String set,
      @RequestParam(required = false) String clazz,
      @RequestParam(required = false) String faction,
      @RequestParam(required = false) String quality,
      @RequestParam(required = false) String race,
      @RequestParam(required = false) String type) {
    if (search != null) {
      return get("/cards/search/{name}", search);
    }
    if (set != null) {
      return get("/cards/sets/{set}", set);
    }
    if (clazz != null) {
      return get("/cards/classes/{clazz}", clazz);
    }
    if (faction != null) {
      return get("/cards/factions/{faction}", faction);
    }
    if (quality != null) {
      return get("/cards/qualities/{quality}", quality);
    }
    if (race != null) {
      return get("/cards/races/{race}", race);
    }
    if (type != null) {
      return get("/cards/types/{type}", type);
    }
    return get("/cards");
  }

  @GetMapping("/api/hs/cards/{id}")
  public ResponseEntity<String> findById(@PathVariable String id) {
    return get("/cards/{id}", id);
  }

  private ResponseEntity<String> get(String path, Object... params) {
    String uri =
        UriComponentsBuilder
            .fromUriString(API_HOST + path)
            .queryParam("collectible", 1)
            .build().toUriString();

    HttpHeaders headers = new HttpHeaders();
    headers.set(MASHAPE_HEADER, mashapeKey);

    HttpEntity<String> entity = new HttpEntity<>(headers);
    RestTemplate template = new RestTemplate();

    return template.exchange(uri, HttpMethod.GET, entity, String.class, params);
  }
}
