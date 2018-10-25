package edu.sunypoly.cypher.backend.service;

//Date: 24 Oct. 2018

//'<System(Cypher)>' error tags?

import java.io.*;

public class DockerSandbox {

	private ProgCompSubmission sub;
	private String javaClassName;
	private String path;

	public DockerSandbox(ProgCompSubmission submission) {
		sub = new ProgCompSubmission(submission.ProblemNumber, submission.TeamID, submission.code,
										submission.language);
		javaClassName = null;
		path = null;
	}

	//Creates a temporary file in which to write and copy a competitor's submission code
	public File writeTmpFile() {
		try {
			File f = new File(System.getProperty("user.home") + System.getProperty("file.separator")
								+ "Cypher" + System.getProperty("file.separator") + "src.tmp");

			if (!f.getParentFile().exists()) {
				if (f.getParentFile().mkdirs()) {
					f.createNewFile();
				}
				else {
					System.err.println("<System(Cypher> Error: Failed to create parent directories for file '"
											+ f.getCanonicalPath() + "'");
				}
			}
			else {
				for (File oldFiles : f.getParentFile().listFiles()) {
					oldFiles.delete();
				}
				f.createNewFile();
			}

			BufferedWriter writer = new BufferedWriter(new FileWriter(f));

			writer.write(sub.code, 0, sub.code.length());

			writer.close();
			return f;
		}

		catch (SecurityException e) {
			System.err.println(e.toString());
			e.printStackTrace();
		}
		catch (IOException e) {
			System.err.println(e.toString());
			e.printStackTrace();
		}
		catch (Exception e) {
			System.err.println(e.toString());
			e.printStackTrace();
		}
		
		return null;
	}

//	public boolean writeTmpFile(ProgCompSubmission compSub) {}

	//Returns the compiler/interpreter for C/C++, Java, or Python 
	public String getCompiler(String language) {
		if (language.equalsIgnoreCase("c++") || language.equalsIgnoreCase("cpp") || language.equalsIgnoreCase("c")) {
			return "gcc";
		}
		else if (language.equalsIgnoreCase("java")) {
			return "openjdk";
		}
		else if (language.equalsIgnoreCase("python")) {
			return "python";
		}
		else {
			return null;
		}
	}

	//Returns the file extension for C/C++, Java, or
	//Python source files
	public String getExtension(String language) {
		if (language.equalsIgnoreCase("c++") || language.equalsIgnoreCase("cpp")) {
			return ".cpp";
		}
		else if (language.equalsIgnoreCase("c")) {
			return ".c";
		}
		else if (language.equalsIgnoreCase("java")) {
			return ".java";
		}
		else if (language.equalsIgnoreCase("python")) {
			return ".py";
		}
		else {
			return null;
		}
	}

	public String getPath() {
		return path;
	}

	//Sets the Java classpath argument when loading a
	//a Java class from the JVM (e.g., "java -cp [CLASSPATH]
	//[CLASSNAME]")
	public boolean setJavaClassPath(String s) {
		if (s.contains("class")) {
			int indexStart = s.indexOf(" class");
			indexStart = s.indexOf(' ', indexStart + 1);
			while (s.charAt(indexStart) == ' ') {
				indexStart = indexStart + 1;
			}
			int indexEnd = s.indexOf(' ', indexStart);
			javaClassName = new String(s.substring(indexStart, indexEnd).trim());

			path = new String(getCompiler(sub.language)
				+ "-cypher:/home/appuser/" + javaClassName + getExtension(sub.language));

			return true;
		}
		else {
			System.err.println("Error: No Java class defined in source code");
			return false;
		}

	}

	public String getJavaClassName() {
		return javaClassName;
	}

	//Copies a C/C++, Java, or Python source file
	//from the host file system into a container
	public boolean copySource() {
		try {
			File f = writeTmpFile();
			if (f != null) {
				ProcessBuilder pb = new ProcessBuilder();
				String errorMessage = new String();
				String s = null;

				if (!getExtension(sub.language).contentEquals(".java")) {
					path = new String(getCompiler(sub.language)
							+ "-cypher:/home/appuser/" + sub.TeamID + getExtension(sub.language));
					pb.command("docker", "container", "cp", f.getCanonicalPath(), getPath());

					Process p = pb.start();

					BufferedReader stdErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));

					while ((s = stdErr.readLine()) != null) {
						errorMessage = errorMessage + s;
					}

					if ((errorMessage != null) && (!errorMessage.isEmpty())) {
						stdErr.close();
						p.waitFor();
						p.destroy();

						File parentDir = f.getParentFile();
						f.delete();
						parentDir.delete();
						
						System.err.println("<System(Cypher)> " + errorMessage);
					}
					else {
						if (stdErr != null) {
							stdErr.close();
						}
						p.waitFor();
						p.destroy();
						
						File parentDir = f.getParentFile();
						f.delete();
						parentDir.delete();
						
						return true;
					}
				}
				else {
					if (setJavaClassPath(sub.code)) {
						pb.command("docker", "container", "cp", f.getCanonicalPath(), getPath());

						Process p = pb.start();

						BufferedReader stdErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));

						while ((s = stdErr.readLine()) != null) {
							errorMessage = errorMessage + s;
						}

						if ((errorMessage != null) && (!errorMessage.isEmpty())) {
							stdErr.close();
							p.waitFor();
							p.destroy();

							File parentDir = f.getParentFile();
							f.delete();
							parentDir.delete();

							System.err.println("<System(Cypher)> " + errorMessage);
						}
						else {
							if (stdErr != null) {
								stdErr.close();
							}
							p.waitFor();
							p.destroy();

							File parentDir = f.getParentFile();
							f.delete();
							parentDir.delete();

							return true;
						}
					}
				}
			}
			else {
				System.err.println("<System(Cypher)> Error: Failed to retrieve a valid source code file");
			}
		}

		catch (InterruptedException e) {
			System.err.println(e.toString());
			e.printStackTrace();
		}
		catch (IOException e) {
			System.err.println(e.toString());
			e.printStackTrace();
		}
		catch (Exception e) {
			System.err.println(e.toString());
			e.printStackTrace();
		}	
		
		return false;
	}

//	public boolean copySource(ProgCompSubmission compSub) {}

	public boolean compile() {
		try {
			if (copySource()) {
				ProcessBuilder pb = new ProcessBuilder();
				BufferedReader stdErr;
				String errorMessage = new String();
				String s = null;

				if (getExtension(sub.language).contentEquals(".cpp")) {

					pb.command("docker", "container", "exec", new String(getCompiler(sub.language)
								+ "-cypher"), "g++", getPath().substring(getPath().indexOf(":") + 1, getPath().length()),
								"-o",
								new String(getPath().substring(getPath().indexOf(":") + 1, getPath().lastIndexOf("/") + 1)
											+ sub.TeamID));

					Process p = pb.start();

					stdErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));

					while ((s = stdErr.readLine()) != null) {
						errorMessage = errorMessage + s + "\n";
					}

					if ((errorMessage != null) && (!errorMessage.isEmpty())) {
						stdErr.close();
						p.waitFor();
						p.destroy();
						sub.compilationStatus = false;
						sub.result = new String("Error: " + errorMessage);
					}

					else {
						if (stdErr != null) {
							stdErr.close();
						}
						p.waitFor();
						p.destroy();
						sub.compilationStatus = true;
						return true;
					}
				}
				else if (getExtension(sub.language).contentEquals(".c")) {
					pb.command("docker", "container", "exec", new String(getCompiler(sub.language)
								+ "-cypher"), "gcc", getPath().substring(getPath().indexOf(":") + 1, getPath().length()), 
								"-o",
								new String(getPath().substring(getPath().indexOf(":") + 1, getPath().lastIndexOf("/") + 1)
											+ sub.TeamID));

					Process p = pb.start();

					stdErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));

					while ((s = stdErr.readLine()) != null) {
						errorMessage = errorMessage + s + "\n";
					}

					if ((errorMessage != null) && (!errorMessage.isEmpty())) {
						stdErr.close();
						p.waitFor();
						p.destroy();
						sub.compilationStatus = false;
						sub.result = new String("Error: " + errorMessage);
					}

					else {
						if (stdErr != null) {
							stdErr.close();
						}
						p.waitFor();
						p.destroy();
						sub.compilationStatus = true;
						return true;
					}
				}
				else if (getExtension(sub.language).contentEquals(".java")) {
					pb.command("docker", "container", "exec", new String(getCompiler(sub.language)
								+ "-cypher"), "javac", getPath().substring(getPath().indexOf(":") + 1, getPath().length()));

					Process p = pb.start();

					stdErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));

					while ((s = stdErr.readLine()) != null) {
						errorMessage = errorMessage + s + "\n";
					}

					if ((errorMessage != null) && (!errorMessage.isEmpty())) {
						stdErr.close();
						p.waitFor();
						p.destroy();
						sub.compilationStatus = false;
						sub.result = new String("Error: " + errorMessage);
					}

					else {
						if (stdErr != null) {
							stdErr.close();
						}
						p.waitFor();
						p.destroy();
						sub.compilationStatus = true;
						return true;
					}
				}
				else {
					System.err.println("<System(Cypher)> Error: Could not determine language of source code");
				}
			}
			else {
				System.err.println("<System(Cypher)> Error: failed to copy source code into Docker container");
			}
		}

		catch (InterruptedException e) {
			System.err.println(e.toString());
			e.printStackTrace();
			sub.result = new String(e.toString());
		}
		catch (IOException e) {
			System.err.println(e.toString());
			e.printStackTrace();
			sub.result = new String(e.toString());
		}
		catch (Exception e) {
			System.err.println(e.toString());
			e.printStackTrace();
			sub.result = new String(e.toString());
		}
		
		return false;

	}

//	public boolean compile(ProgCompSubmission compSub) {}

	//Executes the source code copied into a container
	public ProgCompSubmission execute() {
		try {
			ProcessBuilder pb = new ProcessBuilder();
			BufferedReader stdErr, stdIn;
			String errorMessage = new String();
			String processInput = new String();
			String s = null;
			
			//C++ or C
			if (getExtension(sub.language).contentEquals(".cpp") || getExtension(sub.language).contentEquals(".c")) {
				pb.command("docker", "container", "exec", new String(getCompiler(sub.language)
							+ "-cypher"),
							new String(getPath().substring(getPath().indexOf(":") + 1, getPath().lastIndexOf("."))));

				Process p = pb.start();

				stdErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				stdIn = new BufferedReader(new InputStreamReader(p.getInputStream()));

				//Error stream for execution process
				while ((s = stdErr.readLine()) != null) {
					errorMessage = errorMessage + s + "\n";
				}

				//Input stream for execution process (which is actually an
				//output stream, because processes are weird like that)
				while ((s = stdIn.readLine()) != null) {
					processInput = processInput + s;
				}

				//Errors (runtime) during execution
				if ((errorMessage != null) && (!errorMessage.isEmpty())) {
					stdErr.close();
					if (stdIn != null) {
						stdIn.close();
					}
					p.waitFor();
					p.destroy();
					sub.result = new String("Error: " + errorMessage);
				}
				
				//Standard execution output
				else if ((processInput != null) && (!processInput.isEmpty())) {
					if (stdErr != null) {
						stdErr.close();
					}
					stdIn.close();
					p.waitFor();
					p.destroy();
					sub.result = new String(processInput);
				}
				
				//Neither standard execution output or errors
				else {
					if (stdErr != null) {
						stdErr.close();
					}
					if (stdIn != null) {
						stdIn.close();
					}
					p.waitFor();
					p.destroy();
					throw new Exception("<System(Cypher)> Error: Docker container process failed to write to STDOUT or STDERR streams");
				}
			}
			
			//Java
			else if (getExtension(sub.language).equals(".java")) {
				pb.command("docker", "container", "exec", new String(getCompiler(sub.language)
							+ "-cypher"), "java", "-cp", "/home/appuser", getJavaClassName());

				Process p = pb.start();

				stdErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				stdIn = new BufferedReader(new InputStreamReader(p.getInputStream()));

				//Error stream for execution process
				while ((s = stdErr.readLine()) != null) {
					errorMessage = errorMessage + s + "\n";
				}

				//Input stream for execution process (which is actually an
				//output stream, because processes are weird like that)
				while ((s = stdIn.readLine()) != null) {
					processInput = processInput + s;
				}

				//Errors (runtime) during execution
				if ((errorMessage != null) && (!errorMessage.isEmpty())) {
					stdErr.close();
					if (stdIn != null) {
						stdIn.close();
					}
					p.waitFor();
					p.destroy();
					sub.result = new String("Error: " + errorMessage);
				}
				
				//Standard execution output
				else if ((processInput != null) && (!processInput.isEmpty())) {
					if (stdErr != null) {
						stdErr.close();
					}
					stdIn.close();
					p.waitFor();
					p.destroy();
					sub.result = new String(processInput);
				}
				
				//Neither standard execution output or errors
				else {
					if (stdErr != null) {
						stdErr.close();
					}
					if (stdIn != null) {
						stdIn.close();
					}
					p.waitFor();
					p.destroy();
					throw new Exception("<System(Cypher)> Error: Docker container process failed to write to STDOUT or STDERR streams");
				}
			}
			
			//Python
			else if (getExtension(sub.language).equals(".py")) {
				if (copySource()) {
					pb.command("docker", "container", "exec", new String(getCompiler(sub.language)
								+ "-cypher"), "python3", getPath().substring(getPath().indexOf(":") + 1, getPath().length()));

					Process p = pb.start();

					stdErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
					stdIn = new BufferedReader(new InputStreamReader(p.getInputStream()));

					//Error stream for execution process
					while ((s = stdErr.readLine()) != null) {
						errorMessage = errorMessage + s + "\n";
					}

					//Input stream for execution process (which is actually an
					//output stream, because processes are weird like that)
					while ((s = stdIn.readLine()) != null) {
						processInput = processInput + s;
					}

					//Errors (runtime) during execution
					if ((errorMessage != null) && (!errorMessage.isEmpty())) {
						stdErr.close();
						if (stdIn != null) {
							stdIn.close();
						}
						p.waitFor();
						p.destroy();
						sub.result = new String("Error: " + errorMessage);
					}
					
					//Standard execution output
					else if ((processInput != null) && (!processInput.isEmpty())) {
						if (stdErr != null) {
							stdErr.close();
						}
						stdIn.close();
						p.waitFor();
						p.destroy();
						sub.result = new String(processInput);
					}
					
					//Neither standard execution output or errors
					else {
						if (stdErr != null) {
							stdErr.close();
						}
						if (stdIn != null) {
							stdIn.close();
						}
						p.waitFor();
						p.destroy();
						throw new Exception("<System(Cypher)> Error: Docker container process failed to write to STDOUT or STDERR streams");
					}
				}
				
				//Special case below is because Python is interpreted, not compiled, so the
				//source code for ".py" files is copied into the appropriate container here
				//in the "execute()" method rather than in the "compile()" method
				else {
					throw new IOException("<System(Cypher)> Error: failed to copy source code into Docker container");
				}
			}
			
			//If the language provided in the submission object does not match one of the
			//supported languages (currently C/C++, Java, and Python)
			else {
				throw new Exception("<System(Cypher)> Error: Could not determine language of source code");
			}
		}

		//Alternatively, could "return = null" as an indicator of failure to execute
		catch (InterruptedException e) {
			System.err.println(e.toString());
			e.printStackTrace();
			sub.result = new String("<System(Cypher)> " + e.toString());
		}
		catch (IOException e) {
			//Commented out to avoid double-printing of errors; 'throw'
			//keyword automatically prints the message sent as an argument
			//to an 'Exception([MESSAGE])' declaration
			//System.err.println(e.toString());
			e.printStackTrace();
			sub.result = new String("<System(Cypher)> " + e.toString());
		}
		catch (Exception e) {
			//Commented out to avoid double-printing of errors; 'throw'
			//keyword automatically prints the message sent as an argument
			//to an 'Exception([MESSAGE])' declaration
			//System.err.println(e.toString());
			e.printStackTrace();
			sub.result = new String("<System(Cypher)> " + e.toString());
		}
		
		return sub;
	}

//	public boolean execute(ProgCompSubmission compSub) {}

	public ProgCompSubmission getSubmission() {
		return sub;
	}

	public static void main(String[] args) {
	///*
		ProgCompSubmission s = new ProgCompSubmission(1, "Luminous", new String("print(\"Hello "
										+ "world from Python!\")"),
										"python");
	//*/
	/*
		ProgCompSubmission s = new ProgCompSubmission(1, "Luminous", new String("public class HelloWorld "
										+ "{\npublic HelloWorld() {}\n"
										+ "public static void main(String[] args) {\nSystem.out.println(\"Hello "
										+ "world from Java!\");\nreturn;\n}\n}"),
										"java");
	*/
	/*
		ProgCompSubmission s = new ProgCompSubmission(1, "Luminous", new String("#include <iostream>\n"
										+ "int main (void) {\nstd::cout<<\"Hello world from C++!\";\n"
										+ "return 0;\n}"),
										"c++");
	*/
	/*
		ProgCompSubmission s = new ProgCompSubmission(1, "Luminous", new String("#include <stdio.h>\n"
										+ "int main (void) {\nprintf(\"Hello world from C!\");\n"
										+ "return 0;\n}"),
										"c");
	*/
	
		DockerSandbox docksand = new DockerSandbox(s);

		if (DockerManager.testDockerDaemon()) {
			if (s.language.equalsIgnoreCase("python")) {
					System.out.println(docksand.execute().result);
					//DockerManager.stopContainers("gcc-cypher", "openjdk-cypher", "python-cypher");
			}
			else {
				if (docksand.compile()) {
					System.out.println(docksand.execute().result);
					//DockerManager.stopContainers("gcc-cypher", "openjdk-cypher", "python-cypher");
				}
				else {
					System.err.println(docksand.getSubmission().result);
				}
			}
		}
		else {
			System.err.println("Error: Docker is not running. Terminated execution.");
		}
	}
}











