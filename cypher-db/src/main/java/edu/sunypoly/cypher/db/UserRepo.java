package edu.sunypoly.cypher.db;

import java.util.List;

import org.cypher.commons.User;

public interface UserRepo {

	List<User> getUserList();

	void createUser(User user);

}
