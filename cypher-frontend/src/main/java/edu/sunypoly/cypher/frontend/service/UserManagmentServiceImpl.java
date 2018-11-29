package edu.sunypoly.cypher.frontend.service;

import java.util.List;

import javax.annotation.Resource;

import org.cypher.commons.CypherUser;
import org.springframework.stereotype.Service;

import edu.sunypoly.cypher.db.UserRepository;

@Service
public class UserManagmentServiceImpl implements UserManagmentService {

	@Resource
	private UserRepository userRepository;

	@Override
	public List<CypherUser> getUserList() {
		return userRepository.getCypherUserList();

	}

	@Override
	public void createUser(Long userId, String name, String userName, String password, String type) {
		
		CypherUser user = new CypherUser ();
		user.setUserId(userId);
		user.setName(name);
		user.setUsername(userName);
		user.setPassword(password);
		user.setType(type);
		
		userRepository.createUser(user);
	}

}
