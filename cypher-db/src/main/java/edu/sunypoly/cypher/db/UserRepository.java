package edu.sunypoly.cypher.db;

import java.util.List;

import org.cypher.commons.CypherUser;
import org.cypher.commons.Student;

public interface UserRepository {

	List<Student> getStudentList();
	
	List<CypherUser> getCypherUserList();

	void createUser(CypherUser user);
	
	
}
