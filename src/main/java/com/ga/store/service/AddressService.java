package com.ga.store.service;

import com.ga.store.dto.AddressRequest;
import com.ga.store.exception.InformationExistsException;
import com.ga.store.exception.InformationNotFoundException;
import com.ga.store.model.Address;
import com.ga.store.model.User;
import com.ga.store.repository.AddressRepository;
import org.springframework.stereotype.Service;

/**
 * Handles customer home address management.
 */
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

    /**
     * Creates a home address for a user.
     *
     * @param email authenticated user's email
     * @param request address information
     * @return created address
     * @throws InformationExistsException if the user already has an address
     */
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

    /**
     * Returns the home address belonging to a user.
     *
     * @param email authenticated user's email
     * @return user's saved address
     * @throws InformationNotFoundException if no address exists
     */
    public Address getAddressByUser(String email) {

        User user = userService.getUserByEmail(email);

        return addressRepository.findByUser(user)
                .orElseThrow(() ->
                        new InformationNotFoundException(
                                "Home address not found"
                        ));
    }

    /**
     * Updates a user's existing home address.
     *
     * @param email authenticated user's email
     * @param request updated address information
     * @return updated address
     */
    public Address updateAddress(
            String email,
            AddressRequest request) {

        Address address = getAddressByUser(email);

        String area = request.getArea();

        if (area != null) {
            area = area.trim();
        }

        address.setHouse(
                request.getHouse().trim()
        );

        address.setRoad(
                request.getRoad().trim()
        );

        address.setBlock(
                request.getBlock().trim()
        );

        address.setArea(area);

        address.setPhoneNumber(
                request.getPhoneNumber().trim()
        );

        return addressRepository.save(address);
    }

    /**
     * Deletes the current user's saved home address.
     *
     * @param email authenticated user's email
     */
    public void deleteAddress(String email) {

        Address address = getAddressByUser(email);

        addressRepository.delete(address);
    }
}