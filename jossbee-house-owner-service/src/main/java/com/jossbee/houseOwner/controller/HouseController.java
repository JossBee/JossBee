package com.jossbee.houseOwner.controller;

import com.jossbee.houseOwner.dto.HouseDto;
import com.jossbee.houseOwner.service.HouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/house")
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
}
