package com.jossbee.houseOwner.service;

import com.jossbee.houseOwner.constants.UpdateType;
import com.jossbee.houseOwner.dto.HouseDto;
import com.jossbee.houseOwner.exception.ServiceException;
import com.jossbee.houseOwner.mapper.HouseMapper;
import com.jossbee.houseOwner.model.Category;
import com.jossbee.houseOwner.model.House;
import com.jossbee.houseOwner.repository.CategoryRepository;
import com.jossbee.houseOwner.repository.HouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HouseService {

    private final HouseMapper houseMapper;
    private final MongoTemplate mongoTemplate;
    private final HouseRepository houseRepository;
    private final CategoryRepository categoryRepository;
    private final KafkaProducerService kafkaProducerService;
    private final JwtTokenDecoderService jwtTokenDecoderService;

    @Transactional
    public HouseDto createNewHouse(String authToken, HouseDto houseDto) {

        String houseOwnerIdentifier = jwtTokenDecoderService.extractIdentifierFormToken(authToken);

        addMetaInformation(houseDto, houseOwnerIdentifier);

        House house = houseRepository.save(houseMapper.convertDtoToModel(houseDto));

        HouseDto houseDto1 = houseMapper.convertModelToDto(house);

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

    public List<HouseDto> searchHouse(Double priceRangeMin, Double priceRangeMax, Integer guestNumber, String title,
                                      String description, Integer numberOfBeds, Integer numberOfBathrooms, String category, String[] amenities) {

        List<House> houses;
        Query query = new Query();

        Criteria criteria = getCriteria(priceRangeMin, priceRangeMax, guestNumber, title, description, numberOfBeds,
                numberOfBathrooms, category, amenities);

        query.addCriteria(criteria);

        houses = mongoTemplate.find(query, House.class);

        return houses.stream()
                .map(houseMapper::convertModelToDto)
                .toList();
    }

    private Criteria getCriteria(Double priceRangeMin, Double priceRangeMax, Integer guestNumber, String title,
                                 String description, Integer numberOfBeds, Integer numberOfBathrooms, String categoryName,
                                 String[] amenities) {

        Criteria criteria = new Criteria();

        if (priceRangeMin != null && priceRangeMax != null && priceRangeMin > 0 && priceRangeMax > 0) {
            criteria = criteria.andOperator(
                    Criteria.where("pricePerNight").gte(priceRangeMin),
                    Criteria.where("pricePerNight").lte(priceRangeMax)
            );
        }

        if (guestNumber != null && guestNumber > 0) {
            criteria = criteria.and("guestsCapacity").gte(guestNumber);
        }

        if (title != null && !title.isEmpty()) {
            criteria = criteria.and("title").regex(title, "i");
        }

        if (description != null && !description.isEmpty()) {
            criteria = criteria.and("description").regex(description, "i");
        }

        if (numberOfBeds != null && numberOfBeds > 0) {
            criteria = criteria.and("numberOfBedRooms").gte(numberOfBeds);
        }

        if (numberOfBathrooms != null && numberOfBathrooms > 0) {
            criteria = criteria.and("numberOfBathrooms").gte(numberOfBathrooms);
        }

        if (amenities != null && amenities.length > 0) {
            criteria = criteria.and("amenities").all(Arrays.asList(amenities));
        }

        if (!StringUtils.isEmpty(categoryName)) {
            Category category = categoryRepository.findCategoryByName(categoryName);
            if (category != null) {
                criteria = criteria.and("category.id").is(category.getId());
            } else {
                log.error("Failed to find category with name: " + categoryName);
            }
        }

        return criteria;
    }
}
