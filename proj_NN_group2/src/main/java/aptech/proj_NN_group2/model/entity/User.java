package aptech.proj_NN_group2.model.entity;

public class User {
    private int userId;
    private String username;
    private String passwordHash;
    private int roleId;
    private String roleName; // Dùng để hiển thị tên quyền thay vì ID
    private boolean isActive;
    private String email;

    // Constructors
    public User() {}

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public int getRoleId() { return roleId; }
    public void setRoleId(int roleId) { this.roleId = roleId; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public String getEmail() { return email; }
    public void setEmail(String email) {this.email = email;}
}