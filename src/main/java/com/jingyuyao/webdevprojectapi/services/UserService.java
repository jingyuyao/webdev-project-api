package com.jingyuyao.webdevprojectapi.services;

import com.jingyuyao.webdevprojectapi.models.User;
import com.jingyuyao.webdevprojectapi.models.User.IdentityProvider;
import com.jingyuyao.webdevprojectapi.repositories.UserRepository;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserService {

  private static final String USER_ID = "user_id";

  private final UserRepository userRepository;
  private final IdTokenValidator idTokenValidator;

  @Autowired
  UserService(UserRepository userRepository, IdTokenValidator idTokenValidator) {
    this.userRepository = userRepository;
    this.idTokenValidator = idTokenValidator;
  }

  @PostMapping("/api/logInOrRegister")
  public ResponseEntity<User> logInOrRegister(
      @Valid @RequestBody IdTokenPayload idTokenPayload, HttpSession httpSession) {
    IdentityProvider identityProvider = idTokenPayload.getIdentityProvider();
    String idToken = idTokenPayload.getIdToken();
    Integer sessionUserId = (Integer) httpSession.getAttribute(USER_ID);

    return idTokenValidator
        .validate(identityProvider, idToken)
        .map(validUser ->
            userRepository
                .findByIdentityProviderAndProvidedId(identityProvider, validUser.getProvidedId())
                .map(savedUser -> {
                  if (sessionUserId == null || sessionUserId != savedUser.getId()) {
                    httpSession.setAttribute(USER_ID, savedUser.getId());
                  }
                  return ResponseEntity.ok(savedUser);
                })
                .orElseGet(() -> {
                  User savedUser = userRepository.save(validUser);
                  if (sessionUserId == null || sessionUserId != savedUser.getId()) {
                    httpSession.setAttribute(USER_ID, savedUser.getId());
                  }
                  return ResponseEntity.ok(savedUser);
                }))
        .orElseGet(() -> ResponseEntity.badRequest().build());
  }

  @PostMapping("/api/logOut")
  public void logOut(HttpSession httpSession) {
    httpSession.invalidate();
  }

  @GetMapping("/api/profile")
  public ResponseEntity<User> profile(HttpSession httpSession) {
    Integer userId = (Integer) httpSession.getAttribute(USER_ID);
    if (userId != null) {
      return userRepository
          .findById(userId)
          .map(ResponseEntity::ok)
          .orElseGet(() -> ResponseEntity.notFound().build());
    } else {
      return ResponseEntity.notFound().build();
    }
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
