package edu.sunypoly.cypher.backend.controller;

import java.util.concurrent.BlockingQueue;

import javax.annotation.Resource;

import org.cypher.commons.TestRequest;
import org.cypher.commons.TestResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import edu.sunypoly.cypher.backend.service.*;
import edu.sunypoly.cypher.backend.service.TestExecutorService;

//import edu.sunyit.executor.phython.service.TestExecutorService;

@RestController
@RequestMapping(value= {"/tests"},method=RequestMethod.POST)
public class MainController
{
	public MainController() 
	{
		handler = new ProgCompSubmissionHandler();
	}
	
	@Resource private TestExecutorService service;

	@RequestMapping("/execute")
	public TestResponse execute(@RequestBody TestRequest request)
	{
		
		
		
		
		
		
		
		return service.execute(request);
	}
	 private ProgCompSubmissionHandler handler;

} 