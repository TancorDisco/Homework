package ru.sweetbun;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import ru.sweetbun.entity.Category;
import ru.sweetbun.entity.Location;
import ru.sweetbun.service.KudaGoService;
import ru.sweetbun.storage.Storage;

import java.util.concurrent.TimeUnit;

@EnableCaching
@SpringBootApplication
public class Homework5Application {

	public static void main(String[] args) {
		SpringApplication.run(Homework5Application.class, args);
	}

	@Bean
	public CacheManager cacheManager() {
		CaffeineCacheManager cacheManager = new CaffeineCacheManager("currencyRates");
		cacheManager.setCaffeine(Caffeine.newBuilder()
				.expireAfterWrite(1, TimeUnit.HOURS));
		return cacheManager;
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration()
				.setSkipNullEnabled(true)
				.setMatchingStrategy(MatchingStrategies.STRICT);
		return modelMapper;
	}
}
