package com.jossbee.houseOwner.service;

import com.jossbee.houseOwner.constants.UpdateType;
import com.jossbee.houseOwner.dto.HouseDto;
import com.jossbee.houseOwner.exception.ServiceException;
import com.jossbee.houseOwner.mapper.HouseMapper;
import com.jossbee.houseOwner.model.House;
import com.jossbee.houseOwner.repository.HouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HouseService {

    private final MongoTemplate mongoTemplate;
    private final HouseRepository houseRepository;
    private final HouseMapper houseMapper;
    private final KafkaProducerService kafkaProducerService;
    private final JwtTokenDecoderService jwtTokenDecoderService;

    @Transactional
    public HouseDto createNewHouse(String authToken, HouseDto houseDto) {

        String houseOwnerIdentifier = jwtTokenDecoderService.extractIdentifierFormToken(authToken);

        addMetaInformation(houseDto, houseOwnerIdentifier);

        House house = houseRepository.save(houseMapper.convertDtoToModel(houseDto));

        HouseDto houseDto1 = houseMapper.convertModelToDto(house);

        //Produce to kafka for elastic search indexing
        kafkaProducerService.publishHouseDtoToKafka(houseDto1, UpdateType.CREATE.name());

        return houseDto1;
    }


    private void addMetaInformation(HouseDto houseDto, String userId) {
        houseDto.setIsActive(true);
        houseDto.setCreatedBy(userId);
        houseDto.setCreatedAt(LocalDateTime.now());

        fetchUserInformation(houseDto, userId);
    }

    private void fetchUserInformation(HouseDto houseDto, String userId) {
        //TODO: Add code to fetch user-related information from auth-server.
    }

    public void deleteHouse(String authToken, String houseId) {
        String houseOwnerIdentifier = jwtTokenDecoderService.extractIdentifierFormToken(authToken);

        House house = houseRepository.findHouseByOwnerIdAndHouseId(houseOwnerIdentifier, houseId)
                .orElseThrow(() -> new ServiceException("Failed to find house with given houseId: " + houseId));

        house.setIsActive(false);
        house.setUpdatedAt(LocalDateTime.now());
        house.setUpdatedBy(houseOwnerIdentifier);

        houseRepository.save(house);

        HouseDto houseDto1 = houseMapper.convertModelToDto(house);
        kafkaProducerService.publishHouseDtoToKafka(houseDto1, UpdateType.DELETE.name());
    }

    public List<HouseDto> getAllRegisteredHouses(String authToken, String title, String houseId) {

        String houseOwnerId = jwtTokenDecoderService.extractIdentifierFormToken(authToken);

        List<House> houses;
        Query query = new Query();

        Criteria criteria = Criteria.where("host.hostUuid").is(houseOwnerId);

        if (title != null && !title.isEmpty()) {
            criteria = criteria.and("title").regex(title, "i");
        }

        if (houseId != null && !houseId.isEmpty()) {
            criteria = criteria.and("_id").is(houseId);
        }

        query.addCriteria(criteria);

        houses = mongoTemplate.find(query, House.class);

        return houses.stream()
                .map(houseMapper::convertModelToDto)
                .toList();
    }

    public List<HouseDto> getAllRegisteredHouses() {

        List<House> houses;
        Query query = new Query();

        houses = mongoTemplate.find(query, House.class);

        return houses.stream()
                .map(houseMapper::convertModelToDto)
                .toList();
    }
}
