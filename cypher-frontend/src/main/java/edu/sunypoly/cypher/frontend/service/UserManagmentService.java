package edu.sunypoly.cypher.frontend.service;

import java.util.List;

import org.cypher.commons.CypherUser;

public interface UserManagmentService {
	List<CypherUser> getUserList();

	void createUser(Long userid, String name, String userName, String password, String role);

}
