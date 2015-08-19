package com.etiennek;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
	public static void main(String[] args) throws Exception {
		System.setProperty("rx.ring-buffer.size", "1024"); // XXX: Ugly hack! Use RX Backpressure instead!
		SpringApplication.run(Application.class, args);
	}
}
