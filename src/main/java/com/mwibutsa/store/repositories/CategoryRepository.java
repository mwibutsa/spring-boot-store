package com.mwibutsa.store.repositories;

import com.mwibutsa.store.entities.Category;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category, Byte> {
}