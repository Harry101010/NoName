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
	private final UserMapper mapper = new UserMapper();

	public User login(String username) {
		String sql = "SELECT u.*, r.role_name FROM users u " + "JOIN roles r ON u.role_id = r.role_id "
				+ "WHERE u.username = ?";
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

	@Override
	protected User map(ResultSet rs) throws SQLException {
		return mapper.RowMap(rs);
	}

	// 1. Hàm tạo tài khoản mới
	public boolean create(User user) {
		String sql = "INSERT INTO users (username, password_hash, role_id, is_active) VALUES (?, ?, ?, 1)";
		return executeUpdate(sql, ps -> {
			ps.setString(1, user.getUsername());
			ps.setString(2, user.getPasswordHash()); // Lưu ý: Nên dùng BCrypt.hashpw ở đây
			ps.setInt(3, user.getRoleId());
		});
	}

	// 2. Hàm Khóa/Mở khóa tài khoản
	public boolean toggleActive(int userId, boolean newStatus) {
		String sql = "UPDATE users SET is_active = ? WHERE user_id = ?";
		return executeUpdate(sql, ps -> {
			ps.setInt(1, newStatus ? 1 : 0);
			ps.setInt(2, userId);
		});
	}

	public boolean delete(int userId) {
		String sql = "DELETE FROM users WHERE user_id = ?";
		return executeUpdate(sql, ps -> ps.setInt(1, userId));
	}

	// 3. Hàm cập nhật thông tin cơ bản (Tên và Quyền)
	public boolean update(User user) {
		String sql = "UPDATE users SET username = ?, role_id = ? WHERE user_id = ?";
		return executeUpdate(sql, ps -> {
			ps.setString(1, user.getUsername());
			ps.setInt(2, user.getRoleId());
			ps.setInt(3, user.getUserId());
		});
	}

	// 4.1 Hàm reset mật khẩu (Dùng khi nhân viên quên mật khẩu)
	public boolean updatePassword(int userId, String newPasswordHash) {
		String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";
		return executeUpdate(sql, ps -> {
			ps.setString(1, newPasswordHash);
			ps.setInt(2, userId);
		});
	}
	
	//4.2 Hàm update mk tạm và ép đổi mk
	public boolean updatePasswordAndSetMustChange(int userId, String newPasswordHash) {
		String sql = "UPDATE users SET password_hash = ?, must_change_password = 1 WHERE user_id = ?";
		return executeUpdate(sql, ps -> {
			ps.setString(1, newPasswordHash);
			ps.setInt(2, userId);
		});
	}
	//4.3 Đổi lại mk sau khi dùng tính năng quên mk
	public boolean updatePasswordAndClearMustChange(int userId, String newPasswordHash) {
	    String sql = "UPDATE users SET password_hash = ?, must_change_password = 0 WHERE user_id = ?";
	    return executeUpdate(sql, ps -> {
	        ps.setString(1, newPasswordHash);
	        ps.setInt(2, userId);
	    });
	}

	// 5. Hàm quên mật khẩu
	public User findByEmailOrUsername(String input) {
		String sql = "SELECT u.*, r.role_name FROM users u " + "JOIN roles r ON u.role_id = r.role_id "
				+ "WHERE u.username = ? OR u.email = ?";
		return findOne(sql, ps -> {
			ps.setString(1, input);
			ps.setString(2, input);
		});
	}
}