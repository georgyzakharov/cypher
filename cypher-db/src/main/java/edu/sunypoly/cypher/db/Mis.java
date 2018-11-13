package edu.sunypoly.cypher.db;

/*
Author: Austin Monson(Sannity)

Date of Last Revision: 10/25/2018

Class: CS 370
    Group Members: Dylan, Jacob, Georgy

Description: The top manager to the group of database manager classes.
    This class initializes the many different sub-managers that complete
    the set of database managers.

    The submanagers can be used in this fashion:
        [Mis Manager].[Sub-Manager].---;

Specification:
    Mis(String sqlUrl, String sqlUsername, String sqlPassword)
        - creates the SQL connection and sends that to the sub managers
*/

import java.sql.Connection;
import java.sql.DriverManager;

public final class Mis
{
    //JDBC SQL Connection Object
    private Connection SQL_CONNECTION = null;
    
    //Variables for the various submodules
    public final UserManager User;
    public final ProblemManager Problem;
    public final SolutionManager Solution;
    public final AccountManager Account;

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
