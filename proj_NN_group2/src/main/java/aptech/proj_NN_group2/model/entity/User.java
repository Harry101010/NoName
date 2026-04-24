package aptech.proj_NN_group2.model.entity;

import java.sql.Date;

public class User {
    private int userId;
    private String username;
    private String passwordHash;
    private int roleId;
    private String roleName; // Dùng để hiển thị tên quyền thay vì ID
    private boolean isActive;
    private String email;
    private Date created_at;
    private boolean mustChangePassword;
    
    public User() {}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	
	public boolean isMustChangePassword() {
	    return mustChangePassword;
	}

	public void setMustChangePassword(boolean mustChangePassword) {
	    this.mustChangePassword = mustChangePassword;
	}

}