package com.chakray.usersapi.service;

import com.chakray.usersapi.dto.AddressDto;
import com.chakray.usersapi.dto.UserResponse;
import com.chakray.usersapi.model.Address;
import com.chakray.usersapi.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Maps between domain entities and API DTOs. The response intentionally omits the password.
 */
@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setPhone(user.getPhone());
        dto.setTaxId(user.getTaxId());
        dto.setCreatedAt(user.getCreatedAt());
        if (user.getAddresses() != null) {
            dto.setAddresses(user.getAddresses().stream()
                    .map(this::toAddressDto)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public List<UserResponse> toResponseList(List<User> users) {
        return users.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public AddressDto toAddressDto(Address address) {
        return new AddressDto(address.getId(), address.getName(),
                address.getStreet(), address.getCountryCode());
    }

    public Address toAddress(AddressDto dto) {
        return new Address(dto.getId(), dto.getName(), dto.getStreet(), dto.getCountryCode());
    }

    public List<Address> toAddressList(List<AddressDto> dtos) {
        if (dtos == null) {
            return new java.util.ArrayList<>();
        }
        return dtos.stream().map(this::toAddress).collect(Collectors.toList());
    }
}
