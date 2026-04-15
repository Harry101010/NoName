package aptech.proj_NN_group2.model.business.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import aptech.proj_NN_group2.model.business.BaseRepository;
import aptech.proj_NN_group2.model.entity.User;
import aptech.proj_NN_group2.model.interfaces.IDelete;
import aptech.proj_NN_group2.model.interfaces.IFind;
import aptech.proj_NN_group2.model.interfaces.IUpdate;
import aptech.proj_NN_group2.model.mapper.UserMapper;

public class UserRepository extends BaseRepository<User>
        implements IFind<User>, IDelete<User>, IUpdate<User> {

    private static final String BASE_SELECT = """
        SELECT u.*, r.role_name
        FROM users u
        JOIN roles r ON u.role_id = r.role_id
        """;

    private final UserMapper mapper = new UserMapper();

    public User login(String username) {
        String sql = BASE_SELECT + " WHERE u.username = ?";
        return findOne(sql, ps -> ps.setString(1, username));
    }

    @Override
    public User findById(int id) {
        String sql = BASE_SELECT + " WHERE u.user_id = ?";
        return findOne(sql, ps -> ps.setInt(1, id));
    }

    @Override
    public List<User> findAll() {
        return find(BASE_SELECT + " ORDER BY u.username", null);
    }

    @Override
    protected User map(ResultSet rs) throws SQLException {
        return mapper.RowMap(rs);
    }

    public boolean create(User user) {
        String sql = "INSERT INTO users (username, password_hash, role_id, is_active) VALUES (?, ?, ?, 1)";
        return executeUpdate(sql, ps -> {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setInt(3, user.getRoleId());
        });
    }

    public boolean toggleActive(int userId, boolean newStatus) {
        String sql = "UPDATE users SET is_active = ? WHERE user_id = ?";
        return executeUpdate(sql, ps -> {
            ps.setInt(1, newStatus ? 1 : 0);
            ps.setInt(2, userId);
        });
    }

    @Override
    public boolean delete(int userId) {
        return executeUpdate("DELETE FROM users WHERE user_id = ?", ps -> ps.setInt(1, userId));
    }

    @Override
    public boolean update(User user) {
        String sql = "UPDATE users SET username = ?, role_id = ? WHERE user_id = ?";
        return executeUpdate(sql, ps -> {
            ps.setString(1, user.getUsername());
            ps.setInt(2, user.getRoleId());
            ps.setInt(3, user.getUserId());
        });
    }

    public boolean updatePassword(int userId, String newPasswordHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";
        return executeUpdate(sql, ps -> {
            ps.setString(1, newPasswordHash);
            ps.setInt(2, userId);
        });
    }

    public User findByEmailOrUsername(String input) {
        String sql = BASE_SELECT + " WHERE u.username = ? OR u.email = ?";
        return findOne(sql, ps -> {
            ps.setString(1, input);
            ps.setString(2, input);
        });
    }
}