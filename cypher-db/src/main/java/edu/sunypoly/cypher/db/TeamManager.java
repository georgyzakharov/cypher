/*
Author: Austin Monson(Sannity)

Date of Last Revision: 10/25/2018

Class: CS 370
    Group Members: Dylan, Jacob, Georgy

Description: This is the submanager that manages the teams on the cypher
    software management system. 

    This submanager can be used in this fashion:
        [Mis Manager].[TeamManager].---;

Specification:
    Public:
        boolean create(String teamName, String password)
        boolean update(int teamId, String teamName)
        boolean delete(int teamId/String teamName)
        int getId(String teamName)
        String getName(int teamId)
    Private:
        boolean createAccount(int teamId, String password)
        
*/

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class TeamManager
{
    private static Connection SQLCON = null; 
    private final int MAX_TEAM_NAME_LENGTH = 50;
    private final int NOT_EXISTS = -1;

    public TeamManager(Connection INSQLCON)
    {
        SQLCON = INSQLCON;
    }

    public boolean create(String teamName, String password) throws AlreadyExistsException, NullInputException, InvalidDataException, DoesNotExistException
    {
        boolean success = false;
        PreparedStatement stmt = null;
        String query = "INSERT INTO team(name) VALUES (?);";
        
        //teamName/password check. Can't be null, can't be empty
        if(teamName == null || teamName.isEmpty())
            throw new InvalidDataException("Must provide a team name!");
        if(password == null) //password empty check done in AccountManager
            throw new NullInputException("Must provide a password!");
        
        //Truncate the teamName if it's too long for database
        if(teamName.length() > MAX_TEAM_NAME_LENGTH)
            teamName = teamName.substring(0, MAX_TEAM_NAME_LENGTH); 
        
        //if the team doesnt exist
        if(getId(teamName) == NOT_EXISTS)
        {
            try 
            {
                createAccount(getId(teamName), password);
                stmt = SQLCON.prepareStatement(query);
                stmt.setString(1, teamName);
                stmt.executeUpdate();
            } 
            catch (SQLException e) 
            {
                System.err.println("SQLException: " + e.getMessage());
                System.err.println("SQLState: " + e.getSQLState());
                System.err.println("VendorError: " + e.getErrorCode());
            }
            if(getId(teamName) != NOT_EXISTS)
                success = true;
        }
        else
            throw new AlreadyExistsException(teamName + " already exists!");
        return success;
    }
    public boolean update(int teamId, String teamName) throws DoesNotExistException, AlreadyExistsException, InvalidDataException, NullInputException 
    {
        boolean success = false;
        PreparedStatement stmt = null;
        PreparedStatement fkStatement = null;
        String query = "UPDATE team SET name = ? WHERE id = ?";
        String fk = "SET FOREIGN_KEY_CHECKS=?;";
  
        //check if team name is null
        if(teamName== null || teamName.isEmpty())
            throw new NullInputException("Must provide a team name!");
        
        //check teamName length, truncate to 50 chars
        if(teamName.length() > MAX_TEAM_NAME_LENGTH)
            teamName = teamName.substring(0, MAX_TEAM_NAME_LENGTH); 

        if(getName(teamId) != null && (getId(teamName) == NOT_EXISTS)) //old teamName does exist, and newTeamName doesnt
        {
            try 
            {
                fkStatement = SQLCON.prepareStatement(fk);
                fkStatement.setInt(1, 0);
                fkStatement.executeUpdate();

                stmt = SQLCON.prepareStatement(query);
                stmt.setString(1, teamName);
                stmt.setInt(2, teamId);
                stmt.executeUpdate();

                fkStatement = SQLCON.prepareStatement(fk);
                fkStatement.setInt(1, 1);
                fkStatement.executeUpdate();
            } 
            catch (SQLException e) 
            {
                System.err.println("SQLException: " + e.getMessage());
                System.err.println("SQLState: " + e.getSQLState());
                System.err.println("VendorError: " + e.getErrorCode());
            }
            if(getId(teamName) != NOT_EXISTS)
                success = true;    
        }
        else if(getId(teamName) != NOT_EXISTS) //newTeamName already exists
            throw new AlreadyExistsException(teamName + " already exists!");
        else //The team youre trying to replace doesnt exist.
            throw new DoesNotExistException(teamId + " does not exist!");
        
        return success;
    }

    public boolean delete(int teamId) 
    {
        boolean success = false;
        PreparedStatement stmt = null;
        String query = "DELETE FROM team WHERE id = ?;";
        if(getName(teamId) != null)
        {
            try
            {
                stmt = SQLCON.prepareStatement(query);
                stmt.setInt(1, teamId);
                stmt.executeUpdate();
            } 
            catch (SQLException e) 
            {
                System.err.println("SQLException: " + e.getMessage());
                System.err.println("SQLState: " + e.getSQLState());
                System.err.println("VendorError: " + e.getErrorCode());
            }
            if(getName(teamId) == null)
                success = true;
        }       
        return success;
    }
    public boolean delete(String teamName)
    {   
        //Truncate the teamName if it's too long for database
        if(teamName.length() > MAX_TEAM_NAME_LENGTH)
            teamName = teamName.substring(0, MAX_TEAM_NAME_LENGTH);
        
        return delete(getId(teamName));
    }


    public int getId(String teamName) 
    {
        int teamId = NOT_EXISTS;
        PreparedStatement stmt = null;
        String query = "SELECT id FROM team WHERE name = ?;";

        //Truncate the teamName if it's too long for database
        if(teamName.length() > MAX_TEAM_NAME_LENGTH)
            teamName = teamName.substring(0, MAX_TEAM_NAME_LENGTH);
        
        if(teamName != null)
            try 
            {
                stmt = SQLCON.prepareStatement(query);
                stmt.setString(1, teamName);
                ResultSet result = stmt.executeQuery();
                result.next();
                teamId = result.getInt("id");
            } 
            catch (SQLException e) {}
        return teamId;
    }

    public String getName(int teamId) 
    {
        String teamName = null;
        PreparedStatement stmt = null;
        String query = "SELECT name FROM team WHERE id = ?;";
        if(teamId != NOT_EXISTS)
            try 
            {
                stmt = SQLCON.prepareStatement(query);
                stmt.setInt(1, teamId);
                ResultSet result = stmt.executeQuery();
                result.next();
                teamName = result.getString("name");
            } 
            catch (SQLException e) {}
        return teamName;
    }

    private boolean createAccount(int teamId, String password) throws InvalidDataException
    {
        boolean valid = false;
        Pattern pattern;
        Matcher matcher;
        String passwordPattern = "((?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,50})";
        MessageDigest digest = null;
        byte[] hash = null;
        PreparedStatement stmt = null;
        String query = "INSERT INTO account VALUES (?,?)";
        pattern = Pattern.compile(passwordPattern);
        matcher = pattern.matcher(password);
        
        if(!matcher.matches())
            throw new InvalidDataException("Invalid Password, Password must contain a lowercase and uppercase letter, a number, and be between 6 and 50 characters");
        else
            valid = true;
        try
        {
            digest = MessageDigest.getInstance("SHA-256");
            hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        }
        catch(NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        try
        {
            stmt = SQLCON.prepareStatement(query);
            stmt.setInt(1, teamId);
            stmt.setString(2, String.format("%032x", new BigInteger(hash)));
            stmt.executeUpdate();
        }
        catch(SQLException e){}

        //Convert to a hex string to store
        StringBuffer hexString = new StringBuffer();
        for (byte b : hash) {
            hexString.append(Integer.toHexString(Byte.toUnsignedInt(b) & 0xff));
        }
        return valid;
    }
}