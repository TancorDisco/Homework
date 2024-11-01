package ru.sweetbun.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class LocationDTO {

    private String slug;
    private String name;
}
