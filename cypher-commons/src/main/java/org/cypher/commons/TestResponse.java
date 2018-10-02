package org.cypher.commons;

public class TestResponse {

	private Long compilationStatus;
	private String compilationDetails;

	private AssignmentTestResult[] testResults;

	public Long getCompilationStatus() {
		return compilationStatus;
	}

	public void setCompilationStatus(Long compilationStatus) {
		this.compilationStatus = compilationStatus;
	}

	public String getCompilationDetails() {
		return compilationDetails;
	}

	public void setCompilationDetails(String compilationDetails) {
		this.compilationDetails = compilationDetails;
	}

	public AssignmentTestResult[] getTestResults() {
		return testResults;
	}

	public void setTestResults(AssignmentTestResult[] testResults) {
		this.testResults = testResults;
	}

}
