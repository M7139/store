package com.ga.store.service;

import com.ga.store.dto.AddressRequest;
import com.ga.store.exception.InformationExistsException;
import com.ga.store.model.Address;
import com.ga.store.model.User;
import com.ga.store.repository.AddressRepository;
import org.springframework.stereotype.Service;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserService userService;

    public AddressService(
            AddressRepository addressRepository,
            UserService userService) {

        this.addressRepository = addressRepository;
        this.userService = userService;
    }

    public Address createAddress(
            String email,
            AddressRequest request) {

        User user = userService.getUserByEmail(email);

        if (addressRepository.existsByUser(user)) {
            throw new InformationExistsException(
                    "Home address already exists"
            );
        }

        String area = request.getArea();

        if (area != null) {
            area = area.trim();
        }

        Address address = new Address(
                user,
                request.getHouse().trim(),
                request.getRoad().trim(),
                request.getBlock().trim(),
                area,
                request.getPhoneNumber().trim()
        );

        return addressRepository.save(address);
    }
}