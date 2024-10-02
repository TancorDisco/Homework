package ru.sweetbun.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Location implements Identifiable {
    private Long id;
    private String slug;
    private String name;
}
