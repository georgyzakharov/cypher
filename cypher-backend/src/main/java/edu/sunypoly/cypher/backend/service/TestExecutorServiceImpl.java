package edu.sunypoly.cypher.backend.service;

import java.io.IOException;

import org.cypher.commons.AssignmentTest;
import org.cypher.commons.AssignmentTestResult;
import org.cypher.commons.TestRequest;
import org.cypher.commons.TestResponse;
import org.springframework.stereotype.Service;

import ch.qos.logback.core.net.SyslogOutputStream;

@Service
public class TestExecutorServiceImpl implements TestExecutorService {

	public TestExecutorServiceImpl() 
	{
		subHandler = new ProgCompSubmissionHandler();
	}

	@Override
	public synchronized TestResponse execute(TestRequest request) {
		// you have access to all the variables that i send to you

		int pnum = request.getProgramNumber();
		String teamId = request.getTeamId();
		String code = request.getApplicationCode();
		String lang = request.getLanguage();
		//AssignmentTest[] tests = request.getAssignmentTests();

//GEORGY CODE
		
		// you will have to return your info to me here
		/**************/
		//DockerRunDriver submission = new DockerRunDriver(1, "team1", code, lang);

		//submission.writeResults(DockerRun.compExec(submission));
		/**************/
		
//JAKE CODE
		
		/**************/
		ProgCompSubmission submission = new ProgCompSubmission(pnum, teamId, code, lang);
		submission.run();
/*		try
		{
			GradingModuleHandler.gradeSubmission(submission);
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
*/		/**************/
		
//DYLAN CODE
		/**************/
		//ProgCompSubmission submission = subHandler.addSubmission(new ProgCompSubmission(pnum, teamId, code, lang));
		/**************/
		
		TestResponse testresponse = new TestResponse();
		AssignmentTestResult testResult = new AssignmentTestResult();
		//testResult.setTestOutput(result);
		testResult.setTestOutput(submission.result);
		
		System.err.println(testResult.getTestOutput());
		
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
	private ProgCompSubmissionHandler subHandler;
}
