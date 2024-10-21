package ru.sweetbun.DTO;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LocationDTO {

    private String slug;
    private String name;
}
