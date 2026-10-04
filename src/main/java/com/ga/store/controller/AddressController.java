package com.ga.store.controller;

import com.ga.store.dto.AddressRequest;
import com.ga.store.dto.AddressResponse;
import com.ga.store.model.Address;
import com.ga.store.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(
            AddressService addressService) {

        this.addressService = addressService;
    }

    @PostMapping("/me")
    public ResponseEntity<AddressResponse> createAddress(
            Authentication authentication,
            @Valid @RequestBody AddressRequest request) {

        String email = authentication.getName();

        Address address = addressService.createAddress(
                email,
                request
        );

        AddressResponse response =
                createAddressResponse(address);

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    @GetMapping("/me")
    public ResponseEntity<AddressResponse> getAddress(
            Authentication authentication) {

        String email = authentication.getName();

        Address address =
                addressService.getAddressByUser(email);

        AddressResponse response =
                createAddressResponse(address);

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @PutMapping("/me")
    public ResponseEntity<AddressResponse> updateAddress(
            Authentication authentication,
            @Valid @RequestBody AddressRequest request) {

        String email = authentication.getName();

        Address address =
                addressService.updateAddress(
                        email,
                        request
                );

        AddressResponse response =
                createAddressResponse(address);

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @DeleteMapping("/me")
    public ResponseEntity<String> deleteAddress(
            Authentication authentication) {

        String email = authentication.getName();

        addressService.deleteAddress(email);

        return new ResponseEntity<>(
                "Home address deleted successfully",
                HttpStatus.OK
        );
    }

    private AddressResponse createAddressResponse(
            Address address) {

        return new AddressResponse(
                address.getId(),
                address.getHouse(),
                address.getRoad(),
                address.getBlock(),
                address.getArea(),
                address.getPhoneNumber()
        );
    }
}