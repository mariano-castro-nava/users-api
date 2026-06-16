package com.chakray.usersapi.repository;

import com.chakray.usersapi.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * In-memory user store backed by a thread-safe list (the "array" from the spec).
 */
@Repository
public class UserRepository {

    private final List<User> users = new CopyOnWriteArrayList<>();

    public List<User> findAll() {
        return new java.util.ArrayList<>(users);
    }

    public Optional<User> findById(UUID id) {
        return users.stream().filter(u -> u.getId().equals(id)).findFirst();
    }

    public Optional<User> findByTaxId(String taxId) {
        return users.stream()
                .filter(u -> u.getTaxId() != null && u.getTaxId().equals(taxId))
                .findFirst();
    }

    public boolean existsByTaxId(String taxId) {
        return findByTaxId(taxId).isPresent();
    }

    public User save(User user) {
        // replace if an entity with the same id already exists, otherwise append
        Optional<User> existing = (user.getId() == null) ? Optional.empty() : findById(user.getId());
        existing.ifPresent(users::remove);
        users.add(user);
        return user;
    }

    public boolean deleteById(UUID id) {
        return users.removeIf(u -> u.getId().equals(id));
    }

    public long count() {
        return users.size();
    }
}
