package com.jingyuyao.webdevprojectapi.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

@Entity
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  @NotNull
  private IdentityProvider identityProvider;
  @NotNull
  private String providedId;
  @NotNull
  private String name;
  @NotNull
  private String email;
  @ElementCollection
  private List<Role> roles = new ArrayList<>();
  @JsonIgnore
  @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<Deck> decks = new ArrayList<>();

  public int getId() {
    return id;
  }

  public IdentityProvider getIdentityProvider() {
    return identityProvider;
  }

  public void setIdentityProvider(
      IdentityProvider identityProvider) {
    this.identityProvider = identityProvider;
  }

  public String getProvidedId() {
    return providedId;
  }

  public void setProvidedId(String providedId) {
    this.providedId = providedId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public List<Role> getRoles() {
    return roles;
  }

  public List<Deck> getDecks() {
    return decks;
  }

  public enum IdentityProvider {
    GOOGLE,
  }

  public enum Role {
    ADMIN,
  }
}
