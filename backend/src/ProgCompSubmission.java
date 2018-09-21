import java.io.*;

public class ProgCompSubmission implements Serializable, Runnable
{	
	private static final long serialVersionUID = 2990291944369043085L;
	
	public ProgCompSubmission(int ProblemNumber, String TeamID, String code, String language) 
	{
		this.ProblemNumber = ProblemNumber;
		this.TeamID = TeamID;
		this.code = code;
		this.language = language;
	}
	
	public void run()
	{
				
	}
	
	public int ProblemNumber;
	public String TeamID;
	public String code;
	public String language;
	public int score;

	
	
}
