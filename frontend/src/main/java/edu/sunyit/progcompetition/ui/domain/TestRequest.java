package edu.sunyit.progcompetition.ui.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class TestRequest {

	private String applicationCode;
	private AssignmentTest[] assignmentTests;

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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
