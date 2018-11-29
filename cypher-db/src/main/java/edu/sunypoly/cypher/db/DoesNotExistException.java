package edu.sunypoly.cypher.db;
/**
 * a value does not exist in the database
 * @author Austin Monson (Sannity)
 * @since 11/13/2018
 */
public class DoesNotExistException extends Exception
{
    public DoesNotExistException(String message)
    {
        super(message);
    }
}