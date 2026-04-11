package aptech.proj_NN_group2.model.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import aptech.proj_NN_group2.model.entity.IceCream;
import aptech.proj_NN_group2.model.interfaces.IMapper;

public class IceCreamMapper implements IMapper<IceCream> {
	@Override
    public IceCream RowMap(ResultSet rs) throws SQLException {
        IceCream i = new IceCream();
        i.setIce_cream_id(rs.getInt("ice_cream_id"));
        i.setIce_cream_name(rs.getString("ice_cream_name"));
        i.setIs_active(rs.getBoolean("is_active"));
        return i;
    }
}