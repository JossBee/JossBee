package com.jossbee.houseOwner.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "categories")
public class Category {

    @Id
    private String id;

    @NotNull(message = "Must provide image url for category")
    @NotBlank(message = "Must provide image url for category")
    private String imageUrl;

    @NotNull(message = "Category name can not be empty")
    @NotBlank(message = "Category name can not be empty")
    private String name;

}