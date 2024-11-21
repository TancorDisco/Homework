package ru.sweetbun.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class EventDTO {

    private String title;
    private String price;
    private LocalDate date;
    private PlaceDTO place;
}
