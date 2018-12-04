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

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;;
import java.util.regex.Pattern;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;

/**
 * The submanager that controls the users of the cypher management system
 * @author Austin Monson (Sannity)
 * @since 11/13/2018
 */
public class UserManager
{
    private static Connection SQLCON = null; 
    private final int MAX_NAME_TYPE_LENGTH = 50;
    private final int NOT_EXISTS = -1;
    /**
     * sets the passed sql connection to the local sql connection
     * @param INSQLCON parameter passed into the manager from the Mis class 
     */
    public UserManager(Connection INSQLCON)
    {
        SQLCON = INSQLCON;
    }
    /**
     * Creates a user and inserts the specified values into the database tables. Also hashes and inserts the password into the account table in the databse
     * @see AccountManager
     * @param name The name for the user. Max 50 chars
     * @param type The type of user (proctor, team, administrator)
     * @param password The password for the user
     * @return Boolean value representing successful insertion into the database
     * @throws AlreadyExistsException There is already a user with the name
     * @throws NullInputException One of the inputs are NULL, and cannot be
     * @throws InvalidDataException One of the inputs are not correctly formatted
     */
    public boolean create(String name, String type, String password) throws AlreadyExistsException, NullInputException, InvalidDataException
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
    /**
     * This function allows the updating of a user, you must specify all values of user to update the user
     * @see getId
     * @param id The id of the team you are updating
     * @param name The name for the user. Max 50 chars
     * @param type The type of user (proctor, team, administrator)
     * @return Boolean value representing sucessful updating of tuple
     * @throws DoesNotExistException There is no user with that id
     * @throws AlreadyExistsException There is already a user with the specified name
     * @throws InvalidDataException Incorrectly formatted data was passed to the function
     * @throws NullInputException One of the inputs to the function are NULL and shouldn't be
      */
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
    /**
     * Deletes a user based on it's id
     * <p>
     * *Note: Anything that references this user will also be deleted as a result*
     * </p>
     * @see getId
     * @param id Id of the user to delete
     * @return Boolean value representing successful deletion
     */
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
    /**
     * Deletes a user based on it's name
     * <p>
     * *Note: Anything that references this user will also be deleted as a result*
     * </p>
     * @see getId
     * @param name Name of the user to delete
     * @return Boolean value representing successful deletion
     */
    public boolean delete(String name)
    {   
        //Truncate the name if it's too long for database
        if(name.length() > MAX_NAME_TYPE_LENGTH)
            name = name.substring(0, MAX_NAME_TYPE_LENGTH);
        
        return delete(getId(name));
    }

    /**
     * getter of the id for the user with the specified name
     * @param name the name of the user that you want the id of
     * @return id of the user
     */
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
    /**
     * getter of the name for the user with the specified id
     * @param id the id of the user you want the name of
     * @return name of the user
     */
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
    /**
     * getter of the type of user
     * @param id id of the user to get the type of
     * @return type of the user
     */
    public String getType(int id) 
    {
        String type = null;
        PreparedStatement stmt = null;
        String query = "SELECT type FROM user WHERE id = ?;";
        if(id != NOT_EXISTS)
            try 
            {
                stmt = SQLCON.prepareStatement(query);
                stmt.setInt(1, id);
                ResultSet result = stmt.executeQuery();
                result.next();
                type = result.getString("type");
            } 
            catch (SQLException e) {}
        return type;
    }
    /**
     * getter of the type of user
     * @param name name of the user to get the type of
     * @return type of the user
     */
    public String getType(String name) 
    {
        String type = null;

        //Truncate the name if it's too long for database
        if(name.length() > MAX_NAME_TYPE_LENGTH)
            name = name.substring(0, MAX_NAME_TYPE_LENGTH);
        
        type = getType(getId(name));
        
        return type;
    }

    public List<Integer> getUserList()
    {
        ResultSetMetaData metadata = null;
        PreparedStatement stmt = null;
        int column_count = 0;
        List<Integer> userList = new ArrayList<Integer>();
        String query = "SELECT id FROM user";
        try 
        {
            stmt = SQLCON.prepareStatement(query);
            ResultSet result= stmt.executeQuery();
            while(result.next())
            {
                userList.add(Integer.valueOf(result.getInt("id")));
            }
        } 
        catch (SQLException e) {}
        return userList;
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