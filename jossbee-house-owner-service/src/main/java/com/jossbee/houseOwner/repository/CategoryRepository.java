package com.jossbee.houseOwner.repository;

import com.jossbee.houseOwner.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {
    @Query("{'name': ?0}")
    Category findCategoryByName(String categoryName);
}
