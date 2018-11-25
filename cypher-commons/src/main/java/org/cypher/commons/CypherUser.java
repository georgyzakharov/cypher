package org.cypher.commons;

public class CypherUser {

	private Long userId;
	private String name;
	private String username;
	private String password;
	private String type;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "CypherUser [userId=" + userId + ", name=" + name + ", username=" + username + ", password=" + password
				+ ", type=" + type + "]";
	}
}
