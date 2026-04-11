package aptech.proj_NN_group2.model.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import aptech.proj_NN_group2.model.IMapper;
import aptech.proj_NN_group2.model.entity.User;

public class UserMapper implements IMapper<User> {

    @Override
    public User RowMap(ResultSet rs) throws SQLException {
        User u = new User();
        
        // Khớp với các cột trong Database và Method trong User.java
        u.setUserId(rs.getInt("user_id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setRoleId(rs.getInt("role_id"));
        u.setActive(rs.getBoolean("is_active"));
        // Nếu lớp User chưa có created_at, có thể tạm bỏ qua dòng dưới
        // u.setRoleName(rs.getString("role_name")); 
        u.setEmail(rs.getString("email"));
        return u;
    }

}