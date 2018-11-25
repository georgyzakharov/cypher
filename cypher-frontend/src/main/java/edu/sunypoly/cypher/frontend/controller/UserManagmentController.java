package edu.sunypoly.cypher.frontend.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import edu.sunypoly.cypher.frontend.service.UserManagmentService;




@Controller
public class UserManagmentController {
	
	@Resource private UserManagmentService userManagmentService;
	
	@RequestMapping(value = { "/usermanagment" }, method = RequestMethod.GET)
	public ModelAndView getUsers() {

		
		ModelAndView model = new ModelAndView("users");
		model.addObject("userList", userManagmentService.getUserList());
		return model;
	}
	
	@RequestMapping(value = {"/usermanagment/create"}, method = RequestMethod.POST)
	public String createUser(Long userid, String name, String username, String passwrd,String type_role ) {
		System.out.println(userid + " " + name + " " + username + " " + passwrd + " " + type_role);
		
		
		userManagmentService.createUser(userid, name, username, passwrd, type_role);
		
		
		return "redirect:";
	}

}
