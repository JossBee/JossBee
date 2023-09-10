package com.jossbee.houseOwner.controller;

import com.jossbee.houseOwner.dto.HouseDto;
import com.jossbee.houseOwner.service.HouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/house/v1")
@RequiredArgsConstructor
public class HouseController {

    private final HouseService houseService;

    @PostMapping
    public ResponseEntity<HouseDto> createNewHouse(@RequestHeader("authToken") String authToken, @RequestBody @Valid HouseDto houseDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(houseService.createNewHouse(authToken, houseDto));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteHouse(@RequestHeader("authToken") String authToken, @RequestParam("id") String houseId) {
        houseService.deleteHouse(authToken, houseId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<HouseDto>> getAllRegisteredHouse(@RequestHeader("authToken") String authToken,
                                                                @RequestParam(name = "title", required = false) String title,
                                                                @RequestParam(name = "houseId", required = false) String houseId) {
        List<HouseDto> houses = houseService.getAllRegisteredHouses(authToken, title, houseId);
        return ResponseEntity.status(HttpStatus.OK).body(houses);
    }

    @GetMapping("/all")
    public ResponseEntity<List<HouseDto>> getAllHouses() {
        List<HouseDto> houses = houseService.getAllRegisteredHouses();
        return ResponseEntity.status(HttpStatus.OK).body(houses);
    }

    @GetMapping("/search-house")
    public ResponseEntity<List<HouseDto>> searchHouse(@RequestParam(name = "price_range_min", required = false) Double priceRangeMin,
                                                      @RequestParam(name = "price_range_max", required = false) Double priceRangeMax,
                                                      @RequestParam(name = "guest_number", required = false) Integer guestNumber,
                                                      @RequestParam(name = "title", required = false) String title,
                                                      @RequestParam(name = "description", required = false) String description,
                                                      @RequestParam(name = "number_of_beds", required = false) Integer numberOfBeds,
                                                      @RequestParam(name = "number_of_bathrooms", required = false) Integer numberOfBathrooms,
                                                      @RequestParam(name = "category", required = false) String category,
                                                      @RequestParam(name = "amenities", required = false) String[] amenities) {

        List<HouseDto> houses = houseService.searchHouse(priceRangeMin, priceRangeMax, guestNumber, title, description,
                numberOfBeds, numberOfBathrooms, category, amenities);
        return ResponseEntity.status(HttpStatus.OK).body(houses);
    }
}
