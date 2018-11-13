package edu.sunypoly.cypher.db;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * The top manager that controls all submanagers in the cypher database api
 * @author Austin Monson (Sannity)
 * @since 11/13/2018
 */

public final class Mis
{
    //JDBC SQL Connection Object
    private Connection SQL_CONNECTION = null;
    
    //Variables for the various submodules
    /**User Sub-Manager*/
    public final UserManager User;
    /**Problem Sub-Manager*/
    public final ProblemManager Problem;
    /**Solution Sub-Manager*/
    public final SolutionManager Solution;
    /**Account Sub-Manager*/
    public final AccountManager Account;
    /**
     * The constructor class will initiate the sub-managers for the Mis class
     * 
     * @param sqlUrl The SQL URL for the specified system
     * @param sqlUsername The specific username for the SQL URL
     * @param sqlPassword The password for the specified username
     * @return Initialized sub-managers, User, Problem, Solution, and Account
     */
    public Mis(String sqlUrl, String sqlUsername, String sqlPassword)
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            SQL_CONNECTION = DriverManager.getConnection(sqlUrl, sqlUsername, sqlPassword);
        }
        catch (Exception e) 
        {
            System.err.println("CRITICAL ERROR IN SQL DATABASE CONNECTION!");
            e.printStackTrace();
        }
        //Initialize User Manager
        User = new UserManager(SQL_CONNECTION);
        //Initialize Problem Manager
        Problem = new ProblemManager(SQL_CONNECTION);    
        //Initialize Solution Manager
        Solution = new SolutionManager(SQL_CONNECTION);
        //Initialize Account Manager
        Account = new AccountManager(SQL_CONNECTION);
    }
}
