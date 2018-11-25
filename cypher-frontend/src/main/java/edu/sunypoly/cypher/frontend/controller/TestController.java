package edu.sunypoly.cypher.frontend.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import edu.sunypoly.cypher.frontend.service.TestStudentService;

@Controller
public class TestController {

	@Resource private TestStudentService studentService;
	
	
	@RequestMapping("/test")
	public ModelAndView getStudentTest() {
		
		ModelAndView testModelAndView = new ModelAndView("students");
		testModelAndView.addObject("studentList", studentService.getStudentList());
		
		return testModelAndView;
			
	}
}
