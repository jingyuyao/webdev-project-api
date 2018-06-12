package com.jingyuyao.webdevprojectapi.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.jingyuyao.webdevprojectapi.models.User;
import com.jingyuyao.webdevprojectapi.models.User.IdentityProvider;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class IdTokenValidator {

  private static final String GOOGLE_CLIENT_ID =
      "941505616508-7942kmf4veq3rh8apuqj8itjch246rgb.apps.googleusercontent.com";

  private final GoogleIdTokenVerifier googleIdTokenVerifier;

  IdTokenValidator() {
    this.googleIdTokenVerifier =
        new GoogleIdTokenVerifier.Builder(
            new NetHttpTransport(), JacksonFactory.getDefaultInstance())
            .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
            .build();
  }

  public Optional<User> validate(IdentityProvider identityProvider, String idToken) {
    switch (identityProvider) {
      case GOOGLE:
        return validateGoogleIdToken(idToken);
      default:
        return Optional.empty();
    }
  }

  private Optional<User> validateGoogleIdToken(String idToken) {
    try {
      return Optional
          .ofNullable(googleIdTokenVerifier.verify(idToken))
          .map(googleIdToken -> {
            Payload payload = googleIdToken.getPayload();
            User user = new User();
            user.setIdentityProvider(IdentityProvider.GOOGLE);
            user.setProvidedId(payload.getSubject());
            user.setEmail(payload.getEmail());
            user.setName((String) payload.get("name"));
            return user;
          });
    } catch (GeneralSecurityException | IOException e) {
      return Optional.empty();
    }
  }
}
