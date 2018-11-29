package edu.sunypoly.cypher.db;
/** 
 * an input was null and a null cannot be put into the database
 * @author Austin Monson (Sannity)
 * @since 11/13/2018
 */
public class NullInputException extends Exception
{
    public NullInputException(String message)
    {
        super(message);
    }
}