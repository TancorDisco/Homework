package ru.sweetbun.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlaceDTO {

    private String title;
    private String address;
    private String location;
}
