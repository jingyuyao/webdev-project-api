package com.jingyuyao.webdevprojectapi.repositories;

import com.jingyuyao.webdevprojectapi.models.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {

}
