package com.jossbee.houseOwner.repository;

import com.jossbee.houseOwner.model.House;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HouseRepository extends MongoRepository<House, String> {

    Optional<House> findByHouseOwnerIdAndHouseId(String houseOwnerIdentifier, String houseId);
}
