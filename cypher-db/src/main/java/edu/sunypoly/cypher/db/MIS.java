import java.sql.*;

public final class MIS
{
    private final String URL;
    private final String USERNAME;
    private final String PASSWORD;
    
    private TeamManager tempTeamManager = null;
    private ProblemManager tempProblemManager = null;   
    private SolutionManager tempSolutionManager = null;

    public final TeamManager Team;
    public final ProblemManager Problem;
    public final SolutionManager Solution;
    
    private static Connection SQLCON = null;

    public MIS(String SQLURL, String SQLUsername, String SQLPassword)
    {
        USERNAME = SQLUsername;
        PASSWORD = SQLPassword;
        URL = SQLURL;

        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            SQLCON = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        
        //Initialize Team Manager
        tempTeamManager = new TeamManager(SQLCON);
        Team = tempTeamManager;
    
        //Initialize Problem Manager
        tempProblemManager = new ProblemManager(SQLCON);
        Problem = tempProblemManager;    
    
        //Initialize Solution Manager
        tempSolutionManager = new SolutionManager(SQLCON);
        Solution = tempSolutionManager;
    
    }
}
