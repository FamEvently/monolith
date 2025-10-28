package com.famevently.monolith;

import org.springframework.boot.SpringApplication;

public class TestMonolithApplication {

	public static void main(String[] args) {
		SpringApplication.from(MonolithApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
