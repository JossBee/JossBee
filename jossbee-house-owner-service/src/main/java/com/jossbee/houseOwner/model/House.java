package com.jossbee.houseOwner.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "house")
public class House {

    @Id
    private String id;

    @Field("active")
    private Boolean isActive;

    private LocalDateTime createdAt;

    @NotBlank(message = "Created by is mandatory")
    @NotEmpty(message = "Created by is mandatory")
    private String createdBy;

    private LocalDateTime updatedAt;

    private String updatedBy;

    @Field("title")
    @NotBlank(message = "Title is mandatory")
    @NotEmpty(message = "Title is mandatory")
    private String title;

    @Field("description")
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
    private Host host;

    @Valid
    private Address address;

    @DBRef
    @NotNull(message = "Category must be provided for house")
    private Category category;

    private List<@NotBlank(message = "Amenity cannot be blank") String> amenities;

    private List<@NotBlank(message = "Photo URL cannot be blank") String> photos;
}
