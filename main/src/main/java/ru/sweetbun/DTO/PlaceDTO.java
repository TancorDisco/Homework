package ru.sweetbun.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlaceDTO {

    private String title;
    private String slug;
    private String address;
    private LocationDTO location;
}
