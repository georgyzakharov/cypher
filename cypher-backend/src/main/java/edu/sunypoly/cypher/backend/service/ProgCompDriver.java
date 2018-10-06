package edu.sunypoly.cypher.backend.service;
import java.util.concurrent.BlockingQueue;
public class ProgCompDriver
{

	public static BlockingQueue<Runnable> submissions;

	public static void main(String[] args)
	{
		
		ProgCompSubmissionHandler handler = new ProgCompSubmissionHandler(submissions);
		
		ProgCompSubmission sub1 = new ProgCompSubmission(1, "Yeet", "Yeeet", "Yeeeet");
		submissions.offer(sub1);
		
		
	}
	
	
	
	
	

}
