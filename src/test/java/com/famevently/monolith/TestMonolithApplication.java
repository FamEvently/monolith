package com.famevently.monolith;

import org.springframework.boot.SpringApplication;

public class TestMonolithApplication {

	public static void main(String[] args) {
		SpringApplication.from(Application::main).with(TestcontainersConfiguration.class).run(args);
	}

}
