package com.example.liber_cinema;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.example.liber_cinema.repositories")
public class LiberCinemaApplication {

	public static void main(String[] args) {
		SpringApplication.run(LiberCinemaApplication.class, args);
	}

}
