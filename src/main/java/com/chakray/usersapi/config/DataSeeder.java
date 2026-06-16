package com.chakray.usersapi.config;

import com.chakray.usersapi.model.Address;
import com.chakray.usersapi.model.User;
import com.chakray.usersapi.repository.UserRepository;
import com.chakray.usersapi.security.Aes256Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Loads the 3 demo users into the in-memory repository at startup.
 * Passwords are stored AES-256 encrypted. The plaintext credentials are listed
 * in the README so the /login endpoint can be tested.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository repository;
    private final Aes256Util aes256Util;

    public DataSeeder(UserRepository repository, Aes256Util aes256Util) {
        this.repository = repository;
        this.aes256Util = aes256Util;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }

        repository.save(buildUser(
                "user1@mail.com", "user1", "+1 55 555 555 55",
                "password1", "AARR990101XXX", "01-01-2026 00:00",
                List.of(
                        new Address(1L, "workaddress", "street No. 1", "UK"),
                        new Address(2L, "homeaddress", "street No. 2", "AU"))));

        repository.save(buildUser(
                "user2@mail.com", "user2", "+52 5512345678",
                "password2", "BBSS880202YYY", "15-02-2026 10:30",
                List.of(
                        new Address(1L, "workaddress", "street No. 10", "ES"))));

        repository.save(buildUser(
                "user3@mail.com", "user3", "5523456789",
                "password3", "CCTT770303ZZZ", "20-03-2026 14:45",
                List.of(
                        new Address(1L, "homeaddress", "street No. 20", "US"))));

        log.info("Seeded {} demo users into the in-memory store", repository.count());
    }

    private User buildUser(String email, String name, String phone, String plainPassword,
                           String taxId, String createdAt, List<Address> addresses) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(email);
        user.setName(name);
        user.setPhone(phone);
        user.setPassword(aes256Util.encrypt(plainPassword));
        user.setTaxId(taxId);
        user.setCreatedAt(createdAt);
        user.setAddresses(new java.util.ArrayList<>(addresses));
        return user;
    }
}
