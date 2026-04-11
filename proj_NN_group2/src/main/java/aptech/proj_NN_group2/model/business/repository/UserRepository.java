package aptech.proj_NN_group2.model.business.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import aptech.proj_NN_group2.model.IFind;
import aptech.proj_NN_group2.model.business.BaseRepository;
import aptech.proj_NN_group2.model.entity.User;
import aptech.proj_NN_group2.model.mapper.UserMapper;
import aptech.proj_NN_group2.util.Database;

public class UserRepository extends BaseRepository<User> implements IFind<User> {
	private final UserMapper mapper = new UserMapper();

//    public User login(String username) {
//        String sql = "SELECT u.*, r.role_name FROM users u JOIN roles r ON u.role_id = r.role_id WHERE u.username = ? AND u.is_active = 1";
//        return findOne(sql, ps -> ps.setString(1, username));
//    }

	public User login(String username) {
		// Bỏ u.is_active = 1 để có thể lấy được cả User đang bị khóa
		String sql = "SELECT u.*, r.role_name FROM users u " + "JOIN roles r ON u.role_id = r.role_id "
				+ "WHERE u.username = ?";

		return findOne(sql, ps -> {
			try {
				ps.setString(1, username);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
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
		User u = mapper.RowMap(rs);
		// Cắt bỏ khoảng trắng dư thừa từ SQL (đặc biệt nếu dùng kiểu CHAR/NCHAR)
		if (u.getPasswordHash() != null) {
			u.setPasswordHash(u.getPasswordHash().trim());
		}
		try {
			// Phải lấy thêm cột này vì nó được JOIN từ bảng roles
			u.setRoleName(rs.getString("role_name"));
		} catch (SQLException e) {
			// Bỏ qua nếu query không có JOIN roles
		}
		return u;
	}

	// 1. Hàm tạo tài khoản mới
	public boolean create(User user) {
		String sql = "INSERT INTO users (username, password_hash, role_id, is_active) VALUES (?, ?, ?, 1)";
		try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, user.getUsername());
			ps.setString(2, user.getPasswordHash()); // Lưu ý: Nên dùng BCrypt.hashpw ở đây
			ps.setInt(3, user.getRoleId());
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// 2. Hàm Khóa/Mở khóa tài khoản
	public boolean toggleActive(int userId, boolean newStatus) {
		String sql = "UPDATE users SET is_active = ? WHERE user_id = ?";
		try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, newStatus ? 1 : 0);
			ps.setInt(2, userId);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
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

	// 4. Hàm reset mật khẩu (Dùng khi nhân viên quên mật khẩu)
	public boolean updatePassword(int userId, String newPasswordHash) {
		String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";
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

//package aptech.proj_NN_group2.model.business.repository;
//
//import java.util.List;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//import aptech.proj_NN_group2.model.IFind;
//import aptech.proj_NN_group2.model.business.BaseRepository;
//import aptech.proj_NN_group2.model.entity.User;
//import aptech.proj_NN_group2.model.mapper.UserMapper;
//
//public class UserRepository extends BaseRepository<User> implements IFind<User>{
//	private final UserMapper mapper = new UserMapper();
//	
//	@Override
//	public User findById(int id) {
//		return findOne(
//			"SELECT * FROM users WHERE user_id = ?", 
//			ps -> ps.setInt(1, id)
//		);
//	}
//	
//	@Override
//	public List<User> findAll() {
//		return find("SELECT * FROM users", null);
//	}
//
//	@Override
//	protected User map(ResultSet rs) throws SQLException {
//		return mapper.RowMap(rs);
//	}
//	
//}
