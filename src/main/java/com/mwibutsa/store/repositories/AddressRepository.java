package com.mwibutsa.store.repositories;

import com.mwibutsa.store.entities.Address;
import org.springframework.data.repository.CrudRepository;

public interface AddressRepository extends CrudRepository<Address, Long> {
}