package com.jossbee.houseOwner.model;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Host {

    @NotBlank(message = "Host UUID is mandatory")
    private String hostUuid;

    @NotBlank(message = "Name is mandatory")
    private String name;

    private String hostProfilePictureUrl;
}
