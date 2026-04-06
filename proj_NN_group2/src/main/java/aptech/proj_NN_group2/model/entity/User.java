package aptech.proj_NN_group2.model.entity;

import java.sql.Date;

public class User {
	private int user_id;
	private String username;
	private String password_hash;
	private int role_id;
	private boolean is_active;
	private Date created_at;
	
	public User() {
		super();
	}

	public User(int user_id, String username, String password_hash, int role_id, boolean is_active,
			Date created_at) {
		super();
		this.user_id = user_id;
		this.username = username;
		this.password_hash = password_hash;
		this.role_id = role_id;
		this.is_active = is_active;
		this.created_at = created_at;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword_hash() {
		return password_hash;
	}

	public void setPassword_hash(String password_hash) {
		this.password_hash = password_hash;
	}

	public int getRole_id() {
		return role_id;
	}

	public void setRole_id(int role_id) {
		this.role_id = role_id;
	}

	public boolean isIs_active() {
		return is_active;
	}

	public void setIs_active(boolean is_active) {
		this.is_active = is_active;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	@Override
	public String toString() {
		return "User [user_id=" + user_id + ", username=" + username + ", password_hash=" + password_hash + ", role_id="
				+ role_id + ", is_active=" + is_active + ", created_at=" + created_at + "]";
	}
}