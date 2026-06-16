package com.chakray.usersapi.dto;

import com.chakray.usersapi.validation.AndresFormat;
import com.chakray.usersapi.validation.ValidationPatterns;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.ArrayList;
import java.util.List;

/**
 * Payload for POST /users.
 */
public class CreateUserRequest {

    @NotBlank
    @Email(message = "email must be a well formed address")
    private String email;

    @NotBlank
    private String name;

    @NotBlank
    @AndresFormat
    private String phone;

    @NotBlank
    private String password;

    @NotBlank
    @Pattern(regexp = ValidationPatterns.RFC, message = "tax_id must have a valid RFC format")
    private String taxId;

    @Valid
    private List<AddressDto> addresses = new ArrayList<>();

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

    public List<AddressDto> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressDto> addresses) {
        this.addresses = addresses;
    }
}
