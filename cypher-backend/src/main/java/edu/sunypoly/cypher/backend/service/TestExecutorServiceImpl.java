package edu.sunypoly.cypher.backend.service;

import java.util.concurrent.BlockingQueue;

import org.cypher.commons.AssignmentTest;
import org.cypher.commons.TestRequest;
import org.cypher.commons.TestResponse;
import org.springframework.stereotype.Service;

@Service
public class TestExecutorServiceImpl implements TestExecutorService {

	public static BlockingQueue<Runnable> Submissions;

	@Override
	public TestResponse execute(TestRequest request) {
		//you have access to all the variables that i send to you
		String code = request.getApplicationCode();
		String lang = request.getLanguage();
		int pnum = request.getProgramNumber();
		String teamId= request.getTeamId();
		AssignmentTest[] tests = request.getAssignmentTests();
		
		
		//you will have to return your info to me here 
		/**************/
		DockerRunDriver submission = new DockerRunDriver(pnum, teamId, code, lang);

		submission.writeResults(DockerRun.compExec(submission));

		/**/
		String result = submission.getResults();
		
		return null;
	}
}
