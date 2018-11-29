package edu.sunypoly.cypher.backend.service;

import org.cypher.commons.TestRequest;
import org.cypher.commons.TestResponse;

public interface TestExecutorService {

	TestResponse execute(TestRequest request);
}
