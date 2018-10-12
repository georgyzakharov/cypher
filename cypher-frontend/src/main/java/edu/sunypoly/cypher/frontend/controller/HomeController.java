package edu.sunypoly.cypher.frontend.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import edu.sunypoly.cypher.frontend.service.ApplicationExecutorService;

@Controller
public class HomeController {

	// Spring injection
	@Resource
	private ApplicationExecutorService service;
	
	// Home page with all the UI
	@RequestMapping(value = { "/" }, method = RequestMethod.GET)
	public ModelAndView getHome() {

		ModelAndView model = new ModelAndView("home");

		return model;
	}

	
	@RequestMapping(value = { "/submission" }, method = RequestMethod.POST)
	public ModelAndView getAnswer(String language, String applicationCode, String test) {

		ModelAndView model = new ModelAndView("answer");

		model.addObject("language", language);
		model.addObject("applicationCode", applicationCode);
		model.addObject("test", test);
		model.addObject("result", service.getResult(applicationCode, test));

		return model;
	}

	/*
	@RequestMapping(value = { "/proctor" }, method = RequestMethod.GET)
	public ModelAndView getProblem() {

		ModelAndView model = new ModelAndView("problem");

		return model;
	}
	
	
	@RequestMapping(value = {"/post"}, method = RequestMethod.POST)
	public ModelAndView 
	
	 */

}
