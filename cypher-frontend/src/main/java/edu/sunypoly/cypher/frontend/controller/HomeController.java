package edu.sunypoly.cypher.frontend.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import edu.sunypoly.cypher.frontend.service.ApplicationExecutorService;

import edu.sunypoly.cypher.db.*;
@Controller
public class HomeController {

	// Spring injection
	@Resource
	private ApplicationExecutorService service;

	Mis db_manager = new Mis("jdbc:mysql://localhost/cypher_db?useSSL=false", "cypher", "cypher");

	// Home page with all the UI
	@RequestMapping(value = { "/" }, method = RequestMethod.GET)
	public ModelAndView getLogin() 
	{
		ModelAndView model = new ModelAndView("log_in");
		return model;
	}

	@RequestMapping(value = { "/mainpage" }, method = RequestMethod.GET)
	public ModelAndView getHome(int typeOfUser, String username, String password) 
	{
		boolean userExists = false;
		
		try
		{
			userExists = db_manager.Account.validate(username, password);
		}
		catch(NullInputException e)
		{
			ModelAndView model = new ModelAndView("error");
			return model;
		}
		catch(InvalidDataException e)
		{
			ModelAndView model = new ModelAndView("error");
			return model;
		}
		catch(DoesNotExistException e)
		{
			ModelAndView model = new ModelAndView("error");
			return model;
		}

		if(userExists)
		{
			if (typeOfUser == 0) 
			{
				ModelAndView model = new ModelAndView("team_landing");
				return model;
			} 
			else if (typeOfUser == 1) 
			{
				ModelAndView model = new ModelAndView("proctor_landing");
				return model;
			}
		}
		else
		{
			ModelAndView model = new ModelAndView("error");
			return model;
		}
		return null;
	}
}
