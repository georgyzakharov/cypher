package edu.sunypoly.cypher.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.cypher.commons.CypherUser;
import org.cypher.commons.Student;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryJdbcImpl implements UserRepository {

	@Resource
	JdbcTemplate jdbcTemplate;

	@Override
	public List<Student> getStudentList() {
		List<Student> studentList = jdbcTemplate.query("select * from student", new RowMapper<Student>() {

			@Override
			public Student mapRow(ResultSet rs, int rowNum) throws SQLException {
				Student s = new Student();

				s.setStudentNumber(rs.getInt("student_number"));
				s.setName(rs.getString("name"));
				s.setClazz(rs.getInt("clazz"));
				s.setMajor(rs.getString("major"));

				return s;
			}
		});

		return studentList;
	}

	@Override
	public List<CypherUser> getCypherUserList() {
		List<CypherUser> userList = jdbcTemplate.query("select * from users", new RowMapper<CypherUser>() {

			@Override
			public CypherUser mapRow(ResultSet rs, int rowNum) throws SQLException {
				CypherUser u = new CypherUser();

				u.setName(rs.getString("name"));
				u.setUsername(rs.getString("username"));
				u.setUserId(rs.getLong("userid"));
				u.setPassword(rs.getString("passwd"));

				return u;
			}
		});

		return userList;
	}

	@Override
	public void createUser(CypherUser user) {
		jdbcTemplate.update("insert into users (userid, name, username, passwd, type_role) values (?,?,?,?,?)", 
				user.getUserId(),
				user.getName(),
				user.getUsername(),
				user.getPassword(),
				user.getType());
		
	}
	
	
	

}
