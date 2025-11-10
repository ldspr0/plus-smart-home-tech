package ru.yandex.practicum.interactionapi.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressDto {
    private String country;
    private String city;
    private String street;
    private String house;
    private String flat;
}