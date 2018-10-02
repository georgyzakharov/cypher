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
		
		ProgCompSubmissionHandler handler = new ProgCompSubmissionHandler(Submissions);
		
		ProgCompSubmission sub1 = new ProgCompSubmission(pnum, teamId, code, lang);
		Submissions.offer(sub1);
		
		
		
		return null;
	}
}
