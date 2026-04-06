package aptech.proj_NN_group2.model.business.repository;

import java.util.List;
import java.sql.ResultSet;
import java.sql.SQLException;

import aptech.proj_NN_group2.model.IFind;
import aptech.proj_NN_group2.model.business.BaseRepository;
import aptech.proj_NN_group2.model.entity.User;
import aptech.proj_NN_group2.model.mapper.UserMapper;

public class UserRepository extends BaseRepository<User> implements IFind<User>{
	private final UserMapper mapper = new UserMapper();
	
	@Override
	protected User map(ResultSet rs) throws SQLException {
		return mapper.RowMap(rs);
	}

	@Override
	public User findById(int id) {
		return findOne(
			"SELECT * FROM users WHERE user_id = ?", 
			ps -> ps.setInt(1, id)
		);
	}
	
	@Override
	public List<User> findAll() {
		return find("SELECT * FROM users", null);
	}
}