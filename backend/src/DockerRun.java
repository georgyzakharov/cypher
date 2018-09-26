import java.io.*;

public final class DockerRun {

	private static String imageTag;
	private static String errors;
	private static ProcessBuilder pb;

	private DockerRun() {
	}

	public static String getExtension (String s) {
		s = s.substring(s.lastIndexOf(File.separator), s.length());
		return s.substring(s.lastIndexOf("."), s.length());
	}

	public static boolean writeDockerfile(String source) {
		try {
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

	public static String runDocker(String source) {
		if (buildDockerImage(source)) {
			return runDockerContainer();
		}
		else {
			return errors;
		}
	}

	public static boolean buildDockerImage(String source) {
		try {
			imageTag = new String(source.substring(source.lastIndexOf(File.separator) + 1, source.length()).toLowerCase());
			imageTag = imageTag.substring(0, imageTag.lastIndexOf(".")) + "-" + imageTag.substring(imageTag.lastIndexOf(".") + 1, imageTag.length());
			pb = new ProcessBuilder();
			//pb.redirectErrorStream(true);

			pb.command("docker", "build", "-t", imageTag, ".");
			pb.directory(new File(source.substring(0, source.lastIndexOf(File.separator))));

			Process p = pb.start();

			//BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			String s = null;
			errors = new String();
			while ((s = stdError.readLine()) != null) {
				errors = errors + s;
			}

			//PROBLEM: 'if (errors != null || !errors.isEmpty())'
			if (errors != null && !errors.isEmpty()) {
				p.waitFor();
				p.destroy();
				//return errors;
				return false;
			}
			else {
				p.waitFor();
				p.destroy();
				//return runDockerContainer(pb, imageTag);
				return true;
			}

		}

		catch (IOException e) {
			errors = new String("<IOException> thrown in 'buildDockerImage()'!");
			e.printStackTrace();
			//return "Error!";
			return false;
		}

		catch (Exception e) {
			errors = new String("<Exception> thrown in 'buildDockerImage()'!");
			e.printStackTrace();
			//return "Error!";
			return false;
		}
	}

	//Private because this method should only be called after a Docker
	//image has been built in 'buildDockerImage()'
	private static String runDockerContainer() {
		try {
			//pb.command("/bin/sh", "-c", "docker", "run", "--rm", imageTage);
			pb.command("docker", "run", "--rm", imageTag);

			Process p = pb.start();

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			String s = null;
			String results = new String();
			errors = new String();

			while ((s = stdInput.readLine()) != null) {
				results = results + s;
			}

			while ((s = stdError.readLine()) != null) {
				errors = errors + s;
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
			else {
				p.waitFor();
				p.destroy();
				return "Docker container process did not produce errors or standard output.";
			}
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

	public static String compExec(String s) {

		String source = new String(s);

		if (writeDockerfile(source)) {
			return runDocker(source);
		}
		else {
			return "Failed to write Dockerfile. Check source code filename.";
		}
	}

	public static void main(String[] args) {

		System.out.println(DockerRun.compExec(args[args.length-1]));

		return;
	}
}

