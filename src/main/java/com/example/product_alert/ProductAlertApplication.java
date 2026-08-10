package com.example.product_alert;

import com.example.product_alert.interfaces.scheduler.ScrapingScheduler;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ProductAlertApplication {
	public static void main(String[] args) {
		SpringApplication.run(ProductAlertApplication.class, args);
	}

	@Bean
    CommandLineRunner testar(ScrapingScheduler scheduler) {
		return args -> scheduler.executar();
	}
}
