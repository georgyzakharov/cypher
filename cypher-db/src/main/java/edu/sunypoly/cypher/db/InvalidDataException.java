package edu.sunypoly.cypher.db;
/**
 * Incorrect data was provided to the function, it can not be stored in the database
 * @author Austin Monson (Sannity)
 * @since 11/13/2018
 * 
 */
public class InvalidDataException extends Exception
{
    public InvalidDataException(String message)
    {
        super(message);
    }
}