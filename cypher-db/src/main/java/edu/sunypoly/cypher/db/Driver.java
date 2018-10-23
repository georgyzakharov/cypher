package edu.sunypoly.cypher.db;

import java.io.File;
import java.nio.file.Files;

public class Driver
{
    public static void main(String[] args) throws AlreadyExistsException, NullInputException, InvalidDataException
    {
        MIS Manager = new MIS("jdbc:mysql://localhost/cypher_db?useSSL=false", "root", "DamienBro");
        File INproblemDesc = new File("ElvisDesc.txt");
        File INproblemTest = new File("ElvisTest.txt");
        File INsolution = new File("SampleSolution.txt");
        byte[] problemDesc = null;
        byte[] problemTest = null;
        byte[] solution = null;
        
        Manager.Team.create("BestTeam#1");
        
        
        try
        {
            problemDesc = Files.readAllBytes(INproblemDesc.toPath());
            problemTest = Files.readAllBytes(INproblemTest.toPath());
            solution = Files.readAllBytes(INsolution.toPath());

        }
        catch(Exception exception)
        {   }
        /*
        try
        {
            //TEST SET 1, SIMPLE CREATIONS AND DELETIONS AND MODIFICATIONS
        
            System.err.println("____________________________________________________________________");
            System.err.println("*Deleteing HeLLoZZ INIT");
            System.err.println("*" + Manager.Team.delete("HeLLoZZ"));
            System.err.println("*Deleteing Hello INIT");
            System.err.println("*" + Manager.Team.delete("Hello") + "\n");

            System.err.println("*Creating Hello Team");
            System.err.println(Manager.Team.create("Hello"));
            System.err.println("-- " + Manager.Team.getName(Manager.Team.getId("Hello")) + " ----- " + Manager.Team.getId("Hello")); 
            System.err.println("*Changing the team Name to HeLLoZZ");
            System.err.println(Manager.Team.update(Manager.Team.getId("Hello"), "HeLLoZZ"));

            System.err.println("____________________________________________________________________");
            System.err.println("\n\n\n**DELETEING PROBLEM Elvis INIT");
            System.err.println("**" + Manager.Problem.delete("Elvis"));
            Manager.Problem.delete("JohnnybCash");
            System.err.println("**Creating Problem Elvis");
            System.err.println("**" + Manager.Problem.create("Elvis", problemDesc, problemTest));

            System.err.println("**Changing problem Elvis");
            System.err.println("**" + Manager.Problem.update(Manager.Problem.getId("Elvis"), "JohnnybCash", problemTest, problemDesc));


            System.err.println("____________________________________________________________________");
            System.err.println("\n\n\n***DELETING SOLUTION MySol INIT");
            System.err.println("***" + Manager.Solution.delete("MySol"));
            System.err.println("*** CREATING MySol SOLUTION");
            System.err.println("***" + Manager.Solution.create("MySol", Manager.Team.getId("HeLLoZZ"), Manager.Problem.getId("JohnnybCash"), "JAVA", solution));
            System.err.println("*** changing MySol");
            Manager.Team.delete("temp team");
            Manager.Team.create("temp team");
            System.err.println("***" + Manager.Solution.update(Manager.Solution.getId("MySol"), "YourSol", Manager.Team.getId("temp team") , Manager.Problem.getId("JohnnybCash"), "C++", 20, problemDesc));
             
            
            

            
        }
        catch(AlreadyExistsException e)
        {
            System.err.println(e.getMessage());
        }
        catch(DoesNotExistException e)
        {
            System.err.println(e.getMessage());
        }
        catch(Exception e)
        {
            System.err.println(e.getMessage());
        }
        */

        
        try
        {
            //Truncating Team Names
            //Manager.Team.create("12345678901234567890123456789012345678901234567890123456345uio954345679543456789654345776543456794345679765456795445679976545678909765456789765434569097654345679r43456797654345797654345678976543456789765435678976543456797654345678976543456789076543456789765434567909765432345678909765432345678909765434567890976543234567i909765edcvbhju765fghju76tfvbju76tfvbhu6tfvbhu76tfghu76tfvbhu76tfvbhuytfvbhu765rfvghu76rfvhu765rfghju76fvhu765rfvbju76tfvghju76tghju76");
            //Manager.Team.delete("12345678901234567890123456789012345678901234567890123456345uio954345679543456789654345776543456794345679765456795445679976545678909765456789765434569097654345679r43456797654345797654345678976543456789765435678976543456797654345678976543456789076543456789765434567909765432345678909765432345678909765434567890976543234567i909765edcvbhju765fghju76tfvbju76tfvbhu6tfvbhu76tfghu76tfvbhu76tfvbhuytfvbhu765rfvghu76rfvhu765rfghju76fvhu765rfvbju76tfvghju76tghju76");

            //Manager.Team.create("ThisTeam");
            //System.err.println(Manager.Team.update((~0 >> 999999999), "LOLZ "));

            //Manager.Problem = null;
            Manager.Team.delete(100);
            byte[] austin = {'b'};
            byte[] austincool = {'a'};
            Manager.Problem.delete("HelloWorld");
            Manager.Problem.create("HelloWorld", austin, austincool);
            System.err.println(Manager.Problem.getId("HelloWorld"));

        
        }
        catch(Exception e)
        {
            System.err.println(e.getMessage()); 
        }
    }
}