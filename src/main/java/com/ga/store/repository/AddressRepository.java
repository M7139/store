package com.ga.store.repository;

import com.ga.store.model.Address;
import com.ga.store.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressRepository
        extends JpaRepository<Address, Long> {

    Optional<Address> findByUser(User user);

    boolean existsByUser(User user);
}