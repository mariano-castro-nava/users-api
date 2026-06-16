package com.chakray.usersapi.model;

/**
 * Address sub-resource of a {@link User}.
 * The JSON attribute {@code country_code} is mapped automatically thanks to the
 * global SNAKE_CASE Jackson naming strategy configured in application.yml.
 */
public class Address {

    private Long id;
    private String name;
    private String street;
    private String countryCode;

    public Address() {
    }

    public Address(Long id, String name, String street, String countryCode) {
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
