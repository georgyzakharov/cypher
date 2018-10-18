import java.sql.*;

public class TeamManager
{
    private static Connection SQLCON = null; 
    private static final int MAX_TEAM_NAME_LENGTH = 50;
    public TeamManager(Connection INSQLCON)
    {
        SQLCON = INSQLCON;
        if(SQLCON == null)
            System.err.println("THIS IS IT");
    }

    public boolean create(String teamName) throws AlreadyExistsException, NullInputException, InvalidDataException
    {
        boolean success = false;
        PreparedStatement stmt = null;
        String query = "INSERT INTO team(name) VALUES (?);";
        
        //check if team name is null
        if(teamName == null)
            throw new NullInputException("teamName cannot be null");
        //check if teamName is empty string 
        if(teamName.isEmpty())
            throw new InvalidDataException("No team name provided");
        //check teamName length, truncate to 50 chars
        if(teamName.length() > MAX_TEAM_NAME_LENGTH)
            teamName = teamName.substring(0, MAX_TEAM_NAME_LENGTH); 

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
    public boolean update(int teamId, String newTeamName) throws DoesNotExistException, AlreadyExistsException, InvalidDataException, NullInputException 
    {
        boolean success = false;
        PreparedStatement stmt = null;
        PreparedStatement fkStatement = null;
        String query = "UPDATE team SET name = ? WHERE id = ?";
        String fk = "SET FOREIGN_KEY_CHECKS=?;";
  
        //check if team name is null
        if(newTeamName == null)
            throw new NullInputException("teamName cannot be null");
        //check if teamName is empty string 
        if(newTeamName.isEmpty())
            throw new InvalidDataException("No team name provided");
        //check teamName length, truncate to 50 chars
        if(newTeamName.length() > MAX_TEAM_NAME_LENGTH)
            newTeamName = newTeamName.substring(0, MAX_TEAM_NAME_LENGTH); 

        if(getName(teamId) != null && (getId(newTeamName) == -1)) //old teamName does exist, and newTeamName doesnt
        {
            try 
            {
                fkStatement = SQLCON.prepareStatement(fk);
                fkStatement.setInt(1, 0);
                fkStatement.executeUpdate();

                stmt = SQLCON.prepareStatement(query);
                stmt.setString(1, newTeamName);
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
            if(getId(newTeamName) != -1)
                success = true;    
        }
        else if(getId(newTeamName) != -1) //newTeamName already exists
            throw new AlreadyExistsException(newTeamName + " already exists!");
        else //The team youre trying to replace doesnt exist.
            throw new DoesNotExistException(teamId + " does not exist!");
        
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
        if(teamName != null)
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
        if(teamId != -1)
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