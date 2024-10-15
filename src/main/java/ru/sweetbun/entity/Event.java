package ru.sweetbun.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Event implements Identifiable{

    private Long id;
    private String title;
    private String price;
}
