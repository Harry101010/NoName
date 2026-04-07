package aptech.proj_NN_group2.model.business.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import aptech.proj_NN_group2.model.IFind;
import aptech.proj_NN_group2.model.business.BaseRepository;
import aptech.proj_NN_group2.model.entity.Role;
import aptech.proj_NN_group2.model.mapper.RoleMapper;

public class RoleRepository extends BaseRepository<Role> implements IFind<Role> {
	private final RoleMapper mapper = new RoleMapper();
	
	@Override
	public Role findById(int id) {
		return findOne(
			"SELECT * FROM roles WHERE role_id = ?",
			ps -> ps.setInt(1, id)
		);
	}

	@Override
	public List<Role> findAll() {
		return find("SELECT * FROM roles", null);
	}

	@Override
	protected Role map(ResultSet rs) throws SQLException {
		return mapper.RowMap(rs);
	}

}
