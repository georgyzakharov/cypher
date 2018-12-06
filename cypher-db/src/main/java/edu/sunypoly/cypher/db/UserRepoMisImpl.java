package edu.sunypoly.cypher.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.cypher.commons.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class UserRepoMisImpl implements UserRepo {

	@Resource
	JdbcTemplate jdbcTemplate;

	@Override
	public List<User> getUserList() {
		List<User> userList = jdbcTemplate.query("SELECT * FROM user", new RowMapper<User>() {

			@Override
			public User mapRow(ResultSet rs, int rowNum) throws SQLException {
				User u = new User();

				u.setUname(rs.getString("name"));
				u.setType(rs.getString("type"));

				return u;
			}
		});

		return userList;
	}

	@Override
	public void createUser(User user) {
		// TODO Auto-generated method stub

	}

}
