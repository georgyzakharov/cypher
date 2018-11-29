package org.cypher.commons;

public class TestRequest {
	private int programNumber;
	private String teamId;
	private String Fname;
	private String applicationCode;
	private AssignmentTest[] assignmentTests;
	private String language;

	public int getProgramNumber() {
		return programNumber;
	}

	public void setProgramNumber(int programNumber) {
		this.programNumber = programNumber;
	}

	public String getTeamId() {
		return teamId;
	}

	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}

	public String getFname() {
		return Fname;
	}

	public void setFname(String fname) {
		Fname = fname;
	}

	public String getApplicationCode() {
		return applicationCode;
	}

	public void setApplicationCode(String applicationCode) {
		this.applicationCode = applicationCode;
	}

	public AssignmentTest[] getAssignmentTests() {
		return assignmentTests;
	}

	public void setAssignmentTests(AssignmentTest[] assignmentTests) {
		this.assignmentTests = assignmentTests;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

}
