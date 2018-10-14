package edu.sunypoly.cypher.frontend.domain;

public class TestRequest {

	private String applicationCode;
	private AssignmentTest[] assignmentTests;
	private String langauge;

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

	public String getLangauge() {
		return langauge;
	}

	public void setLangauge(String langauge) {
		this.langauge = langauge;
	}

}
