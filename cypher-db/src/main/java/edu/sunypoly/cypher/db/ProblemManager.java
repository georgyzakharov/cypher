package edu.sunypoly.cypher.db;

/*
Author: Austin Monson(Sannity)


Date of Last Revision: 10/30/2018

Class: CS 370
    Group Members: Dylan, Jacob, Georgy

Description: The manager that manages the problems in the cypher 
    software management suite.

    The problem manager can be used in this fashion:
        [Mis Manager].[Problem-Manager].---;

Specification: 
    ---
*/

import java.sql.*;

public class ProblemManager
{
    private static Connection SQLCON = null; 
    private static final int MAX_PROBLEM_NAME_LENGTH = 50;
    private static final int LONGBLOB_MAX_SIZE = 2000000000; //2GB

    public ProblemManager(Connection INSQLCON)
    {
        SQLCON = INSQLCON;
    }

    /* Problem Manager */
    public boolean create(String problemName, byte[] problemDescription, byte[] problemTestCode) throws AlreadyExistsException, NullInputException, InvalidDataException
    {
        boolean success = false;
        PreparedStatement stmt = null;
        PreparedStatement nameStmt = null;
        String nameQuery = "INSERT INTO problem(name) VALUES (?);";
        String storageQuery = "INSERT INTO problem_storage VALUES (?, ?, ?)";
        
        //no problemName provided
        if(problemName == null)
            throw new NullInputException("Problem must have a name!");
        //empty problemname string provided
            else if(problemName.isEmpty())
            throw new InvalidDataException("Problem name cannot be an empty string!");
        //if problemname is too long, truncate
        if(problemName.length() > MAX_PROBLEM_NAME_LENGTH)
            problemName = problemName.substring(0, MAX_PROBLEM_NAME_LENGTH);
        
    
        if(problemDescription == null || problemDescription.length == 0)
            throw new InvalidDataException("Problem must have a description!");
        else if(problemDescription.length > LONGBLOB_MAX_SIZE)
            throw new InvalidDataException("Description file too large, Max file size is 2GB");
        if(problemTestCode == null || problemTestCode.length == 0)
            throw new InvalidDataException("Problem must have Test Code Uploaded alongside it!");
        else if(problemTestCode.length > LONGBLOB_MAX_SIZE)
            throw new InvalidDataException("Test Code file too large, Max file size is 2GB");
        
        if(getId(problemName) == -1)
        {
            try 
            {
                nameStmt = SQLCON.prepareStatement(nameQuery);
                nameStmt.setString(1, problemName);
                nameStmt.executeUpdate();

                stmt = SQLCON.prepareStatement(storageQuery);
                stmt.setInt(1, getId(problemName));
                stmt.setBytes(2, problemDescription);
                stmt.setBytes(3, problemTestCode);
                stmt.executeUpdate();

            } 
            catch (SQLException e) 
            {
                System.err.println("SQLException: " + e.getMessage());
                System.err.println("SQLState: " + e.getSQLState());
                System.err.println("VendorError: " + e.getErrorCode());
            }
            if(getId(problemName) != -1)
                success = true;
        }
        else
            throw new AlreadyExistsException(problemName + "already exists!");
 
        return success;
    }

    public boolean update(int problemId, String problemName, byte[] problemDescription, byte[] problemTestCode) throws DoesNotExistException, AlreadyExistsException, NullInputException, InvalidDataException
    {
        boolean success = false;
        PreparedStatement stmt = null;
        PreparedStatement fkStatement = null;
        String nameQuery = "UPDATE problem SET name = ? WHERE id = ?";
        String storageQuery = "UPDATE problem_storage SET problem_description = ?, problem_test = ? WHERE id = ?";
        String fk = "SET FOREIGN_KEY_CHECKS=?;";

        if(problemName == null)
            throw new NullInputException("Problem must have a name!");
        //empty problemname string provided
            else if(problemName.isEmpty())
            throw new InvalidDataException("Problem name cannot be an empty string!");
        //if problemname is too long, truncate
        if(problemName.length() > MAX_PROBLEM_NAME_LENGTH)
            problemName = problemName.substring(0, MAX_PROBLEM_NAME_LENGTH);
        
        if(problemDescription == null || problemDescription.length == 0)
            throw new InvalidDataException("Problem must have a description!");
        else if(problemDescription.length > LONGBLOB_MAX_SIZE)
            throw new InvalidDataException("Description file too large, Max file size is 2GB");
        if(problemTestCode == null || problemTestCode.length == 0)
            throw new InvalidDataException("Problem must have Test Code Uploaded alongside it!");
        if(problemTestCode.length > LONGBLOB_MAX_SIZE)
            throw new InvalidDataException("Test Code file too large, Max file size is 2GB");

        if(getName(problemId) != null && getId(problemName) == -1)
        {
            try
            {
                fkStatement = SQLCON.prepareStatement(fk);
                fkStatement.setInt(1, 0);
                fkStatement.executeUpdate();

                stmt = SQLCON.prepareStatement(nameQuery);
                stmt.setString(1,problemName);
                stmt.setInt(2,problemId);
                stmt.executeUpdate();

                stmt = SQLCON.prepareStatement(storageQuery);
                stmt.setBytes(1, problemDescription);
                stmt.setBytes(2, problemTestCode);
                stmt.setInt(3, problemId);
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
            if(getId(problemName) != -1)
                success = true;
        }
        else if (getName(problemId) == null)
            throw new DoesNotExistException(problemId + " does not exist!");
        else 
            throw new AlreadyExistsException(problemName + " is a used problemName");
        return success;
    }
    public boolean delete(String problemName)
    {
        boolean success = false;
        PreparedStatement stmt = null;
        String query = null;
        //if problemname is too long, truncate
        if(problemName.length() > MAX_PROBLEM_NAME_LENGTH)
            problemName = problemName.substring(0, MAX_PROBLEM_NAME_LENGTH);
        if(getId(problemName) != -1)
        {
            try 
            {
                query = "DELETE FROM problem WHERE id = ?;";
                stmt = SQLCON.prepareStatement(query);
                stmt.setInt(1, getId(problemName));
                stmt.executeUpdate();
            } 
            catch (SQLException e) {}
            if(getId(problemName) == -1)
                success = true;
        }
        return success;
    }
    
    public boolean delete(int problemId)
    {
        boolean success = false;
        PreparedStatement stmt = null;
        String query = null;
        if(getName(problemId) != null)
        {
            try
            {
                //DEPRECIATED, DATABASE NOW UPDATES THIS INTERNALLY
                /*
                query = "DELETE FROM problem_storage WHERE id = ?;";
                stmt = SQLCON.prepareStatement(query);
                stmt.setInt(1, problemId);
                stmt.executeUpdate();
                */

                query = "DELETE FROM problem WHERE id = ?;";
                stmt = SQLCON.prepareStatement(query);
                stmt.setInt(1, problemId);
                stmt.executeUpdate(query);
            }
            catch(SQLException e) {}
            if(getName(problemId) == null)
                success = true;
        }
        return success;
    }
    
    public int getId(String problemName) 
    {
        int problemId = -1;
        PreparedStatement stmt = null;
        String query = "SELECT id FROM problem WHERE name = ?;";
        //if problemname is too long, truncate
        if(problemName.length() > MAX_PROBLEM_NAME_LENGTH)
            problemName = problemName.substring(0, MAX_PROBLEM_NAME_LENGTH);
        if(problemName != null)
            try 
            {
                stmt = SQLCON.prepareStatement(query);
                stmt.setString(1, problemName);
                ResultSet result = stmt.executeQuery();
                result.next();
                problemId = result.getInt("id");
            } 
            catch (SQLException e) {}
        return problemId;
    }
    
    public String getName(int problemId) 
    {
        String problemName = null;
        PreparedStatement stmt = null;
        String query = "SELECT name FROM problem WHERE id = ?;";
        if(problemId != -1)
            try 
            {
                stmt = SQLCON.prepareStatement(query);
                stmt.setInt(1, problemId);
                ResultSet result = stmt.executeQuery();
                result.next();
                problemName = result.getString("name");
            } 
            catch (SQLException e) {}
        return problemName;
    }

    public byte[] getDescription(int problemId)
    {
        byte[] problemDescription = null;
        PreparedStatement stmt = null;
        String query = "SELECT problem_description FROM problem_storage WHERE id = ?;";
        if(getName(problemId) != null)
            try
            {
                stmt = SQLCON.prepareStatement(query);
                stmt.setInt(1, problemId);
                ResultSet result = stmt.executeQuery();
                result.next();
                problemDescription = result.getBytes("problem_description");
            } 
            catch (SQLException e) {}
        return problemDescription;
    }

    public byte[] getDescription(String problemName)
    {
        byte[] problemDescription = null;
        PreparedStatement stmt = null;
        String query = "SELECT problem_description FROM problem_storage WHERE id = ?;";
        //if problemname is too long, truncate
        if(problemName.length() > MAX_PROBLEM_NAME_LENGTH)
            problemName = problemName.substring(0, MAX_PROBLEM_NAME_LENGTH);
        if(getId(problemName) != -1)
            try
            {
                stmt = SQLCON.prepareStatement(query);
                stmt.setInt(1, getId(problemName));
                ResultSet result = stmt.executeQuery();
                result.next();
                problemDescription = result.getBytes("problem_description");
            } 
            catch (SQLException e) {}
        return problemDescription;
    }
    
    public byte[] getTestCode(int problemId)
    {
        byte[] problemTestCode = null;
        PreparedStatement stmt = null;
        String query = "SELECT problem_test FROM problem_storage WHERE id = ?;";
        if(getName(problemId) != null)
            try
            {
                stmt = SQLCON.prepareStatement(query);
                stmt.setInt(1, problemId);
                ResultSet result = stmt.executeQuery();
                result.next();
                problemTestCode = result.getBytes("problem_test");
            } 
            catch (SQLException e) {}
        return problemTestCode;
    }

    public byte[] getTestCode(String problemName)
    {
        byte[] problemTestCode = null;
        PreparedStatement stmt = null;
        String query = "SELECT problem_test FROM problem_storage WHERE id = ?;";
        //if problemname is too long, truncate
        if(problemName.length() > MAX_PROBLEM_NAME_LENGTH)
            problemName = problemName.substring(0, MAX_PROBLEM_NAME_LENGTH);
        if(getId(problemName) != -1)
            try
            {
                stmt = SQLCON.prepareStatement(query);
                stmt.setInt(1, getId(problemName));
                ResultSet result = stmt.executeQuery();
                result.next();
                problemTestCode = result.getBytes("problem_test");
            } 
            catch (SQLException e) {}
        return problemTestCode;
    }

}