package com.jossbee.houseOwner.service;

import com.jossbee.houseOwner.dto.HouseDto;
import com.jossbee.houseOwner.exception.ServiceException;
import com.jossbee.houseOwner.model.House;
import com.jossbee.houseOwner.repository.HouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HouseService {

    private final HouseRepository houseRepository;
    private final HouseConverterService houseConverterService;
    private final JwtTokenDecoderService jwtTokenDecoderService;

    @Transactional
    public HouseDto createNewHouse(String authToken, HouseDto houseDto) {

        String houseOwnerIdentifier = jwtTokenDecoderService.extractIdentifierFormToken(authToken);

        addMetaInformation(houseDto, houseOwnerIdentifier);

        House house = houseRepository.save(houseConverterService.convertDtoToModel(houseDto));

        return houseConverterService.convertModelToDto(house);
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
    }

    public List<HouseDto> getAllRegisteredHouses(String authToken, String houseId, String title) {
        String houseOwnerIdentifier = jwtTokenDecoderService.extractIdentifierFormToken(authToken);

        List<House> houses = houseRepository.findActiveHousesByOptionalCriteria(houseId, title, houseOwnerIdentifier);

        return houses.stream()
                .map(houseConverterService::convertModelToDto)
                .toList();
    }
}
