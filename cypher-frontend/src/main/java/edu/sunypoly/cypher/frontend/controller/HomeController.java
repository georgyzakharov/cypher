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
	public ModelAndView getLogin() {

		ModelAndView model = new ModelAndView("log_in");

		return model;
	}

	@RequestMapping(value = { "/mainpage" }, method = RequestMethod.GET)
	public ModelAndView getHome(int typeOfUser) {
		if (typeOfUser == 0) {
			ModelAndView model = new ModelAndView("team_landing");
			return model;
		} else if (typeOfUser == 1) {
			ModelAndView model = new ModelAndView("proctor_landing");
			return model;
		}
		return null;
	}

}
