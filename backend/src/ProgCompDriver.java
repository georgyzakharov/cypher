import java.util.concurrent.BlockingQueue;
public class ProgCompDriver
{
	public static void main(String[] args)
	{
		ProgCompSubmissionHandler handler = new ProgCompSubmissionHandler(Submissions);
		
		ProgCompSubmission sub1 = new ProgCompSubmission(1, "Yeet", "Yeeet", "Yeeeet");
		Submissions.offer(sub1);
		
		
	}
	
	public static BlockingQueue<Runnable> Submissions;

}
