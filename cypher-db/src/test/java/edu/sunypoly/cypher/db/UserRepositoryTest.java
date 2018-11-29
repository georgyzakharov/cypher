package edu.sunypoly.cypher.db;

import static org.junit.Assert.assertNotNull;

import javax.annotation.Resource;

import org.cypher.commons.Student;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@JdbcTest
@AutoConfigureTestDatabase(replace=Replace.NONE)
public class UserRepositoryTest {

	@Value("${spring.datasource.url}")  private String databaseUrl;
	@Resource UserRepository userRepository;
	
	@Test
	public void test() {
		assertNotNull(userRepository);
	}
	
	@Test
	public void test3() {
		for (Student s : userRepository.getStudentList()) {
			System.out.println(s);
		}
	}

	@Test
	public void testUrl() {
		System.out.println(databaseUrl);
	}
	
	@Configuration
	@ComponentScan(basePackages="edu.sunypoly.cypher")
	public static class SpringConfig {

	}
}
