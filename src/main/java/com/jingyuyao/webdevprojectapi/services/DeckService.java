package com.jingyuyao.webdevprojectapi.services;

import com.jingyuyao.webdevprojectapi.models.Deck;
import com.jingyuyao.webdevprojectapi.repositories.DeckRepository;
import com.jingyuyao.webdevprojectapi.repositories.UserRepository;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeckService {

  private final DeckRepository deckRepository;
  private final UserRepository userRepository;

  @Autowired
  DeckService(DeckRepository deckRepository, UserRepository userRepository) {
    this.deckRepository = deckRepository;
    this.userRepository = userRepository;
  }

  @GetMapping("/api/deck")
  public Iterable<Deck> findAll(@RequestParam(name = "title", required = false) String title) {
    if (title != null) {
      return deckRepository.findAllByTitleIgnoreCaseContaining(title);
    }
    return deckRepository.findAll();
  }

  @GetMapping("/api/user/{userId}/decks")
  public Iterable<Deck> findAllByUserId(@PathVariable int userId) {
    return deckRepository.findAllByUserId(userId);
  }

  @PostMapping("/api/deck")
  public ResponseEntity<Deck> create(@RequestBody Deck deck, HttpSession httpSession) {
    return UserService
        .getUserId(httpSession)
        .flatMap(userRepository::findById)
        .map(user -> {
          deck.setUser(user);
          return deckRepository.save(deck);
        })
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.badRequest().build());
  }

  @PutMapping("/api/deck/{id}")
  public ResponseEntity<Deck> update(
      @PathVariable int id, @RequestBody Deck deck, HttpSession httpSession) {
    return UserService
        .getUserId(httpSession)
        .flatMap(userRepository::findById)
        .flatMap(user -> deckRepository.findByIdAndUserId(id, user.getId()))
        .map(savedDeck -> {
          savedDeck.setTitle(deck.getTitle());
          savedDeck.setDescription(deck.getDescription());
          return deckRepository.save(savedDeck);
        })
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.badRequest().build());
  }

  @DeleteMapping("/api/deck/{id}")
  public ResponseEntity delete(@PathVariable int id, HttpSession httpSession) {
    return UserService
        .getUserId(httpSession)
        .flatMap(userRepository::findById)
        .flatMap(user -> deckRepository.findByIdAndUserId(id, user.getId()))
        .map(savedDeck -> {
          deckRepository.delete(savedDeck);
          return ResponseEntity.ok().build();
        })
        .orElseGet(() -> ResponseEntity.badRequest().build());
  }
}
