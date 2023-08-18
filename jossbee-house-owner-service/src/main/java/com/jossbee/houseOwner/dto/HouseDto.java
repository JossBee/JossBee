package com.jossbee.houseOwner.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(NON_NULL)
public class HouseDto {

    private String id;

    private Boolean isActive;

    private LocalDateTime createdAt;

    private String createdBy;

    private LocalDateTime updatedAt;

    private String updatedBy;

    @NotBlank(message = "Title is mandatory")
    @NotEmpty(message = "Title is mandatory")
    private String title;

    @NotBlank(message = "Description is mandatory")
    @NotEmpty(message = "Description is mandatory")
    private String description;

    @Positive(message = "Number of bedrooms must be positive")
    private int numberOfBedRooms;

    @Positive(message = "Number of bathrooms must be positive")
    private int numberOfBathrooms;

    @Positive(message = "Guests capacity must be positive")
    private int guestsCapacity;

    @Positive(message = "Price per night must be positive")
    private double pricePerNight;

    @Min(value = 0, message = "Discount value must be greater than or equal to 0")
    private double discount;

    @Valid
    private HostDto host;

    @Valid
    private AddressDto address;

    @Valid
    private CategoryDto category;

    private List<@NotBlank(message = "Amenity cannot be blank") String> amenities;

    private List<@NotBlank(message = "Photo URL cannot be blank") String> photos;
}
