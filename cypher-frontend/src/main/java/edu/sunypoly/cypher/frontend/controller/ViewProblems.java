package edu.sunypoly.cypher.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ViewProblems {
	
	@RequestMapping(value = { "/create_problem" }, method = RequestMethod.GET)
	public ModelAndView getProblems() {

		
		ModelAndView model = new ModelAndView("create_problem");
		
		return model;
	}
	
	
	

}
