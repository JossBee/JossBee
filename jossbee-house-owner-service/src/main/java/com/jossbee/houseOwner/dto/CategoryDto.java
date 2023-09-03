package com.jossbee.houseOwner.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {

    private String id;

    @NotBlank(message = "Must provide category image url")
    @NotNull(message = "Must provide category image url")
    private String imageUrl;

    @NotBlank(message = "Category name must be given")
    @NotNull(message = "Category name must be given")
    private String name;
}
