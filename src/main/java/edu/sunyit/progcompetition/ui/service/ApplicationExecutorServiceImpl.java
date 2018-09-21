package edu.sunyit.progcompetition.ui.service;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import edu.sunyit.progcompetition.ui.domain.AssignmentTest;
import edu.sunyit.progcompetition.ui.domain.TestRequest;
import edu.sunyit.progcompetition.ui.domain.TestResponse;
import edu.sunyit.progcompetition.ui.remote.RemoteApplicationExecutor;
import si.mazi.rescu.RestProxyFactory;

//matches with the request for injection
@Service
public class ApplicationExecutorServiceImpl implements ApplicationExecutorService {
	
	//8081 is the port that my UI application will talk to 
	private String uri = "http://localhost:8081";
	
	private RemoteApplicationExecutor remoteExecutor;
	
	//init (initializing) will create a connection with executor project
	@PostConstruct
	public void init() {
		remoteExecutor = RestProxyFactory.createProxy(RemoteApplicationExecutor.class, uri);
	}
	
	
	//
	@Override
	public String getResult(String applicationCode, String input) {
		TestRequest request = new TestRequest();
		request.setApplicationCode(applicationCode);
		AssignmentTest test = new AssignmentTest();
		test.setTestId(0L);
		test.setInput(input);
		test.setExpectedOutput("");
		
		request.setAssignmentTests(new AssignmentTest[] {test});
		TestResponse testResponse = remoteExecutor.execute(request);
		
		return testResponse.getTestResults()[0].getTestOutput();
	}
	
	

}
