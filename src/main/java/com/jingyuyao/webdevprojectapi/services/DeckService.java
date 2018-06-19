package com.jingyuyao.webdevprojectapi.services;

import com.jingyuyao.webdevprojectapi.models.Card;
import com.jingyuyao.webdevprojectapi.models.Deck;
import com.jingyuyao.webdevprojectapi.models.User;
import com.jingyuyao.webdevprojectapi.models.User.Role;
import com.jingyuyao.webdevprojectapi.repositories.CardRepository;
import com.jingyuyao.webdevprojectapi.repositories.DeckRepository;
import com.jingyuyao.webdevprojectapi.repositories.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
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
  private final CardRepository cardRepository;

  @Autowired
  DeckService(
      DeckRepository deckRepository,
      UserRepository userRepository,
      CardRepository cardRepository) {
    this.deckRepository = deckRepository;
    this.userRepository = userRepository;
    this.cardRepository = cardRepository;
  }

  @GetMapping("/api/deck")
  public Iterable<Deck> findAll(@RequestParam(name = "title", required = false) String title) {
    if (title != null) {
      return deckRepository.findAllByTitleIgnoreCaseContaining(title);
    }
    return deckRepository.findAll();
  }

  @GetMapping("/api/user/{userId}/decks")
  public ResponseEntity<List<Deck>> findAllByUserId(@PathVariable int userId) {
    return userRepository
        .findById(userId)
        .map(User::getDecks)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping("/api/deck/{id}")
  public ResponseEntity<Deck> findById(@PathVariable int id) {
    return deckRepository
        .findById(id)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping("/api/deck/{id}/cards")
  public ResponseEntity<Set<Card>> findCardsByDeckId(@PathVariable int id) {
    return deckRepository
        .findById(id)
        .map(Deck::getCards)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping("/api/deck")
  public ResponseEntity<Deck> createDeck(@Valid @RequestBody Deck deck, HttpSession httpSession) {
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
  public ResponseEntity<Deck> updateDeck(
      @PathVariable int id, @Valid @RequestBody Deck deck, HttpSession httpSession) {
    return getDeckIfAllowed(id, httpSession)
        .map(savedDeck -> {
          savedDeck.setTitle(deck.getTitle());
          savedDeck.setDescription(deck.getDescription());
          return deckRepository.save(savedDeck);
        })
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.badRequest().build());
  }

  @PutMapping("/api/deck/{id}/cards")
  public ResponseEntity<Set<Card>> updateDeckCards(
      @PathVariable int id, @Valid @RequestBody Set<Card> cards, HttpSession httpSession) {
    return getDeckIfAllowed(id, httpSession)
        .map(savedDeck -> {
          cardRepository.saveAll(cards);
          savedDeck.getCards().clear();
          savedDeck.getCards().addAll(cards);
          return deckRepository.save(savedDeck).getCards();
        })
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.badRequest().build());
  }

  @DeleteMapping("/api/deck/{id}")
  public ResponseEntity deleteDeck(@PathVariable int id, HttpSession httpSession) {
    return getDeckIfAllowed(id, httpSession)
        .map(savedDeck -> {
          deckRepository.delete(savedDeck);
          return ResponseEntity.ok().build();
        })
        .orElseGet(() -> ResponseEntity.badRequest().build());
  }

  private Optional<Deck> getDeckIfAllowed(int id, HttpSession httpSession) {
    return UserService
        .getUserId(httpSession)
        .flatMap(userRepository::findById)
        .flatMap(user ->
            user.getRoles().contains(Role.ADMIN)
                ? deckRepository.findById(id)
                : deckRepository.findByIdAndUserId(id, user.getId()));
  }
}
