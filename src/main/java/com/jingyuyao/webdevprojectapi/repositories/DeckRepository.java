package com.jingyuyao.webdevprojectapi.repositories;

import com.jingyuyao.webdevprojectapi.models.Deck;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface DeckRepository extends CrudRepository<Deck, Integer> {

  Optional<Deck> findByIdAndUserId(int id, int userId);

  Iterable<Deck> findAllByUserId(int userId);

  Iterable<Deck> findAllByTitleIgnoreCaseContaining(String title);
}
