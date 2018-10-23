package edu.sunypoly.cypher.db;

public class AlreadyExistsException extends Exception
{
    public AlreadyExistsException(String message)
    {
        super(message);
    }
}