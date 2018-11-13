package edu.sunypoly.cypher.db;


/*
Author: Austin Monson(Sannity)

Date of Last Revision: 10/25/2018

Class: CS 370
    Group Members: Dylan, Jacob, Georgy

Description: This is the submanager that manages the users on the cypher
    software management system. 

    This submanager can be used in this fashion:
        [Mis Manager].[UserManager].---;

Specification:
    Public:
        boolean create(String name, String password)
        boolean update(int userId, String name)
        boolean delete(int userId/String name)
        int getId(String name)
        String getName(int userId)
    Private:
        boolean createAccount(int userId, String password)
        
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

public class UserManager
{
    private static Connection SQLCON = null; 
    private final int MAX_NAME_TYPE_LENGTH = 50;
    private final int NOT_EXISTS = -1;

    public UserManager(Connection INSQLCON)
    {
        SQLCON = INSQLCON;
    }

    public boolean create(String name, String type, String password) throws AlreadyExistsException, NullInputException, InvalidDataException, DoesNotExistException
    {
        boolean success = false;
        PreparedStatement stmt = null;
        String query = "INSERT INTO user(name, type) VALUES (?, ?);";
        
        //name/password check. Can't be null, can't be empty
        if(name == null || name.isEmpty())
            throw new InvalidDataException("Must provide a name!");
        if(type == null || type.isEmpty())
            throw new InvalidDataException("Must provide a type!");
        if(password == null) //password empty check done in AccountManager
            throw new NullInputException("Must provide a password!");
        
        //TRUNCATE TYPE
        if(type.length() > MAX_NAME_TYPE_LENGTH)
            type = type.substring(0, MAX_NAME_TYPE_LENGTH);
        //Truncate the name if it's too long for database
        if(name.length() > MAX_NAME_TYPE_LENGTH)
            name = name.substring(0, MAX_NAME_TYPE_LENGTH); 
        
        //if the user doesnt exist
        if(getId(name) == NOT_EXISTS)
        {
            try 
            {
                stmt = SQLCON.prepareStatement(query);
                stmt.setString(1, name);
                stmt.setString(2, type);
                stmt.executeUpdate();
                createAccount(getId(name), password);
            } 
            catch (SQLException e) 
            {
                System.err.println("SQLException: " + e.getMessage());
                System.err.println("SQLState: " + e.getSQLState());
                System.err.println("VendorError: " + e.getErrorCode());
            }
            if(getId(name) != NOT_EXISTS)
                success = true;
        }
        else
            throw new AlreadyExistsException(name + " already exists!");
        return success;
    }
    public boolean update(int id, String name, String type) throws DoesNotExistException, AlreadyExistsException, InvalidDataException, NullInputException 
    {
        boolean success = false;
        PreparedStatement stmt = null;
        PreparedStatement fkStatement = null;
        String query = "UPDATE user SET name = ?, type = ? WHERE id = ?";
        String fk = "SET FOREIGN_KEY_CHECKS = ?;";
  
        //check if user name is null
        if(name == null || name.isEmpty())
            throw new NullInputException("Must provide a name!");
        if(type == null || type.isEmpty())
            throw new InvalidDataException("Must provide a type!");
        //check name length, truncate to 50 chars
        if(name.length() > MAX_NAME_TYPE_LENGTH)
            name = name.substring(0, MAX_NAME_TYPE_LENGTH); 

        if(getName(id) != null && (getId(name) == NOT_EXISTS)) //old name does exist, and newUserName doesnt
        {
            try 
            {
                fkStatement = SQLCON.prepareStatement(fk);
                fkStatement.setInt(1, 0);
                fkStatement.executeUpdate();

                stmt = SQLCON.prepareStatement(query);
                stmt.setString(1, name);
                stmt.setString(2, type);
                stmt.setInt(3, id);
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
            if(getId(name) != NOT_EXISTS)
                success = true;    
        }
        else if(getId(name) != NOT_EXISTS) //newUserName already exists
            throw new AlreadyExistsException(name + " already exists!");
        else //The user youre trying to replace doesnt exist.
            throw new DoesNotExistException(id + " does not exist!");
        
        return success;
    }

    public boolean delete(int id) 
    {
        boolean success = false;
        PreparedStatement stmt = null;
        String query = "DELETE FROM user WHERE id = ?;";
        if(getName(id) != null)
        {
            try
            {
                stmt = SQLCON.prepareStatement(query);
                stmt.setInt(1, id);
                stmt.executeUpdate();
            } 
            catch (SQLException e) 
            {
                System.err.println("SQLException: " + e.getMessage());
                System.err.println("SQLState: " + e.getSQLState());
                System.err.println("VendorError: " + e.getErrorCode());
            }
            if(getName(id) == null)
                success = true;
        }       
        return success;
    }
    public boolean delete(String name)
    {   
        //Truncate the name if it's too long for database
        if(name.length() > MAX_NAME_TYPE_LENGTH)
            name = name.substring(0, MAX_NAME_TYPE_LENGTH);
        
        return delete(getId(name));
    }


    public int getId(String name) 
    {
        int userId = NOT_EXISTS;
        PreparedStatement stmt = null;
        String query = "SELECT id FROM user WHERE name = ?;";

        //Truncate the name if it's too long for database
        if(name.length() > MAX_NAME_TYPE_LENGTH)
            name = name.substring(0, MAX_NAME_TYPE_LENGTH);
        
        if(name != null)
            try 
            {
                stmt = SQLCON.prepareStatement(query);
                stmt.setString(1, name);
                ResultSet result = stmt.executeQuery();
                result.next();
                userId = result.getInt("id");
            } 
            catch (SQLException e) {}
        return userId;
    }

    public String getName(int id) 
    {
        String name = null;
        PreparedStatement stmt = null;
        String query = "SELECT name FROM user WHERE id = ?;";
        if(id != NOT_EXISTS)
            try 
            {
                stmt = SQLCON.prepareStatement(query);
                stmt.setInt(1, id);
                ResultSet result = stmt.executeQuery();
                result.next();
                name = result.getString("name");
            } 
            catch (SQLException e) {}
        return name;
    }

    private boolean createAccount(int id, String password) throws InvalidDataException
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
            stmt.setInt(1, id);
            stmt.setString(2, String.format("%032x", new BigInteger(hash)));
            stmt.executeUpdate();
        }
        catch(SQLException e){
            e.printStackTrace();
        }

        //Convert to a hex string to store
        StringBuffer hexString = new StringBuffer();
        for (byte b : hash) {
            hexString.append(Integer.toHexString(Byte.toUnsignedInt(b) & 0xff));
        }
        return valid;
    }
}