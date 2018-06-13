package com.jingyuyao.webdevprojectapi.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

  @GetMapping("/api/hs/cards/search/{name}")
  public ResponseEntity<String> search(@PathVariable String name) {
    return get("/cards/search/{name}", name);
  }

  @GetMapping("/api/hs/cards/sets/{set}")
  public ResponseEntity<String> findAllBySet(@PathVariable String set) {
    return get("/cards/sets/{set}", set);
  }

  @GetMapping("/api/hs/cards/classes/{clazz}")
  public ResponseEntity<String> findAllByClass(@PathVariable String clazz) {
    return get("/cards/classes/{clazz}", clazz);
  }

  @GetMapping("/api/hs/cards/factions/{faction}")
  public ResponseEntity<String> findAllByFaction(@PathVariable String faction) {
    return get("/cards/factions/{faction}", faction);
  }

  @GetMapping("/api/hs/cards/qualities/{quality}")
  public ResponseEntity<String> findAllByQuality(@PathVariable String quality) {
    return get("/cards/qualities/{quality}", quality);
  }

  @GetMapping("/api/hs/cards/races/{race}")
  public ResponseEntity<String> findAllByRace(@PathVariable String race) {
    return get("/cards/races/{race}", race);
  }

  @GetMapping("/api/hs/cards/types/{type}")
  public ResponseEntity<String> findAllByType(@PathVariable String type) {
    return get("/cards/types/{type}", type);
  }

  private ResponseEntity<String> get(String path, Object... params) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(MASHAPE_HEADER, mashapeKey);
    HttpEntity<String> entity = new HttpEntity<>(headers);
    RestTemplate template = new RestTemplate();
    return template.exchange(API_HOST + path, HttpMethod.GET, entity, String.class, params);
  }
}
