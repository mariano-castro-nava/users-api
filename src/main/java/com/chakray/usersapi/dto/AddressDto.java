package com.chakray.usersapi.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Address payload used in both requests and responses.
 * country_code <-> countryCode mapping is handled by the global SNAKE_CASE strategy.
 */
public class AddressDto {

    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String street;

    @NotBlank
    private String countryCode;

    public AddressDto() {
    }

    public AddressDto(Long id, String name, String street, String countryCode) {
        this.id = id;
        this.name = name;
        this.street = street;
        this.countryCode = countryCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
