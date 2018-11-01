package edu.sunypoly.cypher.db;
/*
Author: Austin Monson(Sannity)

Date of Last Revision: 10/30/2018

Class: CS 370
    Group Members: Dylan, Jacob, Georgy

Description: The manager that manages the accounts in the cypher 
    software management suite. Passowrds and such.

    The problem manager can be used in this fashion:
        [Mis Manager].[Account Manager].---;

Specification: 
    ---
*/
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Arrays;
import java.util.regex.*;

public class AccountManager
{
    private static Connection SQLCON = null;
    private TeamManager accTeams = null;
    public AccountManager(Connection INSQLCON)
    {
        SQLCON = INSQLCON; 
    }

    public boolean validate(String username, String password) throws NullInputException, InvalidDataException, DoesNotExistException
    {
        PreparedStatement stmt = null;
        String query = "SELECT password FROM account WHERE id = ?";
        byte[] dbHash = null;
        String dbString = null;
        byte[] pwHash = null;
        String pwString = null;
        MessageDigest digest = null;
        accTeams = new TeamManager(SQLCON);

        try
        {
            digest = MessageDigest.getInstance("SHA-256");
        }
        catch(NoSuchAlgorithmException e){e.printStackTrace();}
        pwHash = digest.digest(password.getBytes(StandardCharsets.UTF_8)); 
        pwString = String.format("%032x", new BigInteger(1, pwHash));
        //Ensure there is a username and password
        if(username == null || password == null)
            throw new NullInputException("Must provide a username AND a password");
        if(username.isEmpty() || password.isEmpty())
            throw new InvalidDataException("Must provide a username AND a password, at least 1 char");

        //Check if the team exists
        if(accTeams.getId(username) != -1)
            try 
            {
                stmt = SQLCON.prepareStatement(query);
                stmt.setInt(1, accTeams.getId(username));
                ResultSet result = stmt.executeQuery();
                result.next();
                dbHash = result.getBytes("password");
                dbString = String.format("%032x", new BigInteger(1, dbHash));
            } 
            catch (SQLException e) {}
        else
            throw new DoesNotExistException("Team " + username + " already exists!");
        
        //return boolean that indicates if the stored has and the generated hash are equal
        return pwString.equals(dbString);
    }
}   