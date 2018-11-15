package edu.sunypoly.cypher.db;

import java.sql.*;

/**
 * The manager for the problems in the cypher database
 * @author Austin Monson (Sannity)
 * @since 11/13/2018
 */
public class SolutionManager
{
    private static Connection SQLCON = null; 
    private static final int MAX_SOLUTION_NAME_LENGTH = 50;
    private static final int LONGBLOB_MAX_SIZE = 2000000000; //2GB
    private UserManager solutionUserManager = null;
    private ProblemManager solutionProblemManager = null;

    /**
     * sets the passed sql connection to the local sql connection
     * @param INSQLCON parameter passed into the manager from the Mis class 
     */
    public SolutionManager(Connection INSQLCON)
    {
        SQLCON = INSQLCON;
        solutionUserManager = new UserManager(INSQLCON);
        solutionProblemManager = new ProblemManager(INSQLCON);
    }
    /**
     * creates a solution in the database
     * @param solutionName the name of the problem
     * @param userId the id of the user that made the problem
     * @param problemId the id of the problem the solution is for
     * @param language the language the solution is written in
     * @param solution the solution itself
     * @return Boolean success value based on successful insertion
     * @throws AlreadyExistsException a solution with that name already exists
     * @throws NullInputException an input is null when it should not have been
     * @throws InvalidDataException invalid data was given to the function
     * @throws DoesNotExistException the user or problem does not exist
     */
    public boolean create(String solutionName, int userId, int problemId, String language, byte[] solution) throws AlreadyExistsException, NullInputException, InvalidDataException, DoesNotExistException
    {
        boolean success = false;
        PreparedStatement nameStmt = null;
        PreparedStatement storageStmt = null;
        String nameQuery = "INSERT INTO solution(name, user_id, problem_id, language) VALUES (?,?,?,?);";
        String storageQuery = "INSERT INTO solution_storage(id, solution) VALUES (?,?);";
        
        //noprovided
        if(solutionName == null)
            throw new NullInputException("Solution must have a name!");
        //empty string provided
        else if(solutionName.isEmpty())
            throw new InvalidDataException("Solution name cannot be an empty string!");
        // too long, truncate
        else if(solutionName.length() > MAX_SOLUTION_NAME_LENGTH)
            solutionName = solutionName.substring(0, MAX_SOLUTION_NAME_LENGTH);

        if(userId < 0)
            throw new InvalidDataException("Invalid UserID, out of range");
        else if(solutionUserManager.getName(userId) == null)
            throw new DoesNotExistException("Invalid UserID, does not exist");
        if(problemId < 0)
            throw new InvalidDataException("Invalid problemID, out of range");
        else if(solutionProblemManager.getName(problemId) == null)
            throw new DoesNotExistException("Invalid problemID, does not exist");
        if(language == null)
            throw new NullInputException("language cannot be null");
        else if(language.isEmpty())
            throw new InvalidDataException("language cannot be left empty");
        if(solution == null || solution.length == 0)
            throw new NullInputException("Solution Cannot be null!");
        else if(solution.length > LONGBLOB_MAX_SIZE)
            throw new InvalidDataException("Solution file too large, Max file size is 2GB");
        
        
        
        
        
        if(getId(solutionName) == -1)
        {
            try
            {
                nameStmt = SQLCON.prepareStatement(nameQuery);
                nameStmt.setString(1, solutionName);
                nameStmt.setInt(2, userId);
                nameStmt.setInt(3, problemId);
                nameStmt.setString(4, language);
                nameStmt.execute();

                storageStmt = SQLCON.prepareStatement(storageQuery);
                storageStmt.setInt(1, getId(solutionName));
                storageStmt.setBytes(2, solution);
                storageStmt.execute();
                

            } catch (SQLException e) 
            {
                System.err.println("SQLException: " + e.getMessage());
                System.err.println("SQLState: " + e.getSQLState());
                System.err.println("VendorError: " + e.getErrorCode());
            }
            if(getId(solutionName) != -1)
                success = true;
        }
        else
            throw new AlreadyExistsException(solutionName + "already exists!");
        return success;
    }

    /**
     * updates a solution in the database
     * @param solutionId the id of the solution to change
     * @param solutionName the name of the problem
     * @param userId the id of the user that made the problem
     * @param problemId the id of the problem the solution is for
     * @param language the language the solution is written in
     * @param score the score that the solution recieved
     * @param solution the solution itself
     * @return Boolean success value based on successful insertion
     * @throws AlreadyExistsException a solution with that name already exists
     * @throws NullInputException an input is null when it should not have been
     * @throws InvalidDataException invalid data was given to the function
     * @throws DoesNotExistException the user, problem, or solution does not exist
     */
    public boolean update(int solutionId, String solutionName, int userId, int problemId, String language, int score, byte[] solution) throws DoesNotExistException, AlreadyExistsException, NullInputException, InvalidDataException
    {
        boolean success = false;
        PreparedStatement fkStmt = null;
        PreparedStatement stmt = null;
        String fk = "SET FOREIGN_KEY_CHECKS=?;";
        String nameQuery = "UPDATE solution SET name = ?, user_id = ?, user_id = ?, language = ?, score = ? WHERE id = ?";
        String storageQuery = "UPDATE solution_storage SET solution = ? WHERE id = ?";
        
        if(solutionId < 0)
            throw new InvalidDataException("Invalid SolutionId, out of range");
        //noprovided
        if(solutionName == null)
            throw new NullInputException("Solution must have a name!");
        //empty string provided
        else if(solutionName.isEmpty())
            throw new InvalidDataException("Solution name cannot be an empty string!");
        // too long, truncate
        else if(solutionName.length() > MAX_SOLUTION_NAME_LENGTH)
            solutionName = solutionName.substring(0, MAX_SOLUTION_NAME_LENGTH);

        if(userId < 0)
            throw new InvalidDataException("Invalid userID, out of range");
        else if(solutionUserManager.getName(userId) == null)
            throw new DoesNotExistException("Invalid userID, does not exist");
        if(problemId < 0)
            throw new InvalidDataException("Invalid problemID, out of range");
        else if(solutionProblemManager.getName(problemId) == null)
            throw new DoesNotExistException("Invalid problemID, does not exist");
        if(language == null)
            throw new NullInputException("language cannot be null");
        else if(language.isEmpty())
            throw new InvalidDataException("language cannot be left empty");
        if(solution == null)
            throw new NullInputException("Solution Cannot be null!");
        else if(solution.length > LONGBLOB_MAX_SIZE)
            throw new InvalidDataException("Solution file too large, Max file size is 2GB");
        
        
        if(getName(solutionId) != null && getId(solutionName) == -1)
        {
            try
            {
                fkStmt = SQLCON.prepareStatement(fk);
                fkStmt.setInt(1, 0);
                fkStmt.executeUpdate();

                stmt = SQLCON.prepareStatement(nameQuery);
                stmt.setString(1, solutionName);
                stmt.setInt(2, userId);
                stmt.setInt(3, problemId);
                stmt.setString(4, language);
                stmt.setInt(5, score);
                stmt.setInt(6, solutionId);
                stmt.executeUpdate();

                stmt = SQLCON.prepareStatement(storageQuery);
                stmt.setBytes(1, solution);
                stmt.setInt(2, solutionId);
                stmt.executeUpdate();

                fkStmt = SQLCON.prepareStatement(fk);
                fkStmt.setInt(1, 1);
                fkStmt.executeUpdate();
            }
            catch (SQLException e) 
            {
                System.err.println("SQLException: " + e.getMessage());
                System.err.println("SQLState: " + e.getSQLState());
                System.err.println("VendorError: " + e.getErrorCode());
            }
            if(getId(solutionName) != -1)
                success = true;
        }
        else if(getName(solutionId) == null)
            throw new DoesNotExistException(solutionId + " does not exist!");
        else
            throw new AlreadyExistsException(solutionId + " is already a user!");   

        return success;

    }
    /**
     * deltes a solution
     * @param solutionId id of the solution to delete
     * @return Boolean value based on successful deletion
     */
    public boolean delete(int solutionId)
    {
        boolean success = false;
        PreparedStatement stmt = null;
        String nameQuery = "DELETE FROM solution WHERE id = ?;";
        //String storageQuery = "DELETE FROM solution_storage WHERE id = ?;";
        if(getName(solutionId) != null)
        {
            try
            {
                /*
                stmt = SQLCON.prepareStatement(storageQuery);
                stmt.setInt(1, solutionId);
                stmt.executeUpdate();
                */

                stmt = SQLCON.prepareStatement(nameQuery);
                stmt.setInt(1, solutionId);
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
            if(getName(solutionId) == null)
                success = true;

        }
        return success;
    }
    
    /**
     * deltes a solution
     * @param solutionName name of the solution to delete
     * @return Boolean value based on successful deletion
     */
    public boolean delete(String solutionName)
    {
        boolean success = false;
        PreparedStatement stmt = null;
        String nameQuery = "DELETE FROM solution WHERE id = ?;";
        //String storageQuery = "DELETE FROM solution_storage WHERE id = ?;";

        // too long, truncate
        if(solutionName.length() > MAX_SOLUTION_NAME_LENGTH)
            solutionName = solutionName.substring(0, MAX_SOLUTION_NAME_LENGTH);
        if(getId(solutionName) != -1)
        {
            try
            {
                /*
                stmt = SQLCON.prepareStatement(storageQuery);
                stmt.setInt(1, getId(solutionName));
                stmt.executeUpdate();
                */

                stmt = SQLCON.prepareStatement(nameQuery);
                stmt.setInt(1, getId(solutionName));
                stmt.executeUpdate();
            }
            catch (SQLException e)
            {
                
                System.err.println("SQLException: " + e.getMessage());
                System.err.println("SQLState: " + e.getSQLState());
                System.err.println("VendorError: " + e.getErrorCode());
                
            }
            
            if(getId(solutionName) == -1)
                success = true;
    
        }
        return success;
    }
    /**
     * getter of the solution
     * @param solutionName the name of the solution to get
     * @return the solution in bytes
     */
    public byte[] getSolution(String solutionName)
    {
        byte[] solution = null;
        PreparedStatement stmt = null;
        String query = "SELECT solution FROM solution_storage WHERE name = ?;";
        
        // too long, truncate
        if(solutionName.length() > MAX_SOLUTION_NAME_LENGTH)
            solutionName = solutionName.substring(0, MAX_SOLUTION_NAME_LENGTH);
        if(getId(solutionName) != -1)
            try
            {
                stmt = SQLCON.prepareStatement(query);
                stmt.setString(1, solutionName);
                ResultSet result = stmt.executeQuery();
                result.next();
                solution = result.getBytes("solution");
            }
            catch (SQLException e) 
            {
                System.err.println("SQLException: " + e.getMessage());
                System.err.println("SQLState: " + e.getSQLState());
                System.err.println("VendorError: " + e.getErrorCode());
            }
        return solution;
    
    }
    /**
     * getter of the solution
     * @param solutionId the id of the solution to get
     * @return the solution in bytes
     */
    public byte[] getSolution(int solutionId)
    {
        byte[] solution = null;
        PreparedStatement stmt = null;
        String query = "SELECT solution FROM solution_storage WHERE id = ?;";
        if(getName(solutionId) != null)
            try
            {
                stmt = SQLCON.prepareStatement(query);
                stmt.setInt(1, solutionId);
                ResultSet result = stmt.executeQuery();
                result.next();
                solution = result.getBytes("solution");
            }
            catch (SQLException e) 
            {
                System.err.println("SQLException: " + e.getMessage());
                System.err.println("SQLState: " + e.getSQLState());
                System.err.println("VendorError: " + e.getErrorCode());
            }
        return solution;
    }
    /**
     * getter of the score
     * @param solutionName the name of the solution
     * @return the score of the solution
     */
    public int getScore(String solutionName)
    {
        int score = -1;
        PreparedStatement stmt = null;
        String query = "SELECT score FROM solution WHERE name = ?;";

        // too long, truncate
        if(solutionName.length() > MAX_SOLUTION_NAME_LENGTH)
            solutionName = solutionName.substring(0, MAX_SOLUTION_NAME_LENGTH);
        if(getId(solutionName) != -1)
            try
            {
                stmt = SQLCON.prepareStatement(query);
                stmt.setString(1, solutionName);
                ResultSet result = stmt.executeQuery();
                result.next();
                score = result.getInt("score");
            }
            catch (SQLException e) 
            {
                System.err.println("SQLException: " + e.getMessage());
                System.err.println("SQLState: " + e.getSQLState());
                System.err.println("VendorError: " + e.getErrorCode());
            }
        return score;
    }
    /**
     * getter of the score
     * @param solutionId the id of the solution
     * @return the score of the solution
     */
    public int getScore(int solutionId)
    {
        int score = -1;
        PreparedStatement stmt = null;
        String query = "SELECT score FROM solution WHERE id = ?;";
        if(getName(solutionId) != null)
            try
            {
                stmt = SQLCON.prepareStatement(query);
                stmt.setInt(1, solutionId);
                ResultSet result = stmt.executeQuery();
                result.next();
                score = result.getInt("score");
            }
            catch (SQLException e) 
            {
                System.err.println("SQLException: " + e.getMessage());
                System.err.println("SQLState: " + e.getSQLState());
                System.err.println("VendorError: " + e.getErrorCode());
            }
        return score;
    }
    /**
     * getter of the language of the solution
     * @param solutionName the solution name
     * @return the language the solution is written in
     */
    public String getLanguage(String solutionName)
    {
        String language = null;
        PreparedStatement stmt = null;
        String query = "SELECT language FROM solution WHERE name = ?;";

        // too long, truncate
        if(solutionName.length() > MAX_SOLUTION_NAME_LENGTH)
            solutionName = solutionName.substring(0, MAX_SOLUTION_NAME_LENGTH);
        if(getId(solutionName) != -1)
            try
            {
                stmt = SQLCON.prepareStatement(query);
                stmt.setString(1, solutionName);
                ResultSet result = stmt.executeQuery();
                result.next();
                language = result.getString("language");
            }
            catch (SQLException e) 
            {
                System.err.println("SQLException: " + e.getMessage());
                System.err.println("SQLState: " + e.getSQLState());
                System.err.println("VendorError: " + e.getErrorCode());
            }
        return language;
    }
    /**
     * getter of the language of the solution
     * @param solutionId the solution id
     * @return the language the solution is written in
     */
    public String getLanguage(int solutionId)
    {
        String language = null;
        PreparedStatement stmt = null;
        String query ="SELECT language FROM solution WHERE id = ?;";
        if(getName(solutionId) != null)
            try
            {
                stmt = SQLCON.prepareStatement(query);
                stmt.setInt(1, solutionId);
                ResultSet result = stmt.executeQuery();
                result.next();
                language = result.getString("language");
            }
            catch (SQLException e) 
            {
                System.err.println("SQLException: " + e.getMessage());
                System.err.println("SQLState: " + e.getSQLState());
                System.err.println("VendorError: " + e.getErrorCode());
            }
        return language;
    }
    /**
     * getter of the problemId the solution is associated to
     * @param solutionId the solution id
     * @return the problem id
     */
    public int getProblemId(int solutionId)
    {
        int problemId = -1;
        PreparedStatement stmt = null;
        String query = "SELECT problem_id FROM solution WHERE id = ?;";
        if(getName(solutionId) != null)    
            try
            {
                stmt = SQLCON.prepareStatement(query);
                stmt.setInt(1, solutionId);
                ResultSet result = stmt.executeQuery();
                result.next();
                problemId = result.getInt("problem_id");
            }
            catch (SQLException e) 
            {
                System.err.println("SQLException: " + e.getMessage());
                System.err.println("SQLState: " + e.getSQLState());
                System.err.println("VendorError: " + e.getErrorCode());
            }
        return problemId;
    }
        /**
     * getter of the problemId the solution is associated to
     * @param solutionName the solution name
     * @return the problem id
     */
    public int getProblemId(String solutionName)
    {
        int problemId = -1;
        PreparedStatement stmt = null;
        String query = "SELECT problem_id FROM solution WHERE name = ?;";

        // too long, truncate
        if(solutionName.length() > MAX_SOLUTION_NAME_LENGTH)
            solutionName = solutionName.substring(0, MAX_SOLUTION_NAME_LENGTH);
        if(getId(solutionName) != -1)
            try
            {
                stmt = SQLCON.prepareStatement(query);
                stmt.setString(1, solutionName);
                ResultSet result = stmt.executeQuery();
                result.next();
                problemId = result.getInt("problem_id");
            }
            catch (SQLException e) 
            {
                System.err.println("SQLException: " + e.getMessage());
                System.err.println("SQLState: " + e.getSQLState());
                System.err.println("VendorError: " + e.getErrorCode());
            }
        return problemId;
    }
    /**
     * getter of the user id associated with the solution
     * @param solutionName the solution name
     * @return the user id
     */
    public int getUserId(String solutionName)
    {
        int userId = -1;
        PreparedStatement stmt = null;
        String query = "SELECT user_id FROM solution WHERE name = ?;";

        // too long, truncate
        if(solutionName.length() > MAX_SOLUTION_NAME_LENGTH)
            solutionName = solutionName.substring(0, MAX_SOLUTION_NAME_LENGTH);
        if(getId(solutionName) != -1)
            try
            {
                stmt = SQLCON.prepareStatement(query);
                stmt.setString(1, solutionName);
                ResultSet result = stmt.executeQuery();
                result.next();
                userId = result.getInt("user_id");
            }
            catch (SQLException e) 
            {
                System.err.println("SQLException: " + e.getMessage());
                System.err.println("SQLState: " + e.getSQLState());
                System.err.println("VendorError: " + e.getErrorCode());
            }
        return userId;
    }
    
    /**
     * getter of the user id associated with the solution
     * @param solutionId the solution Id
     * @return the user id
     */
    public int getUserId(int solutionId)
    {
        int userId = -1;
        PreparedStatement stmt = null;
        String query = "SELECT user_id FROM solution WHERE id = ?;";
        if(getName(solutionId) != null)
            try
            {
                stmt = SQLCON.prepareStatement(query);
                stmt.setInt(1, solutionId);
                ResultSet result = stmt.executeQuery();
                result.next();
                userId = result.getInt("user_id");
            }
            catch (SQLException e) 
            {
                System.err.println("SQLException: " + e.getMessage());
                System.err.println("SQLState: " + e.getSQLState());
                System.err.println("VendorError: " + e.getErrorCode());
            }
        return userId;
    }
    /**
     * getter of the unique solution name
     * @param solutionId the id of the solution
     * @return the solution name
     */
    public String getName(int solutionId)
    {
        String solutionName = null;
        PreparedStatement stmt = null;
        String query = "SELECT name FROM solution WHERE id = ?;";    
        if(solutionId > -1)
            try
            {
                stmt = SQLCON.prepareStatement(query);
                stmt.setInt(1, solutionId);
                ResultSet result = stmt.executeQuery();
                result.next();
                solutionName = result.getString("name");
            }
            catch (SQLException e) 
            {

            }
        return solutionName;
    }

    /**
     * getter of the unique solution id
     * @param solutionName the name of the solution
     * @return the solution id
     */
    public int getId(String solutionName)
    {
        int solutionId = -1;
        PreparedStatement stmt = null;
        String query = "SELECT id FROM solution WHERE name = ?;";

        // too long, truncate
        if(solutionName.length() > MAX_SOLUTION_NAME_LENGTH)
            solutionName = solutionName.substring(0, MAX_SOLUTION_NAME_LENGTH);
        if(solutionName != null)
            try
            {
                stmt = SQLCON.prepareStatement(query);
                stmt.setString(1, solutionName);
                ResultSet result = stmt.executeQuery();
                result.next();
                solutionId = result.getInt("id");
            }
            catch (SQLException e) 
            {

            }
        return solutionId;
    }

}