package ru.sweetbun.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import ru.sweetbun.DTO.PlaceDTO;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@Builder
public class Event implements Identifiable{

    private Long id;
    private String title;
    private String price;
    @JsonProperty("favorites_count")
    private int favoritesCount;
    private PlaceDTO place;

    public Double getPriceAsDouble() {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(price);
        if (matcher.find()) {
            return Double.parseDouble(matcher.group());
        }
        return 0.0;
    }
}
