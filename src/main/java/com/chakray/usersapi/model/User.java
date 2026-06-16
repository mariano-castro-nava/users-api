package com.chakray.usersapi.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Domain entity stored in the in-memory repository.
 * {@code password} holds the AES-256 encrypted value and is never exposed in API responses.
 */
public class User {

    private UUID id;
    private String email;
    private String name;
    private String phone;
    private String password;   // AES-256 encrypted
    private String taxId;
    private String createdAt;  // dd-MM-yyyy HH:mm (Madagascar time zone)
    private List<Address> addresses = new ArrayList<>();

    public User() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }
}
