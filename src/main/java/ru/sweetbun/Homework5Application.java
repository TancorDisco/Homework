package ru.sweetbun;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import ru.sweetbun.entity.Category;
import ru.sweetbun.entity.Location;
import ru.sweetbun.service.KudaGoService;
import ru.sweetbun.storage.Storage;

@SpringBootApplication
public class Homework5Application {

	public static void main(String[] args) {
		SpringApplication.run(Homework5Application.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public Storage<Category> categoryStorage() {
		return new Storage<>();
	}

	@Bean
	public Storage<Location> locationStorage() {
		return new Storage<>();
	}

	@Bean
	public KudaGoService<Category> categoryService() {
		return new KudaGoService<>(restTemplate(), categoryStorage());
	}

	@Bean
	public KudaGoService<Location> locationService() {
		return new KudaGoService<>(restTemplate(), locationStorage());
	}
}
