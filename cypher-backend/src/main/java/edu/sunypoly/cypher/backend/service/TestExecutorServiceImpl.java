package edu.sunypoly.cypher.backend.service;

import org.cypher.commons.AssignmentTest;
import org.cypher.commons.AssignmentTestResult;
import org.cypher.commons.TestRequest;
import org.cypher.commons.TestResponse;
import org.springframework.stereotype.Service;

@Service
public class TestExecutorServiceImpl implements TestExecutorService {


	@Override
	public TestResponse execute(TestRequest request) {
		// you have access to all the variables that i send to you

		int pnum = request.getProgramNumber();
		String teamId = request.getTeamId();
		String code = request.getApplicationCode();
		String lang = request.getLanguage();
		AssignmentTest[] tests = request.getAssignmentTests();

		// you will have to return your info to me here
		/**************/
		//DockerRunDriver submission = new DockerRunDriver(1, "team1", code, lang);

		//submission.writeResults(DockerRun.compExec(submission));

		/**************/
		//String result = submission.getResults();
		
		// Revised to reflect the updated Docker management classes
		/**************/
		ProgCompSubmission submission = new ProgCompSubmission(pnum, teamId, code, lang);
		
		submission.run();
		/**************/
		
		TestResponse testresponse = new TestResponse();
		AssignmentTestResult testResult = new AssignmentTestResult();
		//testResult.setTestOutput(result);
		testResult.setTestOutput(submission.result);
		
		
		AssignmentTestResult [] assignmetTestResultArray = new AssignmentTestResult[1];
		assignmetTestResultArray[0] = testResult;
		
		testresponse.setTestResults(assignmetTestResultArray);
		
		//testresponse.setTestResult(new AssignmentTestResult[] {testResult});
		
		/*
		 * Here you will have to implement the conversion of what ever jake return into
		 * the (TestResponse)
		 * 
		 */
		
		return testresponse;
	}
}
