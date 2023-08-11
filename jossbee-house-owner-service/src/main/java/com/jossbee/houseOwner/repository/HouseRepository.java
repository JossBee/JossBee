package com.jossbee.houseOwner.repository;

import com.jossbee.houseOwner.model.House;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HouseRepository extends MongoRepository<House, String> {

    @Query(value = "{ 'host.hostUuid' : ?0, 'id' : ?1, 'isActive' : true }")
    Optional<House> findHouseByOwnerIdAndHouseId(String houseOwnerIdentifier, String houseId);

    @Query("{$and: [" +
            "{'_id': ?0}, " +
            "{'title': ?1}, " +
            "'host.hostUuid': ?2, " +
            "'active': true" +
            "]}")
    List<House> findActiveHousesByOptionalCriteria(String houseId, String title, String hostUuid);
}
