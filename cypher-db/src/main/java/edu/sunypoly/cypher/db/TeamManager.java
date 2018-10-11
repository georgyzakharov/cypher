package edu.sunypoly.cypher.db;

import java.sql.*;

class TeamManager
{
    private static Connection SQLCON = null; 
    public TeamManager(Connection INSQLCON)
    {
        SQLCON = INSQLCON;
        if(SQLCON == null)
            System.err.println("THIS IS IT");
    }

    public boolean create(String teamName) throws AlreadyExistsException
    {
        boolean success = false;
        PreparedStatement stmt = null;
        String query = "INSERT INTO team(name) VALUES (?);";
        //if the team doesnt exist
        if(getId(teamName) == -1)
        {
            try 
            {
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

            if(getId(teamName) != -1)
                success = true;
        }

        //it does exist
        else
            throw new AlreadyExistsException(teamName + " already exists!");
        return success;
    }
    
    public boolean delete(String teamName)
    {
        boolean success = false;
        PreparedStatement stmt = null;
        String query = "DELETE FROM team WHERE name = ?;";
        if(getId(teamName) != -1)
        {
            try
            {
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
            if(getId(teamName) == -1)
                success = true;
        }
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

    public int getId(String teamName) {
        int teamId = -1;
        PreparedStatement stmt = null;
        String query = "SELECT id FROM team WHERE name = ?;";
        try 
        {
            stmt = SQLCON.prepareStatement(query);
            stmt.setString(1, teamName);
            ResultSet result = stmt.executeQuery();
            result.next();
            teamId = result.getInt("id");
        } 
        catch (SQLException e) 
        {
            /*
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("VendorError: " + e.getErrorCode());
            */
        }
        return teamId;
    }

    public String getName(int teamId) 
    {
        String teamName = null;
        PreparedStatement stmt = null;
        String query = "SELECT name FROM team WHERE id = ?;";
        try 
        {
            stmt = SQLCON.prepareStatement(query);
            stmt.setInt(1, teamId);
            ResultSet result = stmt.executeQuery();
            result.next();
            teamName = result.getString("name");
        } 
        catch (SQLException e) 
        {
            /*
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("VendorError: " + e.getErrorCode());
            */
        }
        return teamName;
    }

}