package edu.sunypoly.cypher.backend.service;

import org.cypher.commons.AssignmentTest;
import org.cypher.commons.TestRequest;
import org.cypher.commons.TestResponse;
import org.springframework.stereotype.Service;

@Service
public class TestExecutorServiceImpl implements TestExecutorService {


	@Override
	public TestResponse execute(TestRequest request) {
		//you have access to all the variables that i send to you
		String code = request.getApplicationCode();
		String lang = request.getLanguage();
		AssignmentTest[] tests = request.getAssignmentTests();
		
		
		//you will have to return your info to me here 
		return null;
	}
}
