package com.chakray.usersapi.service;

import com.chakray.usersapi.dto.CreateUserRequest;
import com.chakray.usersapi.dto.UpdateUserRequest;
import com.chakray.usersapi.exception.DuplicateTaxIdException;
import com.chakray.usersapi.exception.InvalidQueryException;
import com.chakray.usersapi.exception.ResourceNotFoundException;
import com.chakray.usersapi.model.User;
import com.chakray.usersapi.repository.UserRepository;
import com.chakray.usersapi.security.Aes256Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Business logic for the /users resource: listing (sort + filter) and CRUD.
 */
@Service
public class UserService {

    private static final Set<String> SORTABLE =
            Set.of("email", "id", "name", "phone", "tax_id", "created_at");
    private static final Set<String> OPERATORS = Set.of("co", "eq", "sw", "ew");

    private final UserRepository repository;
    private final Aes256Util aes256Util;
    private final ZoneId zoneId;
    private final DateTimeFormatter formatter;

    public UserService(UserRepository repository,
                       Aes256Util aes256Util,
                       @Value("${app.timezone}") String timezone,
                       @Value("${app.datetime-format}") String dateFormat) {
        this.repository = repository;
        this.aes256Util = aes256Util;
        this.zoneId = ZoneId.of(timezone);
        this.formatter = DateTimeFormatter.ofPattern(dateFormat);
    }

    /**
     * Returns the users, optionally filtered and/or sorted.
     */
    public List<User> getUsers(String sortedBy, String filter) {
        List<User> result = repository.findAll();

        if (StringUtils.hasText(filter)) {
            result = applyFilter(result, filter);
        }
        if (StringUtils.hasText(sortedBy)) {
            result = applySort(result, sortedBy.trim());
        }
        return result;
    }

    public User create(CreateUserRequest request, UserMapper mapper) {
        if (repository.existsByTaxId(request.getTaxId())) {
            throw new DuplicateTaxIdException("tax_id already exists: " + request.getTaxId());
        }

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setTaxId(request.getTaxId());
        user.setPassword(aes256Util.encrypt(request.getPassword()));
        user.setCreatedAt(ZonedDateTime.now(zoneId).format(formatter));
        user.setAddresses(mapper.toAddressList(request.getAddresses()));

        return repository.save(user);
    }

    public User update(UUID id, UpdateUserRequest request, UserMapper mapper) {
        User user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));

        if (request.getTaxId() != null && !request.getTaxId().equals(user.getTaxId())) {
            if (repository.existsByTaxId(request.getTaxId())) {
                throw new DuplicateTaxIdException("tax_id already exists: " + request.getTaxId());
            }
            user.setTaxId(request.getTaxId());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getPassword() != null) {
            user.setPassword(aes256Util.encrypt(request.getPassword()));
        }
        if (request.getAddresses() != null) {
            user.setAddresses(mapper.toAddressList(request.getAddresses()));
        }

        return repository.save(user);
    }

    public void delete(UUID id) {
        boolean removed = repository.deleteById(id);
        if (!removed) {
            throw new ResourceNotFoundException("User not found: " + id);
        }
    }

    // ------------------------------------------------------------------ filter

    private List<User> applyFilter(List<User> users, String filter) {
        String[] parts = filter.trim().split("\\s+", 3);
        if (parts.length < 3) {
            throw new InvalidQueryException(
                    "filter must follow the format: attribute operator value (e.g. name co user)");
        }
        String attribute = parts[0];
        String operator = parts[1];
        String value = parts[2];

        if (!SORTABLE.contains(attribute)) {
            throw new InvalidQueryException("Unknown filter attribute: " + attribute);
        }
        if (!OPERATORS.contains(operator)) {
            throw new InvalidQueryException("Unknown filter operator: " + operator);
        }

        return users.stream()
                .filter(u -> matches(attributeValue(u, attribute), operator, value))
                .collect(Collectors.toList());
    }

    private boolean matches(String attributeValue, String operator, String value) {
        if (attributeValue == null) {
            return false;
        }
        switch (operator) {
            case "co":
                return attributeValue.contains(value);
            case "eq":
                return attributeValue.equals(value);
            case "sw":
                return attributeValue.startsWith(value);
            case "ew":
                return attributeValue.endsWith(value);
            default:
                throw new InvalidQueryException("Unknown filter operator: " + operator);
        }
    }

    // -------------------------------------------------------------------- sort

    private List<User> applySort(List<User> users, String sortedBy) {
        if (!SORTABLE.contains(sortedBy)) {
            throw new InvalidQueryException("Cannot sort by unknown attribute: " + sortedBy);
        }

        Comparator<User> comparator;
        if ("created_at".equals(sortedBy)) {
            comparator = Comparator.comparing(this::parseCreatedAt,
                    Comparator.nullsLast(Comparator.naturalOrder()));
        } else {
            comparator = Comparator.comparing(u -> attributeValue(u, sortedBy),
                    Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
        }

        List<User> sorted = new ArrayList<>(users);
        sorted.sort(comparator);
        return sorted;
    }

    private java.time.LocalDateTime parseCreatedAt(User user) {
        if (user.getCreatedAt() == null) {
            return null;
        }
        try {
            return java.time.LocalDateTime.parse(user.getCreatedAt(), formatter);
        } catch (Exception e) {
            return null;
        }
    }

    // ---------------------------------------------------------------- helpers

    private String attributeValue(User user, String attribute) {
        switch (attribute) {
            case "email":
                return user.getEmail();
            case "id":
                return user.getId() == null ? null : user.getId().toString();
            case "name":
                return user.getName();
            case "phone":
                return user.getPhone();
            case "tax_id":
                return user.getTaxId();
            case "created_at":
                return user.getCreatedAt();
            default:
                throw new InvalidQueryException("Unknown attribute: " + attribute);
        }
    }

    // exposed for the authentication service / tests
    public java.util.Optional<User> findByTaxId(String taxId) {
        return repository.findByTaxId(taxId);
    }
}
