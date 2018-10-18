package edu.sunypoly.cypher.db;

import java.sql.Connection;
import java.sql.DriverManager;

public class MIS
{
    private static final String URL = "jdbc:mysql://localhost/cypher_db?useSSL=false";
    private static final String USERNAME = "cypher";
    private static final String PASSWORD = "cypher";
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
