package io.github.abbassizied.sms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.abbassizied.sms.entities.User;


public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmailIgnoreCase(String email);

}
