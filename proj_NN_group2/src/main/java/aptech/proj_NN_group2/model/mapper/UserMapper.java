package aptech.proj_NN_group2.model.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import aptech.proj_NN_group2.model.entity.User;
import aptech.proj_NN_group2.model.interfaces.IMapper;

public class UserMapper implements IMapper<User> {
    @Override
    public User RowMap(ResultSet rs) throws SQLException {
        User u = new User();
        
        u.setUserId(rs.getInt("user_id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setRoleId(rs.getInt("role_id"));
        u.setActive(rs.getBoolean("is_active"));
        u.setRoleName(rs.getString("role_name")); 
        u.setEmail(rs.getString("email"));
        u.setMustChangePassword(rs.getBoolean("must_change_password"));
        return u;
    }
}