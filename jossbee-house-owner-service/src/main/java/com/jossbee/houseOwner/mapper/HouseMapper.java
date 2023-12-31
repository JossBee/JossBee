package com.jossbee.houseOwner.mapper;

import com.jossbee.houseOwner.dto.AddressDto;
import com.jossbee.houseOwner.dto.CategoryDto;
import com.jossbee.houseOwner.dto.HostDto;
import com.jossbee.houseOwner.dto.HouseDto;
import com.jossbee.houseOwner.exception.ServiceException;
import com.jossbee.houseOwner.model.Address;
import com.jossbee.houseOwner.model.Category;
import com.jossbee.houseOwner.model.Host;
import com.jossbee.houseOwner.model.House;
import org.springframework.stereotype.Component;

@Component
public class HouseMapper {

    public HouseDto convertModelToDto(House house) {
        HouseDto houseDto = new HouseDto();

        houseDto.setId(house.getId());
        houseDto.setIsActive(house.getIsActive());
        houseDto.setCreatedAt(house.getCreatedAt());
        houseDto.setCreatedBy(house.getCreatedBy());
        houseDto.setUpdatedAt(house.getUpdatedAt());
        houseDto.setUpdatedBy(house.getUpdatedBy());
        houseDto.setTitle(house.getTitle());
        houseDto.setDescription(house.getDescription());
        houseDto.setNumberOfBedRooms(house.getNumberOfBedRooms());
        houseDto.setNumberOfBathrooms(house.getNumberOfBathrooms());
        houseDto.setGuestsCapacity(house.getGuestsCapacity());
        houseDto.setPricePerNight(house.getPricePerNight());
        houseDto.setDiscount(house.getDiscount());

        if (house.getHost() != null) {
            houseDto.setHost(convertHostToDto(house.getHost()));
        }

        if (house.getAddress() != null) {
            houseDto.setAddress(convertAddressToDto(house.getAddress()));
        }

        if (house.getCategory() != null) {
            houseDto.setCategory(convertCategoryToDto(house.getCategory()));
        }

        houseDto.setAmenities(house.getAmenities());
        houseDto.setPhotos(house.getPhotos());

        return houseDto;
    }

    private CategoryDto convertCategoryToDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .imageUrl(category.getImageUrl())
                .build();
    }

    private HostDto convertHostToDto(Host host) {
        return HostDto.builder()
                .hostUuid(host.getHostUuid())
                .name(host.getName())
                .hostProfilePictureUrl(host.getHostProfilePictureUrl())
                .build();
    }

    private AddressDto convertAddressToDto(Address address) {
        return AddressDto.builder()
                .street(address.getStreet())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .city(address.getCity())
                .state(address.getState())
                .country(address.getCountry())
                .zipCode(address.getZipCode())
                .build();
    }

    public House convertDtoToModel(HouseDto houseDto) {

        if (houseDto == null) {
            throw new ServiceException("No DTO provided for constructing the house object!");
        }

        House.HouseBuilder houseBuilder = House.builder()
                .isActive(houseDto.getIsActive())
                .title(houseDto.getTitle())
                .description(houseDto.getDescription())
                .numberOfBedRooms(houseDto.getNumberOfBedRooms())
                .numberOfBathrooms(houseDto.getNumberOfBathrooms())
                .guestsCapacity(houseDto.getGuestsCapacity())
                .pricePerNight(houseDto.getPricePerNight())
                .discount(houseDto.getDiscount());

        if (houseDto.getHost() != null) {
            houseBuilder.host(Host.builder()
                    .hostUuid(houseDto.getHost().getHostUuid())
                    .name(houseDto.getHost().getName())
                    .hostProfilePictureUrl(houseDto.getHost().getHostProfilePictureUrl())
                    .build());
        }

        if (houseDto.getAddress() != null) {
            houseBuilder.address(Address.builder()
                    .street(houseDto.getAddress().getStreet())
                    .latitude(houseDto.getAddress().getLatitude())
                    .longitude(houseDto.getAddress().getLongitude())
                    .city(houseDto.getAddress().getCity())
                    .state(houseDto.getAddress().getState())
                    .country(houseDto.getAddress().getCountry())
                    .zipCode(houseDto.getAddress().getZipCode())
                    .build());
        }

        if (houseDto.getCategory() != null) {
            houseBuilder.category(Category.builder()
                    .id(houseDto.getCategory().getId())
                    .name(houseDto.getCategory().getName())
                    .imageUrl(houseDto.getCategory().getImageUrl())
                    .build());
        }

        if (houseDto.getAmenities() != null) {
            houseBuilder.amenities(houseDto.getAmenities());
        }

        if (houseDto.getPhotos() != null) {
            houseBuilder.photos(houseDto.getPhotos());
        }

        return houseBuilder.build();
    }
}
