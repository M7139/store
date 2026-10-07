package com.ga.store.controller;

import com.ga.store.dto.AddressRequest;
import com.ga.store.dto.AddressResponse;
import com.ga.store.model.Address;
import com.ga.store.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/addresses")
@Tag(
        name = "Addresses",
        description = "Customer home address management"
)
@SecurityRequirement(name = "bearerAuth")
public class AddressController {

    private final AddressService addressService;

    public AddressController(
            AddressService addressService) {

        this.addressService = addressService;
    }

    @PostMapping("/me")
    @Operation(
            summary = "Create home address",
            description = "Creates a home address for the currently authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Address created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid address information"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "User already has an address"
            )
    })
    public ResponseEntity<AddressResponse> createAddress(
            Authentication authentication,
            @Valid @RequestBody AddressRequest request) {

        String email =
                authentication.getName();

        Address address =
                addressService.createAddress(
                        email,
                        request
                );

        AddressResponse response =
                createAddressResponse(
                        address
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    @GetMapping("/me")
    @Operation(
            summary = "Get home address",
            description = "Returns the home address of the currently authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Address returned successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Address not found"
            )
    })
    public ResponseEntity<AddressResponse> getAddress(
            Authentication authentication) {

        String email =
                authentication.getName();

        Address address =
                addressService.getAddressByUser(
                        email
                );

        AddressResponse response =
                createAddressResponse(
                        address
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @PutMapping("/me")
    @Operation(
            summary = "Update home address",
            description = "Updates the home address of the currently authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Address updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid address information"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Address not found"
            )
    })
    public ResponseEntity<AddressResponse> updateAddress(
            Authentication authentication,
            @Valid @RequestBody AddressRequest request) {

        String email =
                authentication.getName();

        Address address =
                addressService.updateAddress(
                        email,
                        request
                );

        AddressResponse response =
                createAddressResponse(
                        address
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @DeleteMapping("/me")
    @Operation(
            summary = "Delete home address",
            description = "Deletes the home address of the currently authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Address deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Address not found"
            )
    })
    public ResponseEntity<String> deleteAddress(
            Authentication authentication) {

        String email =
                authentication.getName();

        addressService.deleteAddress(
                email
        );

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