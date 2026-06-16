package com.chakray.usersapi.service;

import com.chakray.usersapi.dto.CreateUserRequest;
import com.chakray.usersapi.dto.UpdateUserRequest;
import com.chakray.usersapi.exception.DuplicateTaxIdException;
import com.chakray.usersapi.exception.InvalidQueryException;
import com.chakray.usersapi.exception.ResourceNotFoundException;
import com.chakray.usersapi.model.User;
import com.chakray.usersapi.repository.UserRepository;
import com.chakray.usersapi.security.Aes256Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;
    @Mock
    private Aes256Util aes256Util;

    private UserService service;
    private final UserMapper mapper = new UserMapper();

    @BeforeEach
    void setUp() {
        service = new UserService(repository, aes256Util, "Indian/Antananarivo", "dd-MM-yyyy HH:mm");
    }

    private User user(String name, String email, String phone, String taxId, String createdAt) {
        User u = new User();
        u.setId(UUID.randomUUID());
        u.setName(name);
        u.setEmail(email);
        u.setPhone(phone);
        u.setTaxId(taxId);
        u.setCreatedAt(createdAt);
        return u;
    }

    private List<User> sample() {
        return List.of(
                user("user1", "user1@mail.com", "5511111111", "AARR990101XXX", "01-01-2026 00:00"),
                user("user2", "user2@mail.com", "5522222222", "BBSS880202YYY", "15-02-2026 10:30"),
                user("admin", "admin@chakray.com", "5533333333", "CCTT770303ZZZ", "20-03-2026 14:45"));
    }

    // -------------------------------------------------------------- create

    @Test
    void createEncryptsPasswordSetsTimestampAndPersists() {
        when(repository.existsByTaxId("AARR990101XXX")).thenReturn(false);
        when(aes256Util.encrypt("plain")).thenReturn("ENCRYPTED");
        when(repository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        CreateUserRequest req = new CreateUserRequest();
        req.setEmail("user1@mail.com");
        req.setName("user1");
        req.setPhone("+1 55 555 555 55");
        req.setPassword("plain");
        req.setTaxId("AARR990101XXX");

        User created = service.create(req, mapper);

        assertNotNull(created.getId());
        assertEquals("ENCRYPTED", created.getPassword());
        assertNotNull(created.getCreatedAt());
        verify(repository).save(any(User.class));
    }

    @Test
    void createRejectsDuplicateTaxId() {
        when(repository.existsByTaxId("AARR990101XXX")).thenReturn(true);

        CreateUserRequest req = new CreateUserRequest();
        req.setTaxId("AARR990101XXX");
        req.setPassword("plain");

        assertThrows(DuplicateTaxIdException.class, () -> service.create(req, mapper));
    }

    // -------------------------------------------------------------- filter

    @Test
    void filterContains() {
        when(repository.findAll()).thenReturn(sample());
        assertEquals(2, service.getUsers(null, "name co user").size());
    }

    @Test
    void filterEquals() {
        when(repository.findAll()).thenReturn(sample());
        List<User> result = service.getUsers(null, "tax_id eq AARR990101XXX");
        assertEquals(1, result.size());
        assertEquals("user1", result.get(0).getName());
    }

    @Test
    void filterStartsWith() {
        when(repository.findAll()).thenReturn(sample());
        assertEquals(3, service.getUsers(null, "phone sw 55").size());
    }

    @Test
    void filterEndsWith() {
        when(repository.findAll()).thenReturn(sample());
        assertEquals(2, service.getUsers(null, "email ew mail.com").size());
    }

    @Test
    void filterWithBadFormatThrows() {
        lenient().when(repository.findAll()).thenReturn(sample());
        assertThrows(InvalidQueryException.class, () -> service.getUsers(null, "name co"));
        assertThrows(InvalidQueryException.class, () -> service.getUsers(null, "name xx user"));
        assertThrows(InvalidQueryException.class, () -> service.getUsers(null, "unknown co x"));
    }

    // -------------------------------------------------------------- sort

    @Test
    void sortByNameAscending() {
        when(repository.findAll()).thenReturn(sample());
        List<User> result = service.getUsers("name", null);
        assertEquals("admin", result.get(0).getName());
        assertEquals("user2", result.get(2).getName());
    }

    @Test
    void sortByCreatedAtChronologically() {
        when(repository.findAll()).thenReturn(sample());
        List<User> result = service.getUsers("created_at", null);
        assertEquals("01-01-2026 00:00", result.get(0).getCreatedAt());
        assertEquals("20-03-2026 14:45", result.get(2).getCreatedAt());
    }

    @Test
    void sortByUnknownAttributeThrows() {
        lenient().when(repository.findAll()).thenReturn(sample());
        assertThrows(InvalidQueryException.class, () -> service.getUsers("unknown", null));
    }

    @Test
    void emptySortAndFilterReturnsAll() {
        when(repository.findAll()).thenReturn(sample());
        assertEquals(3, service.getUsers(null, null).size());
        assertEquals(3, service.getUsers("", "").size());
    }

    // -------------------------------------------------------------- update / delete

    @Test
    void updateAppliesOnlyProvidedFields() {
        User existing = user("user1", "user1@mail.com", "5511111111", "AARR990101XXX", "01-01-2026 00:00");
        when(repository.findById(existing.getId())).thenReturn(Optional.of(existing));
        when(aes256Util.encrypt(anyString())).thenReturn("NEW_ENC");
        when(repository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateUserRequest req = new UpdateUserRequest();
        req.setName("renamed");
        req.setPassword("newpass");

        User updated = service.update(existing.getId(), req, mapper);

        assertEquals("renamed", updated.getName());
        assertEquals("NEW_ENC", updated.getPassword());
        assertEquals("user1@mail.com", updated.getEmail()); // unchanged
    }

    @Test
    void updateMissingUserThrows() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> service.update(id, new UpdateUserRequest(), mapper));
    }

    @Test
    void deleteMissingUserThrows() {
        UUID id = UUID.randomUUID();
        when(repository.deleteById(id)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> service.delete(id));
    }
}
