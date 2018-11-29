package edu.sunypoly.cypher.db;

/**
 * A value already exists in the databse
 * @author Austin Monson (Sannity)
 * @since 11/13/2018
 */
public class AlreadyExistsException extends Exception
{
    public AlreadyExistsException(String message)
    {
        super(message);
    }
}