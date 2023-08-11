package com.jossbee.houseOwner.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressDto {

    private String street;

    private double latitude;

    private double longitude;

    @NotEmpty(message = "City is mandatory")
    @NotBlank(message = "City is mandatory")
    private String city;

    @NotBlank(message = "State is mandatory")
    private String state;

    @NotBlank(message = "Country is mandatory")
    @NotEmpty(message = "Country is mandatory")
    private String country;

    @NotBlank(message = "Zip code is mandatory")
    @NotEmpty(message = "Zip code is mandatory")
    private String zipCode;
}
