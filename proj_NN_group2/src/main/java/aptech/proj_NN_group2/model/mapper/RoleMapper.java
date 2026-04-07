package aptech.proj_NN_group2.model.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import aptech.proj_NN_group2.model.IMapper;
import aptech.proj_NN_group2.model.entity.Role;

public class RoleMapper implements IMapper<Role> {

	@Override
	public Role RowMap(ResultSet rs) throws SQLException {
		Role r = new Role();
		
		r.setRole_id(rs.getInt("role_id"));
		r.setRole_name(rs.getString("role_name"));
		
		return r;
	}
	
}
