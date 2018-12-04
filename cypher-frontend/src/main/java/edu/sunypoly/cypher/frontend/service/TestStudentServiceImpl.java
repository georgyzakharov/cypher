package edu.sunypoly.cypher.frontend.service;

import java.util.List;

import javax.annotation.Resource;

import org.cypher.commons.Student;
import org.springframework.stereotype.Service;
import edu.sunypoly.cypher.db.UserRepository;

@Service
public class TestStudentServiceImpl implements TestStudentService {

	@Resource private UserRepository userRepository; 
	
	@Override
	public List<Student> getStudentList() {

		return userRepository.getStudentList();
	}

}
