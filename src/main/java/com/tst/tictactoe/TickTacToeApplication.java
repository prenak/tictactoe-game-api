package com.tst.tictactoe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.tst.tictactoe.repository")
public class TickTacToeApplication {

	public static void main(String[] args) {
		SpringApplication.run(TickTacToeApplication.class, args);
	}

}
