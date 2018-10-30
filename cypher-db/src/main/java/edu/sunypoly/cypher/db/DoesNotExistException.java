package edu.sunypoly.cypher.db;

public class DoesNotExistException extends Exception
{
    public DoesNotExistException(String message)
    {
        super(message);
    }
}