import java.sql.*;

public class MIS
{
    private static final String URL = "jdbc:mysql://localhost/cypher_db?useSSL=false";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "DamienBro";
    public ProblemManager Problem = null;
    public SolutionManager Solution = null;
    public TeamManager Team = null;
    private static Connection SQLCON = null;

    public MIS()
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            SQLCON = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        
            if(SQLCON == null)
                System.out.println("I AM NULL I AM NULL");
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        
        Problem = new ProblemManager(SQLCON);
        Solution = new SolutionManager(SQLCON);
        Team = new TeamManager(SQLCON);
    }
}
