package ru.sweetbun.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class City {
    @JsonProperty("slug")
    private String slug;
    @JsonProperty("coords")
    private Coords coords;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Coords {
        @JsonProperty("lat")
        private double lat;
        @JsonProperty("lon")
        private double lon;
    }

    public String toXML() {
        XmlMapper xmlMapper = new XmlMapper();
        try {
            log.debug("Начинается преобразование объекта в XML: {}", this);
            String xml = xmlMapper.writeValueAsString(this);
            log.info("Преобразование в XML успешно: {}", xml);
            return xml;
        } catch (JsonProcessingException e) {
            log.error("Ошибка преобразования в XML: {}", e.getMessage());
            return null;
        }
    }
}
