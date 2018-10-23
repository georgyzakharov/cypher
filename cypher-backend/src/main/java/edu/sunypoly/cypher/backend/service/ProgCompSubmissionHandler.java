package edu.sunypoly.cypher.backend.service;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ProgCompSubmissionHandler implements Runnable
{
	public ProgCompSubmissionHandler() 
	{
		Completed = new LinkedList<ProgCompSubmission>();
		subs = new LinkedBlockingQueue<Runnable>();
		int Cores = Runtime.getRuntime().availableProcessors();
		
		executor = new ThreadPoolExecutor(Cores, Cores*2, 10, TimeUnit.SECONDS, this.subs);	
		
	}
	
	public ProgCompSubmission addSubmission(ProgCompSubmission sub)
	{
		this.subs.offer(sub);

		return sub;
	}
	
	public void run() 
	{
		while(true)
		{
			if(!subs.isEmpty()) 
			{
				try
				{
					ProgCompSubmission Temp = (ProgCompSubmission) subs.take();
					executor.execute(Temp);
					Completed.add(Temp);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	
	private ThreadPoolExecutor executor;
	private static BlockingQueue<Runnable> subs;
	private static Queue<ProgCompSubmission> Completed;
	
}
