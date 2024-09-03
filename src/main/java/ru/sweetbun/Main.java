package ru.sweetbun;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ru.sweetbun.models.City;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Slf4j
public class Main {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        convertFromJsonToXML("city.json", "city.xml");
        convertFromJsonToXML("city-error.json", "city-error.xml");
    }

    public static void convertFromJsonToXML(String jsonFile, String xmlFile) {
        log.debug("Начинается конвертирование файла: {}", jsonFile);
        try {
            City city = mapper.readValue(new File(jsonFile), City.class);
            log.info("Объект успешно получен из файла {}", jsonFile);
            String cityXML = city.toXML();
            if (cityXML != null) {
                Files.write(Paths.get(xmlFile), cityXML.getBytes(), StandardOpenOption.CREATE);
                log.info("XML успешно сохранён в файл {}", xmlFile);
            } else {
                log.warn("Преобразование в XML вернуло null для объекта: {}", city);
            }
        } catch (JsonMappingException e) {
            log.error("Ошибка маппинга JSON в файле {}: {}", jsonFile, e.getMessage());
        } catch (IOException e) {
            log.error("Ошибка чтения или записи файла {}: {}", jsonFile, e.getMessage());
        }
    }
}