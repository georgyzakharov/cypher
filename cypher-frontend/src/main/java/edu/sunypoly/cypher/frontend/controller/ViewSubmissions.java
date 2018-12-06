package edu.sunypoly.cypher.frontend.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import edu.sunypoly.cypher.frontend.service.ApplicationExecutorService;

@Controller
public class ViewSubmissions {
	private String[] language_array = { "Python", "Java", "C++", "C" };

	// Spring injection
	@Resource
	private ApplicationExecutorService service;

	@RequestMapping(value = { "/submission" }, method = RequestMethod.POST)
	public ModelAndView getAnswer(int language, String applicationCode, String test) {

		ModelAndView model = new ModelAndView("answer");

		model.addObject("language", language);
		model.addObject("applicationCode", applicationCode);
		model.addObject("test", test);
		model.addObject("result", service.getResult(language_array[language], applicationCode, test));

		return model;
	}

}
