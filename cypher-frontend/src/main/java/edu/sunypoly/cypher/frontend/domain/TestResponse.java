package edu.sunypoly.cypher.frontend.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
