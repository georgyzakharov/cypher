package org.cypher.commons;

import java.util.Arrays;

public class TestRequest {

	private String applicationCode;
	private AssignmentTest[] assignmentTests;
	private String language;

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

	@Override
	public String toString() {
		return "TestRequest [applicationCode=" + applicationCode + ", assignmentTests="
				+ Arrays.toString(assignmentTests) + ", language=" + language + "]";
	}

}
