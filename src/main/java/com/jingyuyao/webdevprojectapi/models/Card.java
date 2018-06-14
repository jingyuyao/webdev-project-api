package com.jingyuyao.webdevprojectapi.models;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
public class Card {

  @Id
  @NotNull
  private String id;

  public String getId() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Card card = (Card) o;
    return Objects.equals(id, card.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
