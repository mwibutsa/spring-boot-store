package com.mwibutsa.store.repositories;

import com.mwibutsa.store.entities.Profile;
import org.springframework.data.repository.CrudRepository;

public interface ProfileRepository extends CrudRepository<Profile, Long> {
}