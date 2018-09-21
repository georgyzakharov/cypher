package edu.sunyit.progcompetition.ui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@ImportResource("classpath*:/*-context.xml")
@SpringBootApplication
public class App {
	//this Spring application will start up any Controllers that exist in my project 
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

   
}
