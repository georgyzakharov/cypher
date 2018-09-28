package edu.sunypoly.cypher.backend.service;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ProgCompSubmissionHandler implements Runnable
{
	public ProgCompSubmissionHandler(BlockingQueue<Runnable> subs) 
	{
		int Cores = Runtime.getRuntime().availableProcessors();
		this.subs = subs;
		executor = new ThreadPoolExecutor(Cores, Cores*2, 10, TimeUnit.SECONDS, this.subs);
		
		
		
	}
	
	public void run() 
	{
		while(true)
		{
			if(!subs.isEmpty()) 
			{
				try
				{
					executor.execute(subs.take());
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
	
	private ThreadPoolExecutor executor;
	private BlockingQueue<Runnable> subs;
	
}
