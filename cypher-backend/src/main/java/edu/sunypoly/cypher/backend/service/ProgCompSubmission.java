package edu.sunypoly.cypher.backend.service;
import java.io.Serializable;

public class ProgCompSubmission implements Serializable, Runnable
{	
	private static final long serialVersionUID = 2990291944369043085L;
	
	public ProgCompSubmission(int ProblemNumber, String TeamID, String code, String language) 
	{
		this.ProblemNumber = ProblemNumber;
		this.TeamID = TeamID;
		this.code = code;
		this.language = language;
		this.compilationStatus = true;
	}
	
	public void run()
	{
		this.result = DockerRun.compExec(this); 
	}
	
	public int ProblemNumber;
	public String TeamID;
	public String code;
	public String language;
	public int score;
	public String result;
	public boolean compilationStatus;

	
	
}
