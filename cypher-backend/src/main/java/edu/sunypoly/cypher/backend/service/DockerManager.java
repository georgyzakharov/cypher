package edu.sunypoly.cypher.backend.service;

//Date: 24 Oct. 2018

import java.io.*;
import java.util.Random;

public class DockerManager {

	private File gccDockerfile;
	private File openjdkDockerfile;
	private File pythonDockerfile;

	public DockerManager() {
		// "/home/$USER/Cypher/[gcc, openjdk, python]/Dockerfile"
		try {
			gccDockerfile = new File(System.getProperty("user.home") + System.getProperty("file.separator") + "Cypher"
										+ System.getProperty("file.separator") + "gcc" + System.getProperty("file.separator")
										+ "Dockerfile");
			openjdkDockerfile = new File(System.getProperty("user.home") + System.getProperty("file.separator") + "Cypher"
										+ System.getProperty("file.separator") + "openjdk" + System.getProperty("file.separator")
										+ "Dockerfile");
			pythonDockerfile = new File(System.getProperty("user.home") + System.getProperty("file.separator") + "Cypher"
										+ System.getProperty("file.separator") + "python" + System.getProperty("file.separator")
										+ "Dockerfile");
			if (!gccDockerfile.exists() || !openjdkDockerfile.exists() || !pythonDockerfile.exists()) {
				gccDockerfile.getParentFile().mkdirs();
				gccDockerfile.createNewFile();

				openjdkDockerfile.getParentFile().mkdirs();
				openjdkDockerfile.createNewFile();

				pythonDockerfile.getParentFile().mkdirs();
				pythonDockerfile.createNewFile();
			}
		}

		catch (SecurityException e) {
			System.err.println(e.toString());
			e.printStackTrace();
		}

		catch (IOException e) {
			System.err.println(e.toString());
			e.printStackTrace();
		}
	}

	//Appends a string 's' to file 'f'
	public boolean write(File f, String s) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(f.getCanonicalFile(), true));

			writer.write(s, 0, s.length());

			writer.close();
			return true;
		}

		catch (IOException e) {
			System.err.println(e.toString());
			e.printStackTrace();
		}
		
		return false;
	}

	//Writes a string 's' to file 'f', overwriting the file
	//with the length of the string if "true" is passed as the
	//third argument. If false is passed instead, the write(file, string)
	//method is called.
	public boolean write(File f, String s, boolean overwrite) {
		try {
			if (overwrite) {
				BufferedWriter writer = new BufferedWriter(new FileWriter(f.getCanonicalFile()));

				writer.write(s, 0, s.length());

				writer.close();
				return true;
			}
			else {
				return write(f, s);
			}
		}

		catch (IOException e) {
			System.err.println(e.toString());
			e.printStackTrace();
		}
		
		return false;
	}

	//Remove old Dockerfiles if they exist (they shouldn't)
	public boolean initialize() {
		try {
			for (File f : gccDockerfile.getParentFile().getCanonicalFile().listFiles()) {
				if (f.exists()) {
					f.delete();
				}
			}
			gccDockerfile.createNewFile();

			for (File f : openjdkDockerfile.getParentFile().getCanonicalFile().listFiles()) {
				if (f.exists()) {
					f.delete();
				}
			}
			openjdkDockerfile.createNewFile();

			for (File f : pythonDockerfile.getParentFile().getCanonicalFile().listFiles()) {
				if (f.exists()) {
					f.delete();
				}
			}
			pythonDockerfile.createNewFile();
		}

		catch (IOException e) {
			System.err.println(e.toString());
			e.printStackTrace();
			return false;
		}
		catch (Exception e) {
			System.err.println(e.toString());
			e.printStackTrace();
			return false;
		}

		return true;
	}

	//BE CAREFUL!! This method will recursively delete all files
	//within the specified path, including directories!
	//To delete "/home/$USER/Cypher/[gcc,openjdk,python]/Dockerfile",
	//call "delete()" like so:
	//		File f = new File("/home/$USER/Cypher/[gcc,openjdk,python]/Dockerfile")
	//		delete("/home/$USER/Cypher")
	public boolean delete(File f) {
		try {
			if (f.getCanonicalFile().exists()) {
				if (f.isDirectory()) {
					for (File f_dir : f.listFiles()) {
						if (f_dir.isDirectory()) {
							delete(f_dir);
						}
						else {
							f_dir.delete();
						}
					}
				}
				f.delete();
				return true;
			}
			else {
				System.err.println("Error: File '" + f.getCanonicalPath() + "' does not exist");
			}
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
		
		return false;
	}

	//Checks if Docker is running
	public static boolean testDockerDaemon() {
		try {
			ProcessBuilder pb = new ProcessBuilder();
			BufferedReader stdErr;
			String errorMessage = new String();
			String s = null;

			pb.command("docker", "version");

			Process p = pb.start();

			stdErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			while ((s = stdErr.readLine()) != null) {
				errorMessage = errorMessage + s;
			}

			if ((errorMessage != null) && (!errorMessage.isEmpty())) {
				stdErr.close();
				p.waitFor();
				p.destroy();
				System.err.println("<System(Cypher)> " + errorMessage);
			}
			else {
				if (stdErr != null) {
					stdErr.close();
				}
				p.waitFor();
				p.destroy();
				return true;
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

	//Checks if Docker container exists
	public static boolean checkDockerContainer(String imageTag) {
		try {
			ProcessBuilder pb = new ProcessBuilder();
			pb.command("docker", "container", "ps", "-a");
			Process p = pb.start();

			BufferedReader stdIn = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String results = new String();
			String s = null;

			while ((s = stdIn.readLine()) != null) {
				results = results + s;
			}

			if (results.contains(imageTag)) {
				if (stdIn != null) {
					stdIn.close();
				}
				p.waitFor();
				p.destroy();
				return true;
			}
			else {
				if (stdIn != null) {
					stdIn.close();
				}
				p.waitFor();
				p.destroy();
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

	//Checks if Docker image exists
	public boolean checkDockerImage(String imageTag) {
		try {
			//List all Docker images and search images
			//for matching image tag
			ProcessBuilder pb = new ProcessBuilder();
			pb.command("docker", "images");
			Process p = pb.start();

			BufferedReader stdIn = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String results = new String();
			String s = null;

			while ((s = stdIn.readLine()) != null) {
				results = results + s;
			}

			if (results.contains(imageTag)) {
				if (stdIn != null) {
					stdIn.close();
				}
				p.waitFor();
				p.destroy();
				return true;
			}
			else {
				if (stdIn != null) {
					stdIn.close();
				}
				p.waitFor();
				p.destroy();
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

	//Removes Docker image if it exists, stopping and removing
	//Docker containers with the same name along the way
	public boolean removeDockerImage(String imageTag) {
		try {
			ProcessBuilder pb = new ProcessBuilder();
			BufferedReader stdErr;
			String errorMessage = new String();
			String s = null;

			if (checkDockerContainer(imageTag)) {
				//Stop any running containers with the same
				//name as the image tag
				if (stopDockerContainer(imageTag)) {
					System.out.println("Stopped Docker container '" + imageTag + "'");
				}

				//Remove any stopped containers with the same
				//name as the image tag
				if (removeDockerContainer(imageTag)) {
					System.out.println("Removed Docker container '" + imageTag + "'");
				}
			}

			if (checkDockerImage(imageTag)) {
				//Remove any Docker images with the same
				//name as the image tag
				pb.command("docker", "rmi", imageTag);
				
				System.out.println("Removing Docker image '" + imageTag + "'...");

				Process p = pb.start();

				stdErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));

				errorMessage = null;
				s = null;

				while ((s = stdErr.readLine()) != null) {
					errorMessage = errorMessage + s;
				}

				if ((errorMessage != null) && (!errorMessage.isEmpty())) {
					if (stdErr != null) {
						stdErr.close();
					}
					p.waitFor();
					p.destroy();
					System.err.println("Error: " + errorMessage);
				}
				else {
					if (stdErr != null) {
						stdErr.close();
					}
					p.waitFor();
					p.destroy();
					System.out.println("Removed Docker image '" + imageTag + "'");
					return true;
				}
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

	//Stops a Docker container
	public static boolean stopDockerContainer(String imageTag) {
		try {
			ProcessBuilder pb = new ProcessBuilder();
			BufferedReader stdErr;
			String errorMessage = new String();
			String s = null;

			if (checkDockerContainer(imageTag)) {
				//Stop any running containers with the same
				//name as the image tag
				pb.command("docker", "container", "stop", imageTag);
				
				System.out.println("Stopping Docker container '" + imageTag + "'...");

				Process p = pb.start();

				stdErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));

				while ((s = stdErr.readLine()) != null) {
					errorMessage = errorMessage + s;
				}

				if ((errorMessage != null) && (!errorMessage.isEmpty())) {
					if (stdErr != null) {
						stdErr.close();
					}
					p.waitFor();
					p.destroy();
					System.err.println("Error: " + errorMessage);
				}
				else {
					if (stdErr != null) {
						stdErr.close();
					}
					p.waitFor();
					p.destroy();
					return true;
				}
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

	//Removes a Docker container
	public static boolean removeDockerContainer(String imageTag) {
		try {
			ProcessBuilder pb = new ProcessBuilder();
			BufferedReader stdErr;
			String errorMessage = new String();
			String s = null;

			if (checkDockerContainer(imageTag)) {
				//Remove any paused or stopped Docker containers
				//with the provided name ("imageTag")
				pb.command("docker", "container", "rm", imageTag);
				
				System.out.println("Removing Docker container '" + imageTag + "'...");

				Process p = pb.start();

				stdErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));

				while ((s = stdErr.readLine()) != null) {
					errorMessage = errorMessage + s;
				}

				if ((errorMessage != null) && (!errorMessage.isEmpty())) {
					if (stdErr != null) {
						stdErr.close();
					}
					p.waitFor();
					p.destroy();
					System.err.println("Error: " + errorMessage);
				}
				else {
					if (stdErr != null) {
						stdErr.close();
					}
					p.waitFor();
					p.destroy();
					return true;
				}
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

	//Sets up the group and user "appuser" for
	//a Docker container by writing the Linux commands
	//"useradd" and "groupadd" to the Dockerfile.
	//A randomly generated alphanumeric password is
	//used for "appuser", to limit privileged command
	//execution within the container (compiler and
	//source code executables are not privileged
	//and thus alone will never require a password
	//to execute).
	public boolean prepDockerfile(File f) {
		Random PRNG = new Random(System.currentTimeMillis());
		final int len = 16; //Password length
		int i;
		String password = new String();
		for (i = 0; i < len; i++) {
			//122 in ASCII is 'z'
			int n = PRNG.nextInt(123);
			
			//ASCII:
			//	91 = '[' , 92 = '\' , 93 = ']' , 94 = '^' ,
			// 95 = '_' , 96 = '`' ...all invalid password characters
			while ((n >= 91) && (n <= 96)) {
				n = PRNG.nextInt(123);
			}
			//65 in ASCII is 'A', so A-z
			if (n >= 65) {
				password = password + Character.toString((char)n);
			}
			else {
				password = password + Character.toString(Integer.toString(n).charAt(0));
			}
		}
		if (write(f, new String("RUN groupadd -g 999 appuser "
					+ "&& useradd -r -m -u 999 -g appuser -p "
					+ password + " appuser\n"
					+ "USER appuser"))) {
		 	return true;
		}

		return false;
	}

	//Builds a Docker image
	public boolean buildDockerImage(File f, String imageTag) {
		try {
			if (f.getParentFile().isDirectory()) {
				ProcessBuilder pb = new ProcessBuilder();
				pb.directory(f.getParentFile().getCanonicalFile());
				pb.command("docker", "build", "-t", imageTag, ".");
				
				
				
				
				
System.out.println("About to build the Docker image for file: '" + f.getPath() + "'");

				
				
				
				
				
				Process p = pb.start();
	
				
				
				
				
System.out.println("About to read output from the build process...");
				
				
				
				
				
System.out.println("I'm about to create the BufferedReader stdErr!");
				BufferedReader stdErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
System.out.println("I'm hung on BufferedReader!");
				String errorMessage = new String();
				String s = null;
System.out.println("I just declared String 'errorMessage'!");
				while ((s = stdErr.readLine()) != null) {
System.out.println("Error output for build process:" + s);
					errorMessage = errorMessage + s;
				}
	
				
				
				
				
System.out.println("Reading output from build process...");
				
				
				
				
				
				
				if ((errorMessage != null) && (!errorMessage.isEmpty())) {
					if (stdErr != null) {
						stdErr.close();
					}
					p.waitFor();
					p.destroy();
					System.err.println("Error: " + errorMessage);
				}
				else {
					if (stdErr != null) {
						stdErr.close();
					}
					p.waitFor();
					p.destroy();
					return true;
				}
			}
			else {
				System.err.println("Error: File '" + f.getParentFile().getCanonicalPath() + "' is not a directory");
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

	//Runs a Docker container
	public static boolean runDockerContainer(String imageTag) {
		try {
			ProcessBuilder pb = new ProcessBuilder();
			//"-d" switch means to run the container detached from STDIN/STDOUT, basically
			//running in the background. "--name=" assigns a name to the container, and the
			//Linux command "tail -f /dev/null" simply keeps the container up and running
			//rather than terminating once it is up, because by design containers terminate
			//when the root process in the container exits. Thus, "tail -f /dev/null" will run
			//forever and keep the container up and running until it is stopped.
			pb.command("docker", "run", "-d", new String("--name=" + imageTag), imageTag, "tail", "-f", "/dev/null");

			Process p = pb.start();

			BufferedReader stdErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			BufferedReader stdIn = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String errorMessage = new String();
			String processInput = new String();
			String s = null;

			while ((s = stdErr.readLine()) != null) {
				errorMessage = errorMessage + s;
			} 

			while ((s = stdIn.readLine()) != null) {
				processInput = processInput + s;
			}

			if ((errorMessage != null) && (!errorMessage.isEmpty())) {
				if (stdErr != null) {
					stdErr.close();
				}
				if (stdIn != null) {
					stdIn.close();
				}
				p.waitFor();
				p.destroy();
				System.err.println("Error: " + errorMessage);
			}

			else if ((processInput != null) && (!processInput.isEmpty())) {
				if (stdErr != null) {
					stdErr.close();
				}
				if (stdIn != null) {
					stdIn.close();
				}
				p.waitFor();
				p.destroy();
				return true;
			}

			else {
				if (stdErr != null) {
					stdErr.close();
				}
				if (stdIn != null) {
					stdIn.close();
				}
				p.waitFor();
				p.destroy();
				System.err.println("Error: Docker container process did not write to STDERR or STDOUT streams");
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

	//Starts up the containers "gcc-cypher", "openjdk-cypher"
	//and "python-cypher" from scratch. Old containers (if there
	//are any) are stopped and removed, old images are removed,
	//and entirely new images are built from which new containers
	//are started.
	public boolean startContainers(String ...conts) {
		boolean gcc = false;
		boolean openjdk = false;
		boolean python = false;

		for (String container : conts) {
			System.out.println();
			if (!removeDockerImage(container)) {
				System.out.println("Found no images or containers tagged as '" + container + "'");
			}
		}

		try {
			if (initialize()) {
				for (String container : conts) {
					//GCC Docker container
					System.out.println();
					if (container.contentEquals("gcc-cypher")) {
						if (write(gccDockerfile, "FROM gcc\n")) {
							if (prepDockerfile(gccDockerfile)) {
								System.out.println("Building Docker image '" + container + "'...");
								if (buildDockerImage(gccDockerfile, container)) {
									System.out.println("Built Docker image '" + container + "'");
									System.out.println("Starting Docker container '" + container + "'...");
									if (runDockerContainer(container)) {
										gcc = true;
										System.out.println("Started Docker container '" + container + "'...");
										delete(new File(new String(System.getProperty("user.home")
													+ System.getProperty("file.separator")
													+ "Cypher" + System.getProperty("file.separator")
													+ "gcc")));
									}
									else {
										System.err.println("!-->Error: Failed to run Docker container '" + container + "'");
									}
								}
								else {
									System.err.println("!-->Error: Failed to build Docker image '" + container + "'"
														+ " from Dockerfile " + gccDockerfile.getCanonicalPath());
								}
							}
							else {
								System.err.println("!-->Error: Failed to append to Dockerfile "
													+ gccDockerfile.getCanonicalPath());
							}
						}
						else {
							System.err.println("!-->Error: Failed to write to Dockerfile "
													+ gccDockerfile.getCanonicalPath());
						}
					}
					else if (container.contentEquals("openjdk-cypher")) {
						//OpenJDK Docker container
						if (write(openjdkDockerfile, "FROM openjdk\n")) {
							if (prepDockerfile(openjdkDockerfile)) {
								System.out.println("Building Docker image '" + container + "'...");
								if (buildDockerImage(openjdkDockerfile, container)) {
									System.out.println("Built Docker image '" + container + "'");
									System.out.println("Starting Docker container '" + container + "'...");
									if (runDockerContainer(container)) {
										openjdk = true;
										System.out.println("Started Docker container '" + container + "'...");
										delete(new File(new String(System.getProperty("user.home")
													+ System.getProperty("file.separator")
													+ "Cypher" + System.getProperty("file.separator")
													+ "openjdk")));
									}
									else {
										System.err.println("!-->Error: Failed to run Docker container '" + container + "'");
									}
								}
								else {
									System.err.println("!-->Error: Failed to build Docker image '" + container + "'"
														+ " from Dockerfile " + openjdkDockerfile.getCanonicalPath());
								}
							}
							else {
								System.err.println("!-->Error: Failed to append to Dockerfile "
													+ openjdkDockerfile.getCanonicalPath());
							}
						}
						else {
							System.err.println("!-->Error: Failed to write to Dockerfile "
													+ openjdkDockerfile.getCanonicalPath());
						}
					}
					else if (container.contentEquals("python-cypher")) {
						//Python Docker container
						if (write(pythonDockerfile, "FROM python\n")) {
							if (prepDockerfile(pythonDockerfile)) {
								System.out.println("Building Docker image '" + container + "'...");
								if (buildDockerImage(pythonDockerfile, container)) {
									System.out.println("Built Docker image '" + container + "'");
									System.out.println("Starting Docker container '" + container + "'...");
									if (runDockerContainer(container)) {
										python = true;
										System.out.println("Started Docker container '" + container + "'...");
										delete(new File(new String(System.getProperty("user.home")
															+ System.getProperty("file.separator")
															+ "Cypher" + System.getProperty("file.separator")
															+ "python")));
									}
									else {
										System.err.println("!-->Error: Failed to run Docker container '" + container + "'");
									}
								}
								else {
									System.err.println("!-->Error: Failed to build Docker image '" + container + "'"
														+ " from Dockerfile " + pythonDockerfile.getCanonicalPath());
								}
							}
							else {
								System.err.println("!-->Error: Failed to append to Dockerfile "
													+ pythonDockerfile.getCanonicalPath());
							}
						}
						else {
							System.err.println("!-->Error: Failed to write to Dockerfile "
													+ pythonDockerfile.getCanonicalPath());
						}
					}
					else {
						System.err.println("!-->Error: Failed to start '" + container + "' because the container name "
												+ "does not match any of the following permissible container names: "
												+ "'gcc-cypher', 'openjdk-cypher', 'python-cypher'");
					}
				}	
			}
			else {
				System.err.println("!-->Error: Failed to delete files: " + gccDockerfile.getCanonicalPath()
									+ ", " + openjdkDockerfile.getCanonicalPath() 
									+ ", " + pythonDockerfile.getCanonicalPath());
			}

			//Deleting directory "/home/$USER/Cypher"
			if (gcc && openjdk && python) {
				delete(new File(new String(System.getProperty("user.home") + System.getProperty("file.separator")
												+ "Cypher")));
			}
		}

		catch (IOException e) {
			System.err.println(e.toString());
			e.printStackTrace();
		}

		catch (Exception e) {
			System.err.println(e.toString());
			e.printStackTrace();
		}

		return (gcc && openjdk && python);
		//return (gcc || openjdk || python);
	}
	
	//Stops and removes Docker containers
	public static boolean stopContainers(String ...conts) {
		boolean gcc = false;
		boolean openjdk = false;
		boolean python = false;
		
		try {
			for (String container : conts) {
				if (checkDockerContainer(container)) {
					if (stopDockerContainer(container)) {
						System.out.println("Stopped Docker container '" + container + "'");
						if (removeDockerContainer(container)) {
							System.out.println("Removed container '" + container + "'");
							if (container.contentEquals("gcc-cypher")) {
								gcc = true;
							}
							else if (container.contentEquals("openjdk-cypher")) {
								openjdk = true;
							}
							else if (container.contentEquals("python-cypher")) {
								python = true;
							}
							else {
								continue;
							}
						}
						else {
							System.err.println("!-->Error: Failed to remove container '" + container + "'");
						}
					}
					else {
						System.err.println("!-->Error: Failed to stop container '" + container + "'");	
					}
				}
				else {
					System.err.println("!-->Error: Found no container matching the name '" + container + "'");
				}
			}
		}
		
		catch (Exception e) {
			System.err.println(e.toString());
			e.printStackTrace();
		}
	
		return (gcc && openjdk && python);
		//return (gcc || openjdk || python);
	}

	public static void main(String[] args) {
		DockerManager dock = new DockerManager();

		if (DockerManager.testDockerDaemon()) {
			if (dock.startContainers("gcc-cypher", "openjdk-cypher", "python-cypher")) {
				System.out.println("\nSuccessfully started all Docker containers!\n");
			}
			else {
				System.err.println("Error: Failed to start all Docker containers...");
			}
		}
		else {
			System.err.println("\nError: Docker is not running. Terminated execution.\n");
		}

		return;
	}

}










