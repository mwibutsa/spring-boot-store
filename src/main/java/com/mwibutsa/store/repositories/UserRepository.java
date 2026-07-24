package com.mwibutsa.store.repositories;

import com.mwibutsa.store.entities.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Override
    @EntityGraph(attributePaths = {"profile"})
    List<User> findAll(Sort sort);

    
}
