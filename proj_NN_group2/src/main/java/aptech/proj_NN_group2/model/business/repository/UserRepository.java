package aptech.proj_NN_group2.model.business.repository;

import aptech.proj_NN_group2.model.entity.User;
import aptech.proj_NN_group2.model.business.BaseRepository;
import aptech.proj_NN_group2.model.IFind;
import aptech.proj_NN_group2.model.mapper.UserMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserRepository extends BaseRepository<User> implements IFind<User> {
    private final UserMapper mapper = new UserMapper();

    public User login(String username) {
        String sql = "SELECT u.*, r.role_name FROM users u JOIN roles r ON u.role_id = r.role_id WHERE u.username = ? AND u.is_active = 1";
        return findOne(sql, ps -> ps.setString(1, username));
    }

    @Override
    public User findById(int id) {
        return findOne("SELECT * FROM users WHERE user_id = ?", ps -> ps.setInt(1, id));
    }

    @Override
    public List<User> findAll() {
        return find("SELECT u.*, r.role_name FROM users u JOIN roles r ON u.role_id = r.role_id", null);
    }

    public boolean toggleActive(int userId, boolean status) {
        return executeUpdate("UPDATE users SET is_active = ? WHERE user_id = ?", ps -> {
            ps.setBoolean(1, status);
            ps.setInt(2, userId);
        });
    }

    @Override
    protected User map(ResultSet rs) throws SQLException {
        User u = mapper.RowMap(rs);
        try {
            u.setRoleName(rs.getString("role_name"));
        } catch (SQLException e) {
            // Trường hợp Query không có cột role_name
        }
        return u;
    }
}