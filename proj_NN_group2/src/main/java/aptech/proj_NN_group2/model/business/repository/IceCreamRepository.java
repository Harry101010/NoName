package aptech.proj_NN_group2.model.business.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import aptech.proj_NN_group2.model.business.BaseRepository;
import aptech.proj_NN_group2.model.entity.IceCream;

public class IceCreamRepository extends BaseRepository<IceCream> {

    @Override
    protected IceCream map(ResultSet rs) throws SQLException {
        IceCream ic = new IceCream();
        ic.setIce_cream_id(rs.getInt("ice_cream_id"));
        ic.setIce_cream_name(rs.getString("ice_cream_name"));
        ic.setIs_active(rs.getBoolean("is_active"));
        return ic;
    }

    public List<IceCream> findAllActive() {
        return find("SELECT * FROM ice_creams WHERE is_active = 1 ORDER BY ice_cream_name", null);
    }
}
