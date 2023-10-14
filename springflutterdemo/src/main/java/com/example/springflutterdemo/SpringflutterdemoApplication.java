package com.example.springflutterdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class SpringflutterdemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringflutterdemoApplication.class, args);
	}

	@RestController
	class GreetingController {
		@GetMapping("/greeting")
		public Greeting greet() {
			return new Greeting("Hello from Spring Boot! Hows it going");
		}
	}

	class Greeting {
		private String message;

		public Greeting(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}
	}

}
