package edu.sunypoly.cypher.db;

import java.sql.*;

class ProblemManager
{
    private static Connection SQLCON = null; 
    public ProblemManager(Connection INSQLCON)
    {
        SQLCON = INSQLCON;
    }

    /* Problem Manager */
    public boolean create(String problemName, byte[] problemDescription, byte[] problemTestCode) throws AlreadyExistsException
    {
        boolean success = false;
        PreparedStatement stmt = null;
        PreparedStatement nameStmt = null;
        String nameQuery = "INSERT INTO problem(name) VALUES (?);";
        String storageQuery = "INSERT INTO problem_storage VALUES (?, ?, ?)";
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
    
    public boolean delete(String problemName)
    {
        boolean success = false;
        PreparedStatement stmt = null;
        String query = "DELETE FROM problem_storage WHERE id = ?;";
        if(getId(problemName) != -1)
        {
            try 
            {
                stmt = SQLCON.prepareStatement(query);
                stmt.setInt(1, getId(problemName));
                stmt.executeUpdate();
            
                query = "DELETE FROM problem WHERE id = ?;";
                stmt = SQLCON.prepareStatement(query);
                stmt.setInt(1, getId(problemName));
                stmt.executeUpdate();
            } 
            catch (SQLException e) 
            {
                /*
                System.err.println("SQLException: " + e.getMessage());
                System.err.println("SQLState: " + e.getSQLState());
                System.err.println("VendorError: " + e.getErrorCode());
                */
            }
            if(getId(problemName) == -1)
                success = true;
        }
        return success;
    }
    
    public boolean delete(int problemId)
    {
        boolean success = false;
        PreparedStatement stmt = null;
        String query = "DELETE FROM problem_storage WHERE id = ?;";
        if(getName(problemId) != null)
        {
            try
            {
                stmt = SQLCON.prepareStatement(query);
                stmt.setInt(1, problemId);
                stmt.executeUpdate();
        
                query = "DELETE FROM problem WHERE id = ?;";
                stmt = SQLCON.prepareStatement(query);
                stmt.setInt(1, problemId);
                stmt.executeUpdate(query);
            }
            catch(SQLException e)
            {
                /*
                System.err.println("SQLException: " + e.getMessage());
                System.err.println("SQLState: " + e.getSQLState());
                System.err.println("VendorError: " + e.getErrorCode());
                */
            }
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
        try 
        {
            stmt = SQLCON.prepareStatement(query);
            stmt.setString(1, problemName);
            ResultSet result = stmt.executeQuery();
            result.next();
            problemId = result.getInt("id");
        } 
        catch (SQLException e) 
        {
            /*
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("VendorError: " + e.getErrorCode());
            */
        }
        return problemId;
    }
    
    public String getName(int problemId) 
    {
        String problemName = null;
        PreparedStatement stmt = null;
        String query = "SELECT name FROM problem WHERE id = ?;";
        try 
        {
            stmt = SQLCON.prepareStatement(query);
            stmt.setInt(1, problemId);
            ResultSet result = stmt.executeQuery();
            result.next();
            problemName = result.getString("name");
        } 
        catch (SQLException e) 
        {
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("VendorError: " + e.getErrorCode());
        }
        return problemName;
    }

    public byte[] getDescription(int problemId)
    {
        byte[] problemDescription = null;
        PreparedStatement stmt = null;
        String query = "SELECT problem_description FROM problem_storage WHERE id = ?;";
        try
        {
            stmt = SQLCON.prepareStatement(query);
            stmt.setInt(1, problemId);
            ResultSet result = stmt.executeQuery();
            result.next();
            problemDescription = result.getBytes("problem_description");
        } 
        catch (SQLException e) 
        {
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("VendorError: " + e.getErrorCode());
        }
        return problemDescription;
    }

    public byte[] getDescription(String problemName)
    {
        byte[] problemDescription = null;
        PreparedStatement stmt = null;
        String query = "SELECT problem_description FROM problem_storage WHERE id = ?;";
        try
        {
            stmt = SQLCON.prepareStatement(query);
            stmt.setInt(1, getId(problemName));
            ResultSet result = stmt.executeQuery();
            result.next();
            problemDescription = result.getBytes("problem_description");
        } 
        catch (SQLException e) 
        {
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("VendorError: " + e.getErrorCode());
        }
        return problemDescription;
    }
    
    public byte[] getTestCode(int problemId)
    {
        byte[] problemTestCode = null;
        PreparedStatement stmt = null;
        String query = "SELECT problem_test FROM problem_storage WHERE id = ?;";
        try
        {
            stmt = SQLCON.prepareStatement(query);
            stmt.setInt(1, problemId);
            ResultSet result = stmt.executeQuery();
            result.next();
            problemTestCode = result.getBytes("problem_test");
        } 
        catch (SQLException e) 
        {
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("VendorError: " + e.getErrorCode());
        }
        return problemTestCode;
    }

    public byte[] getTestCode(String problemName)
    {
        byte[] problemTestCode = null;
        PreparedStatement stmt = null;
        String query = "SELECT problem_test FROM problem_storage WHERE id = ?;";
        try
        {
            stmt = SQLCON.prepareStatement(query);
            stmt.setInt(1, getId(problemName));
            ResultSet result = stmt.executeQuery();
            result.next();
            problemTestCode = result.getBytes("problem_test");
        } 
        catch (SQLException e) 
        {
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("VendorError: " + e.getErrorCode());
        }
        return problemTestCode;
    }

}