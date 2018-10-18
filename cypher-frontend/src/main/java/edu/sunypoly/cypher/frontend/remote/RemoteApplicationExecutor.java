package edu.sunypoly.cypher.frontend.remote;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.cypher.commons.TestRequest;
import org.cypher.commons.TestResponse;

@Path("/tests")
public interface RemoteApplicationExecutor {
	
	//This method will specify in what kind of format the applications will communicate with each other 
	@POST
	//showing the path
	@Path("/execute")
	@Consumes("application/json")
	@Produces("application/json")
	TestResponse execute(TestRequest request);
	
}

