package edu.sunypoly.cypher.backend.service;
/*
Author: Jacob Hill ("ripcord")
Date of Revision: 09/26/2018
Project: Cypher
Group Members:
	Austin Monson ("Sannity")
	Dylan Navy ("navyd")
	Georgy Zakharov ("georgyzakharov")
Description:
	This program is designed to take a source file,
	which is defined here as a file with the extension
	".java", ".c", ".cpp", or ".py", and attempt to
	compile/interpret and execute that file in a
	virtual Docker container. The program requires the
	absolute file path of the source file. The directory
	immediately above the source code file is treated
	as the "team" directory, that is the directory
	in which the created Dockerfile is stored and which
	the program copies into the created Docker container.
	For example, consider a file with the following path:
		"/home/cypher/src/foobar.java"
	The contents of the directory "src/" would be
	copied into a Docker container at runtime and
	the Dockerfile for "foobar.java" (named by default
	in accordance with Docker convention as simply
	"Dockerfile") would be created and stored within
	"src/".
Bugs:
	None known.
Future Development:
	1. Add support for returned line separators (i.e.,
	newlines or "\n") in subprocess input, which is
	piped to STDOUT on a CLI.
*/


import java.io.*;

//'final' so as not to extend this class
public final class DockerRun {

	//'imageTag' is a unique alphanumeric
	//identifier applied to Docker images
	//for simplified reference vs. default
	//image IDs
	//
	//'errors' is a String that acts as an
	//error log
	//
	//'pb' establishes the context for the
	//subprocesses that invoke Docker
	private static String imageTag;
	private static String errors;
	private static ProcessBuilder pb;

	//Does nothing
	private DockerRun() {
	}

	//Returns a file extension
	public static String getExtension (String s) {
		s = s.substring(s.lastIndexOf(File.separator), s.length());
		return s.substring(s.lastIndexOf("."), s.length());
	}

	//Creates the Dockerfile for a Docker image
	public static boolean writeDockerfile(String source) {
		try {
			//'teamDir' is the directory immediately above the
			//source code file
			File sourceCode = new File(source);
			File teamDir = new File(source.substring(0, source.lastIndexOf(File.separator)));

			if (!sourceCode.getCanonicalFile().exists() || !sourceCode.getCanonicalFile().isFile()) {
				return false;
			}
			else if (!teamDir.getCanonicalFile().exists() || !teamDir.getCanonicalFile().isDirectory()) {
				return false;
			}
			else {
				File Dockerfile = new File(teamDir.getCanonicalPath() + "/Dockerfile");

				if (!Dockerfile.exists()) {
					Dockerfile.createNewFile();
				}

				FileOutputStream out = new FileOutputStream(Dockerfile);

				//Subsequent 3 strings are used when writing to the Dockerfile
				String copyToContainerDir = new String(teamDir.toString().substring(teamDir.toString().lastIndexOf(File.separator), teamDir.toString().length()));
				String sourceFileName = new String(source.substring(source.lastIndexOf(File.separator), source.length()));
				String sourceFileNameNoExtension = sourceFileName.substring(1, sourceFileName.lastIndexOf("."));

				if (getExtension(source).equals(".java")) {
					String s = new String("FROM openjdk\nWORKDIR " + teamDir + "\nCOPY . " + copyToContainerDir
							+ "\nCMD [\"/bin/sh\", \"-c\", \"javac " + copyToContainerDir + sourceFileName
							+ " && java -cp " + copyToContainerDir + " " + sourceFileNameNoExtension + "\"]");
					out.write(s.getBytes());
				}
				else if (getExtension(source).equals(".cpp")) {
					String s = new String("FROM gcc\nWORKDIR " + teamDir + "\nCOPY . " + copyToContainerDir
							+ "\nCMD [\"/bin/sh\", \"-c\", \"g++ " + copyToContainerDir + sourceFileName
							+ " -o " + copyToContainerDir + "/" + sourceFileNameNoExtension + " && "
							+ copyToContainerDir + "/" + sourceFileNameNoExtension + "\"]");
					out.write(s.getBytes());
				}
				else if (getExtension(source).equals(".c")) {
					String s = new String("FROM gcc\nWORKDIR " + teamDir + "\nCOPY . " + copyToContainerDir
							+ "\nCMD [\"/bin/sh\", \"-c\", \"gcc " + copyToContainerDir + sourceFileName
							+ " -o " + copyToContainerDir + "/" + sourceFileNameNoExtension + " && "
							+ copyToContainerDir + "/" + sourceFileNameNoExtension + "\"]");
					out.write(s.getBytes());
				}
				else if (getExtension(source).equals(".py")) {
					String s = new String("FROM python\nWORKDIR " + teamDir + "\nCOPY . " + copyToContainerDir
							+ "\nCMD [\"/bin/sh\", \"-c\", \"python3 " + copyToContainerDir + sourceFileName
							+ "\"]");
					out.write(s.getBytes());
				}
				else {
					System.out.println("Error: Unrecognized source file extension...'" + getExtension(source) + "'");
					if (out != null) {
						out.close();
					}
					return false;
				}

				if (out != null) {
					out.close();
				}
			}

			return true;
		}

		catch (SecurityException e) {
			System.out.println("<SecurityException> thrown!");
			e.printStackTrace();
			return false;
		}

		catch (IOException e) {
			System.out.println("<IOException> thrown!");
			e.printStackTrace();
			return false;
		}

		catch (Exception e) {
			System.out.println("<Exception> thrown!");
			e.printStackTrace();
			return false;
		}
	}

	//Method used to invoke Docker image building
	//and Docker container instantiation
	public static String runDocker(String source) {
		if (buildDockerImage(source)) {
			return runDockerContainer();
		}
		else {
			return errors;
		}
	}

	//Method for building a Docker image from a provided
	//source code file path
	public static boolean buildDockerImage(String source) {
		try {
			imageTag = new String(source.substring(source.lastIndexOf(File.separator) + 1, source.length()).toLowerCase());
			imageTag = imageTag.substring(0, imageTag.lastIndexOf(".")) + "-" + imageTag.substring(imageTag.lastIndexOf(".") + 1, imageTag.length());
			pb = new ProcessBuilder();

			pb.command("docker", "build", "-t", imageTag, ".");
			pb.directory(new File(source.substring(0, source.lastIndexOf(File.separator))));

			Process p = pb.start();

			//Getting the errors (if any) from the process
			BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			String s = null;
			errors = new String();
			while ((s = stdError.readLine()) != null) {
				errors = errors + s + "\n";
			}

			//LOGIC PROBLEM: 'if (errors != null || !errors.isEmpty())'
			if (errors != null && !errors.isEmpty()) {
				p.waitFor();
				p.destroy();
				return false;
			}
			else {
				p.waitFor();
				p.destroy();
				return true;
			}

		}

		catch (InterruptedException e) {
			System.out.println("<InterruptedException> thrown!");
			e.printStackTrace();
			return false;
		}

		catch (IOException e) {
			errors = new String("<IOException> thrown in 'buildDockerImage()'!");
			e.printStackTrace();
			return false;
		}

		catch (Exception e) {
			errors = new String("<Exception> thrown in 'buildDockerImage()'!");
			e.printStackTrace();
			return false;
		}
	}

	//Private because this method should only be called after a Docker
	//image has been built in 'buildDockerImage()'
	private static String runDockerContainer() {
		try {
			pb.command("docker", "run", "--rm", imageTag);

			Process p = pb.start();

			//Getting errors (if any) and output from the process
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			String s = null;
			String results = new String();
			errors = new String();

			while ((s = stdInput.readLine()) != null) {
				results = results + s;
			}

			while ((s = stdError.readLine()) != null) {
				errors = errors + s + "\n";
			}

			if (errors != null && !errors.isEmpty()) {
				p.waitFor();
				p.destroy();
				return errors;
			}
			else if (results != null && !results.isEmpty()) {
				p.waitFor();
				p.destroy();
				return results;
			}
			//The default 'else' case below is unlikely but is
			//provided as insurance for additional error handling
			else {
				p.waitFor();
				p.destroy();
				return "Docker container process did not produce errors or standard output.";
			}
		}

		catch (InterruptedException e) {
			System.out.println("<InterruptedException> thrown!");
			e.printStackTrace();
			return "Error!";
		}
		
		catch (IOException e) {
			System.out.println("<IOException> thrown!");
			e.printStackTrace();
			return "Error!";
		}

		catch (Exception e) {
			System.out.println("<Exception> thrown!");
			e.printStackTrace();
			return "Error!";
		}
	}

	//The 'compilation & execution' method,
	//which invokes the 'runDocker()' method
	public static String compExec(String s) {

		//String 'source' is created so as
		//to pass a brand-new guranteed String
		//object
		String source = new String(s);

		if (writeDockerfile(source)) {
			return runDocker(source);
		}
		else {
			return "Failed to write Dockerfile. Check source code filename.";
		}
	}

	//Solely a testing method, will be removed in future versions
	public static void main(String[] args) {

		System.out.println(DockerRun.compExec(args[args.length-1]));

		return;
	}
}
