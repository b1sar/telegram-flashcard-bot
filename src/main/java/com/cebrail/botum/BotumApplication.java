package com.cebrail.botum;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
public class BotumApplication {

	public static void main(String[] args) {
		ApiContextInitializer.init();
		SpringApplication.run(BotumApplication.class, args);


	}
}
