package edu.sunypoly.cypher.backend.service;
import java.util.concurrent.BlockingQueue;
import java.lang.Thread;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ProgCompSubmissionHandler implements Runnable
{
	public ProgCompSubmissionHandler() 
	{
		subs = new LinkedBlockingQueue<Runnable>();
		int Cores = Runtime.getRuntime().availableProcessors();
		this.thread = new Thread(this);
		this.stopthread = false;
		executor = new ThreadPoolExecutor(Cores, Cores*2, 10, TimeUnit.SECONDS, this.subs);	
		this.thread.start();
	}
	
	public void close() 
	{
		stopthread = true;
	}
	
	public ProgCompSubmission addSubmission(ProgCompSubmission sub)
	{
		this.subs.offer(sub);
		while(sub.threadWait) 
		{
			
		}
		sub.threadWait = true;
		
		return sub;
	}
	
	public void run() 
	{
		while(!stopthread)
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
	
	private boolean stopthread;
	private ThreadPoolExecutor executor;
	private Thread thread;
	private BlockingQueue<Runnable> subs;	
}
