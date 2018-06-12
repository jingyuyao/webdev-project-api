package com.jingyuyao.webdevprojectapi.repositories;

import com.jingyuyao.webdevprojectapi.models.User;
import com.jingyuyao.webdevprojectapi.models.User.IdentityProvider;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {

  Optional<User> findByIdentityProviderAndProvidedId(
      IdentityProvider identityProvider, String providedId);
}
