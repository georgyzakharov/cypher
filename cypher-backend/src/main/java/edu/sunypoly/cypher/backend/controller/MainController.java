package edu.sunypoly.cypher.backend.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(method=RequestMethod.GET)
public class MainController {
	
	//everything 
	@RequestMapping("/execute/{assignementId}")
	public void execute(@PathVariable Long assignementId) {
		// place your code here
		System.out.println("Place yout code here");
		System.out.println(assignementId);
	}

} 