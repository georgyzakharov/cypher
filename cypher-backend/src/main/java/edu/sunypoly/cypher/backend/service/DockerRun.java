/*
Author: Jacob Hill ("ripcord")
Date of Revision: 09/27/2018
Project: Cypher
Group Members:
	Austin Monson ("Sannity")
	Dylan Navy ("navyd")
	Georgy Zakharov ("georgyzakharov")
Description:
	This program is designed to take a submission,
	represented as a 'ProgCompSubmission' object, and
	attempt to compile/interpret and execute the
	submission code in a virtual Docker container.
	This class builds the file tree for a team's
	submission if it does not yet exist. The tree is
	constructed with the user's home directory (e.g.
	'/home/$USER') as the root, beneath which the
	class constructs the following hierarchy:
	'Cypher/Submissions/$PROGCOMPSUBMISSION.TEAMID/'.
	Ultimately, the full directory path would appear
	similar to the example below using an arbitrary
	user named 'foobar' on the team 'Team_Bar':

		'/home/foobar/Cypher/Submissions/Team_Bar/'

	The 'DockerRun' class writes a Dockerfile named
	'Dockerfile' (in accordance with Docker convention)
	to the directory shown above (variable depending on
	the user). This Dockerfile is used to then build a
	Docker image and subsequently the contents of the
	team directory are copied into a Docker container
	(the image mounted in memory with allocated resources).
	During runtime, the source code associated with the
	'ProgCompSubmission' class is compiled and executed
	within the container; the results of the entire
	procedure are then returned in a String object.
	A general overview of the function calls in 'DockerRun'
	is shown below with step-by-step explanations of
	the Dockerfile creation, Docker image build and
	Docker container execution processes.

	---> 'DockerRun.compExec(ProgCompSubmission submission)'
	-------> if: 'DockerRun.writeDockerfile(ProgCompSubmission submission)'
	-----------> 'DockerRun.runDocker()'
	---------------> if: 'DockerRun.buildDockerImage()'
	------------------> return 'DockerRun.runDockerContainer()'
	---------------> else:
	------------------> return "Error!"
	-------> else:
	-----------> return "Error!"


	*** DockerRun.compExec() ***
	*** DockerRun.writeDockerfile() ***

		1. If a team directory for the submission does
		not yet exist, create a directory with a name
		equivalent to 'ProgCompSubmission.TeamID'.
		2. If there any files within the team directory
		that share a name in common with the current
		submission (i.e., are submissions for the same
		problem, determined by the field
		'ProgCompSubmission.ProblemNumber'), delete
		those files.
		3. Create a source code file containing code
		extracted from the field 'ProgCompSubmission.
		code' (a String object).
		4. If a Dockerfile for the team directory does
		not exist, create one and open the file for
		write mode. Else, open the existing Dockerfile
		for write mode (NOT append).
		5. Write the necessary instructions to the
		Dockerfile for building a Docker image and
		mounting and running a Docker container from
		the image.

	*** DockerRun.compExec() ***
	*** DockerRun.runDocker() ***
	*** DockerRun.buildDockerImage() ***
	
		6. Build a Docker image from the team directory
		via the named Dockerfile (named by convention
		as simply 'Dockerfile'). Assign a tag to the
		built image using the name of the source code
		file (e.g., source file named 'MyClass.java'
		would produce an image tag of 'myclass-java').

	*** DockerRun.runDocker() ***
	*** DockerRun.runDockerContainer() ***

		7. Run the built Docker container. Trap the
		standard error and output streams (stderr and
		stdout) and pipe those streams to an instance
		variable of object type <String> in the
		'DockerRun' class.

	*** DockerRun.runDocker() ***
	*** DockerRun.compExec() ***

		8. Return the results of the 'ProgCompSubmission'
		object source code compilation and execution as
		a String object.

Bugs:
	None known.
Future Development:
	1. Provide support for newlines passed as code
	in a 'ProgCompSubmission' object (i.e., add an
	escape sequence to the newline to escape the
	escape character already prepended to a newline
	sequence by default --> '\\n' instead of '\n').
	Failue to do so results in potential compilation
	errors of the source code due to improper
	formatting.
	2. Docker image control (automatically remove
	old and intermediate Docker images)
	3. Replace all '/' with 'file.separator'
	4. Settle on 'toString()' with 'File' objects
	or on 'getCanonicalPath()'
*/

import java.io.*;

//'final' so as not to extend this class
public final class DockerRun {

	//'imageTag' is a unique alphanumeric
	//identifier applied to Docker images
	//for simplified reference vs. default
	//image IDs
	//
	//'sourceFileName' is a String that
	//contains the filename of the source
	//file constructed with elements from
	//the 'ProgCompSubmission' object
	//
	//'teamDirPath' is a String that contains
	//the absolute path of the directory
	//unique to a particular team; this is
	//the directory in which a team's
	//source files are stored
	//
	//'errorMessage' is a String that acts as an
	//error log
	//
	//'javaClassName' and 'javaDirPath' are variables
	//specific to building the Dockerfile and
	//Docker image for Java source code files;
	//'javaClassName' represents the class name of
	//the outermost class defined in the source
	//file, while 'javaDirPath' represents
	//the special file path in which the Java
	//source file is stored
	//
	//'buildProcess' establishes the context
	//for the subprocesses that invoke Docker
	private static String imageTag;
	private static String sourceFileName;
	private static String teamDirPath;
	private static String errorMessage;
	private static String javaClassName;
	private static String javaDirPath;
	private static ProcessBuilder buildProcess;

	//Does nothing
	private DockerRun() {
	}

	//Returns a file extension
	private static String getExtension(String s) throws UnsupportedFileExtensionException {
		if (s.equalsIgnoreCase("Java")) {
			return ".java";
		}
		else if (s.equalsIgnoreCase("C++")) {
			return ".cpp";
		}
		else if (s.equalsIgnoreCase("C")) {
			return ".c";
		}
		else if (s.equalsIgnoreCase("Python")) {
			return ".py";
		}
		else {
			throw new UnsupportedFileExtensionException(s);
		}
	}

	//Creates the Dockerfile for a Docker image
	private static boolean writeDockerfile(ProgCompSubmission submission) {
		try {
			//'submissionsDir' is the directory in which team
			//directories can be found. If this directory does
			//does not exist, it is created, along with the
			//necessary parent directories.
			File submissionsDir = new File(System.getProperty("user.home") + System.getProperty("file.separator") +
											"Cypher" + System.getProperty("file.separator") + "Submissions");
			if (!submissionsDir.exists()) {
				if (!submissionsDir.mkdirs()) {
					errorMessage = new String("Failed to create directory '" + submissionsDir.getCanonicalPath() + "'");
					return false;
				}
			}

			//'teamDir' is the directory immediately above the
			//source code file.
			//Checking if 'teamDir' exists; if not, 'teamDir' is
			//created.
			File teamDir = new File(submissionsDir.getCanonicalPath() + System.getProperty("file.separator") + submission.TeamID);
			teamDirPath = new String(teamDir.getCanonicalPath());

			if (!teamDir.exists()) {
				if (!teamDir.mkdir()) {
					errorMessage = new String("Failed to create directory '" + teamDir.getCanonicalPath() + "'");
					return false;
				}
			}

			//Old team files left over from previous
			//submissions are deleted to make way
			//for new submissions
			for (File f : teamDir.listFiles()) {
				if (f.isDirectory()) {
					for (File f_dir : f.listFiles()) {
						f_dir.delete();
					}
				}
				if (f.toString().contains(Integer.toString(submission.ProblemNumber))) {
					f.delete();
				}

			}

			//Writing the submission code to a source file
			//in the appropriate team directory
			File sourceCode = new File(teamDirPath + System.getProperty("file.separator") + Integer.toString(submission.ProblemNumber)
											+ getExtension(submission.language));
			if (sourceCode.createNewFile()) {
				BufferedWriter writeToSource = new BufferedWriter(new FileWriter(sourceCode));
				writeToSource.write(submission.code, 0, submission.code.length());
				if (writeToSource != null) {
					writeToSource.close();
				}
			}
			else {
				errorMessage = new String("Failed to create source code file '" + sourceCode.getCanonicalPath() + "'");
				return false;
			}

			File Dockerfile = new File(teamDirPath + System.getProperty("file.separator") + "Dockerfile");

			if (!Dockerfile.exists()) {
				if (!Dockerfile.createNewFile()) {
					errorMessage = new String("Failed to create Dockerfile '" + Dockerfile.getCanonicalPath() + "'");
					return false;
				}
			}

			BufferedWriter writeToDockerfile = new BufferedWriter(new FileWriter(Dockerfile));

			//Subsequent 3 strings are used when writing to the Dockerfile
			String copyToContainerDir = new String("/" + submission.TeamID); //'/' is acceptable here because it is within a container
			String sourceFileNameNoExtension = new String(submission.TeamID);
			sourceFileName = new String(sourceCode.getName());

			if (getExtension(submission.language).contentEquals(".java")) {

				/*
				A notable problem...Java requires that '.java.' files be named with the same
				name as the outermost class defined within the '.java' file. If this class is
				'Booboo', then the file should be named 'Booboo.java'. HOWEVER...the convention
				we have applied for 'Cypher' is to name each source code file with the number
				of the problem for which the source code is written (e.g., '5.c' or '10.py').
				Java does not like this...it wants 'Booboo.java', NOT '3.java'. How to solve? 
				*/

				//Finding the class name in order to construct the appropriate
				//filename
				if (submission.code.contains("class")) {
					int indexStart = submission.code.indexOf(" class");
					indexStart = submission.code.indexOf(' ', indexStart + 1);
					while (submission.code.charAt(indexStart) == ' ') {
						indexStart = indexStart + 1;
					}
					int indexEnd = submission.code.indexOf('{', indexStart);
					javaClassName = new String(submission.code.substring(indexStart, indexEnd).trim());

					//Creating source file for Java code
					File javaSourceFile = new File(teamDirPath + "/" + Integer.toString(submission.ProblemNumber)
													+ "/" + javaClassName + getExtension(submission.language));

					//Creating the directory if it doesn't exist
					File javaTargetDir = new File(teamDirPath + "/" + Integer.toString(submission.ProblemNumber));
					javaDirPath = new String(javaTargetDir.getCanonicalPath());
					if (!javaTargetDir.exists()) {
						if (!javaTargetDir.mkdir()) {
							errorMessage = new String("Failed to create directory '" + javaTargetDir.getCanonicalPath() + "'");
							return false;
						}
					}

					//Deleting old files if they exist within the directory
					for (File f : javaTargetDir.listFiles()) {
						if (f.toString().contains(javaClassName)) {
							f.delete();
						}
					}

					//Creating the Java source code file and
					//writing the 'ProgCompSubmission' code to
					//that file
					if (javaSourceFile.createNewFile()) {
						BufferedWriter writeToJavaSource = new BufferedWriter(new FileWriter(javaSourceFile));
						writeToJavaSource.write(submission.code, 0, submission.code.length());
						if (writeToJavaSource != null) {
							writeToJavaSource.close();
						}
					}
					else {
						errorMessage = new String("Failed to create Java source code file '" + javaSourceFile.getCanonicalPath() + "'");
						return false;
					}

					File javaDockerfile = new File(javaTargetDir.getCanonicalPath() + "/Dockerfile");

					//Creating the Java Dockerfile if it does
					//not currently exist
					if (!javaDockerfile.exists()) {
						if (!javaDockerfile.createNewFile()) {
							errorMessage = new String("Failed to create Java Dockerfile '" + javaDockerfile.getCanonicalPath() + "'");
							return false;
						}
					}


					//Writing to the Java Dockerfile
					String s = new String("FROM openjdk\nWORKDIR " + javaTargetDir + "\nCOPY . "
						+ copyToContainerDir + "/" + Integer.toString(submission.ProblemNumber)
						+ "\nCMD [\"/bin/sh\", \"-c\", \"javac " + copyToContainerDir + "/"
						+ Integer.toString(submission.ProblemNumber) + "/" + javaClassName
						+ getExtension(submission.language) + " && java -cp " + copyToContainerDir
						+ "/" + Integer.toString(submission.ProblemNumber) + " " + javaClassName
						+ "\"]");
					BufferedWriter writeToJavaDockerfile = new BufferedWriter(new FileWriter(javaDockerfile));
					writeToJavaDockerfile.write(s, 0, s.length());

					if (writeToJavaDockerfile != null) {
						writeToJavaDockerfile.close();
					}
				}
				else {
					errorMessage = new String("Could not find the outermost class name in the file '"
												+ sourceCode.getCanonicalPath() + "'");
					return false;
				}
			}
			else if (getExtension(submission.language).contentEquals(".cpp")) {
				String s = new String("FROM gcc\nWORKDIR " + teamDirPath + "\nCOPY . " + copyToContainerDir
						+ "\nCMD [\"/bin/sh\", \"-c\", \"g++ " + copyToContainerDir + "/" + sourceFileName
						+ " -o " + copyToContainerDir + "/" + sourceFileNameNoExtension + " && "
						+ copyToContainerDir + "/" + sourceFileNameNoExtension + "\"]");
				writeToDockerfile.write(s, 0, s.length());
			}
			else if (getExtension(submission.language).contentEquals(".c")) {
				String s = new String("FROM gcc\nWORKDIR " + teamDirPath + "\nCOPY . " + copyToContainerDir
						+ "\nCMD [\"/bin/sh\", \"-c\", \"gcc " + copyToContainerDir + "/" + sourceFileName
						+ " -o " + copyToContainerDir + "/" + sourceFileNameNoExtension + " && "
						+ copyToContainerDir + "/" + sourceFileNameNoExtension + "\"]");
				writeToDockerfile.write(s, 0, s.length());
			}
			//else if (getExtension(submission.language).equals(".py")) {
			else {
				String s = new String("FROM python\nWORKDIR " + teamDirPath + "\nCOPY . " + copyToContainerDir
						+ "\nCMD [\"/bin/sh\", \"-c\", \"python3 " + copyToContainerDir + "/" + sourceFileName
						+ "\"]");
				writeToDockerfile.write(s, 0, s.length());
			}

			if (writeToDockerfile != null) {
				writeToDockerfile.close();
			}

			return true;
		}

		catch (UnsupportedFileExtensionException e) {
			System.out.println(e.toString());
			e.printStackTrace();
			return false;
		}

		catch (SecurityException e) {
			System.out.println(e.toString());
			e.printStackTrace();
			return false;
		}

		catch (IOException e) {
			System.out.println(e.toString());
			e.printStackTrace();
			return false;
		}

		catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
			return false;
		}
	}

	//Method used to invoke Docker image building
	//and Docker container instantiation
	private static String runDocker() {
		if (buildDockerImage()) {
			return runDockerContainer();
		}
		else {
			return errorMessage;
		}
	}

	//Method for building a Docker image from a provided
	//source code file path
	private static boolean buildDockerImage() {
		try {

			//Testing if the source code is Java or not
			if (javaClassName != null && javaDirPath != null) {
				int i = 1, k = 2;
				boolean clean = true;
				while (clean) {
					if (javaDirPath.charAt(javaDirPath.length() - i) == System.getProperty("file.separator").charAt(0)) {
						k--;
					}
					if (k == 0) {
						imageTag = new String(javaDirPath.substring(javaDirPath.length() - (i - 1), javaDirPath.length()));
						imageTag = imageTag.replace(System.getProperty("file.separator"), "-") + "-java";
						imageTag = imageTag.toLowerCase();
						clean = false;
					}
					i++;
				}
			}
			else {
				imageTag = new String(teamDirPath.substring(teamDirPath.lastIndexOf(System.getProperty("file.separator"))
					+ 1, teamDirPath.length())
					+ "-"
					+ sourceFileName.substring(0, sourceFileName.lastIndexOf("."))
					+ "-" + sourceFileName.substring(sourceFileName.lastIndexOf(".") + 1, sourceFileName.length()));
				imageTag = imageTag.toLowerCase();
			}


			buildProcess = new ProcessBuilder();

			buildProcess.command("docker", "build", "-t", imageTag, ".");

			if (imageTag.contains("java")) {
				buildProcess.directory(new File(javaDirPath));
			}
			else {
				buildProcess.directory(new File(teamDirPath));
			}

			Process p = buildProcess.start();

			//Getting the errorMessage (if any) from the process
			BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			String s = null;
			errorMessage = new String();
			while ((s = stdError.readLine()) != null) {
				errorMessage = errorMessage + s + "\n";
			}

			//LOGIC PROBLEM: 'if (errorMessage != null || !errorMessage.isEmpty())'
			if (errorMessage != null && !errorMessage.isEmpty()) {
				if (stdError != null) {
					stdError.close();
				}
				p.waitFor();
				p.destroy();
				return false;
			}
			else {
				if (stdError != null) {
					stdError.close();
				}
				p.waitFor();
				p.destroy();
				return true;
			}

		}

		catch (InterruptedException e) {
			errorMessage = new String(e.toString() + "\nTerminated unexpectedly in 'buildDockerImage()'!");
			e.printStackTrace();
			return false;
		}

		catch (IOException e) {
			errorMessage = new String(e.toString() + "\nTerminated unexpectedly in 'buildDockerImage()'!");
			e.printStackTrace();
			return false;
		}

		catch (StringIndexOutOfBoundsException e) {
			errorMessage = new String(e.toString() + "\nTerminated unexpectedly in 'buildDockerImage()'!");
			e.printStackTrace();
			return false;
		}

		catch (Exception e) {
			errorMessage = new String(e.toString() + "\nTerminated unexpectedly in 'buildDockerImage()'!");
			e.printStackTrace();
			return false;
		}
	}

	//Private because this method should only be called after a Docker
	//image has been built in 'buildDockerImage()'
	private static String runDockerContainer() {
		try {
			buildProcess.command("docker", "run", "--rm", imageTag);

			Process p = buildProcess.start();

			//Getting errorMessage (if any) and output from the process
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			String s = null;
			String results = new String();
			errorMessage = new String();

			while ((s = stdInput.readLine()) != null) {
				results = results + s;
			}

			while ((s = stdError.readLine()) != null) {
				errorMessage = errorMessage + s + "\n";
			}

			if (errorMessage != null && !errorMessage.isEmpty()) {
				if (stdError != null) {
					stdError.close();
				}
				if (stdInput != null) {
					stdInput.close();
				}
				p.waitFor();
				p.destroy();
				return errorMessage;
			}
			else if (results != null && !results.isEmpty()) {
				if (stdError != null) {
					stdError.close();
				}
				if (stdInput != null) {
					stdInput.close();
				}
				p.waitFor();
				p.destroy();
				return results;
			}
			//The default 'else' case below is unlikely but is
			//provided as insurance for additional error handling
			else {
				if (stdError != null) {
					stdError.close();
				}
				if (stdInput != null) {
					stdInput.close();
				}
				p.waitFor();
				p.destroy();
				return "Docker container process did not write to STDERR or STDOUT streams.";
			}
		}

		catch (InterruptedException e) {
			errorMessage = new String(e.toString() + "\nTerminated unexpectedly in 'runDockerContainer()'!");
			e.printStackTrace();
			return errorMessage;
		}
		
		catch (IOException e) {
			errorMessage = new String(e.toString() + "\nTerminated unexpectedly in 'runDockerContainer()'!");
			e.printStackTrace();
			return errorMessage;
		}

		catch (Exception e) {
			errorMessage = new String(e.toString() + "\nTerminated unexpectedly in 'runDockerContainer()'!");
			e.printStackTrace();
			return errorMessage;
		}
	}

	private static void nullAll() {
		imageTag = null;
		sourceFileName = null;
		teamDirPath = null;
		errorMessage = null;
		buildProcess = null;
		javaClassName = null;
		javaDirPath = null;

		return;
	}

	//The 'compilation & execution' method,
	//which invokes the 'runDocker()' method
	public static String compExec(ProgCompSubmission submission) {

		nullAll();

		if (writeDockerfile(submission)) {
			return runDocker();
		}
		else {
			return "Error: " + errorMessage;
		}
	}
}
