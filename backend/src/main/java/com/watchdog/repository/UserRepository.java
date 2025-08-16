package com.watchdog.repository;

import com.watchdog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a User by their email address.
     * Spring Data JPA automatically generates the query for this method.
     * @param email The email address of the user.
     * @return An Optional containing the User if found, otherwise empty.
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user with the given email exists.
     * @param email The email address to check.
     * @return true if a user with this email exists, false otherwise.
     */
    boolean existsByEmail(String email);
}