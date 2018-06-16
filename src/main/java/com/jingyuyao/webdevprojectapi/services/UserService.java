package com.jingyuyao.webdevprojectapi.services;

import com.jingyuyao.webdevprojectapi.models.User;
import com.jingyuyao.webdevprojectapi.models.User.IdentityProvider;
import com.jingyuyao.webdevprojectapi.repositories.UserRepository;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserService {

  private static final String USER_ID = "user_id";
  private static final String PROVIDED_ID = "provided_id";

  private final UserRepository userRepository;
  private final IdTokenValidator idTokenValidator;

  @Autowired
  UserService(UserRepository userRepository, IdTokenValidator idTokenValidator) {
    this.userRepository = userRepository;
    this.idTokenValidator = idTokenValidator;
  }

  static Optional<Integer> getUserId(HttpSession httpSession) {
    return Optional.ofNullable((Integer) httpSession.getAttribute(USER_ID));
  }

  @PostMapping("/api/logInOrRegister")
  public ResponseEntity<User> logInOrRegister(
      @Valid @RequestBody IdTokenPayload idTokenPayload, HttpSession httpSession) {
    IdentityProvider identityProvider = idTokenPayload.getIdentityProvider();
    String idToken = idTokenPayload.getIdToken();
    Integer sessionUserId = (Integer) httpSession.getAttribute(USER_ID);
    String sessionProvidedId = (String) httpSession.getAttribute(PROVIDED_ID);

    return idTokenValidator
        .validate(identityProvider, idToken)
        .map(validUser -> {
          String providedId = validUser.getProvidedId();
          if (sessionUserId != null && providedId.equals(sessionProvidedId)) {
            // Trust session.
            return userRepository
                .findById(sessionUserId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
          } else {
            // Update session.
            httpSession.setAttribute(PROVIDED_ID, providedId);
            return userRepository
                .findByIdentityProviderAndProvidedId(identityProvider, providedId)
                .map(savedUser -> {
                  httpSession.setAttribute(USER_ID, savedUser.getId());
                  return ResponseEntity.ok(savedUser);
                })
                .orElseGet(() -> {
                  User savedUser = userRepository.save(validUser);
                  httpSession.setAttribute(USER_ID, savedUser.getId());
                  return ResponseEntity.ok(savedUser);
                });
          }
        })
        .orElseGet(() -> ResponseEntity.badRequest().build());
  }

  @PostMapping("/api/logOut")
  public void logOut(HttpSession httpSession) {
    httpSession.invalidate();
  }

  @PutMapping("/api/profile")
  public ResponseEntity<User> updateProfile(
      @Valid @RequestBody User user, HttpSession httpSession) {
    return getUserId(httpSession)
        .flatMap(userRepository::findById)
        .map(savedUser -> {
          savedUser.setName(user.getName());
          savedUser.setEmail(user.getEmail());
          return userRepository.save(savedUser);
        })
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.badRequest().build());
  }

  public static class IdTokenPayload {

    @NotNull
    private IdentityProvider identityProvider;
    @NotNull
    private String idToken;

    public IdentityProvider getIdentityProvider() {
      return identityProvider;
    }

    public void setIdentityProvider(
        IdentityProvider identityProvider) {
      this.identityProvider = identityProvider;
    }

    public String getIdToken() {
      return idToken;
    }

    public void setIdToken(String idToken) {
      this.idToken = idToken;
    }
  }
}
