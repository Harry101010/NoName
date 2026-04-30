package aptech.proj_NN_group2.model.business.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import aptech.proj_NN_group2.model.business.BaseRepository;
import aptech.proj_NN_group2.model.entity.IceCream;
import aptech.proj_NN_group2.model.interfaces.ICreate;
import aptech.proj_NN_group2.model.interfaces.IDelete;
import aptech.proj_NN_group2.model.interfaces.IFind;
import aptech.proj_NN_group2.model.interfaces.IUpdate;
import aptech.proj_NN_group2.model.mapper.IceCreamMapper;

public class IceCreamRepository extends BaseRepository<IceCream> 
	implements IFind<IceCream>, ICreate<IceCream>, IUpdate<IceCream>, IDelete<IceCream> {
	
	private final IceCreamMapper mapper = new IceCreamMapper();

    @Override
    public IceCream findById(int id) {
        return findOne("SELECT * FROM ice_creams WHERE ice_cream_id = ?", ps -> ps.setInt(1, id));
    }

   

    public boolean create(IceCream i) {
        // Kiểm tra an toàn trước khi gửi xuống DB
        if (i.getIce_cream_name() == null || i.getIce_cream_name().trim().isEmpty()) {
            System.err.println("LỖI: Tên sản phẩm không được để trống.");
            return false;
        }

        return executeUpdate(
            "INSERT INTO ice_creams (ice_cream_name, is_active) VALUES (?, ?)",
            ps -> {
                ps.setString(1, i.getIce_cream_name().trim()); // Trim để tránh lưu khoảng trắng
                ps.setBoolean(2, i.getIs_active());           // Lưu trạng thái Active
            }
        );
    }

    public List<IceCream> findAllActive() {
    	return find("SELECT * FROM ice_creams WHERE is_active = 1 ORDER BY ice_cream_name", null);
    }

//    public boolean update(IceCream i) {
//        String sql = """
//                UPDATE ice_creams
//                SET ice_cream_name = ?, is_active = ?
//                WHERE ice_cream_id = ?
//        """;
//
//    	return executeUpdate(
//			sql,
//			ps -> {
//				ps.setString(1, i.getIce_cream_name());
//	            ps.setBoolean(2, i.getIs_active());
//	            ps.setInt(3, i.getIce_cream_id());
//			}
//    	);
//    }

    public boolean delete(int id) {
        return executeUpdate("DELETE FROM ice_creams WHERE ice_cream_id = ?", ps -> ps.setInt(1, id));
    }

    @Override
    protected IceCream map(ResultSet rs) throws SQLException {
        return mapper.RowMap(rs);
    }
//    public boolean update(IceCream ic) {
//        // Câu lệnh SQL phải có WHERE ice_cream_id để biết sửa dòng nào
//    	String sql = "UPDATE ice_creams SET ice_cream_name = ?, is_active = ? WHERE ice_cream_id = ?";
//        return executeUpdate(sql, ps -> {
//            ps.setString(1, ic.getIce_cream_name());
//            ps.setInt(2, ic.getIce_cream_id()); // Bắt buộc phải có ID
//            
//            
//        });
//    }
    public boolean update(IceCream i) {
        // Kiểm tra an toàn
        if (i.getIce_cream_id() <= 0) return false;

        return executeUpdate(
            "UPDATE ice_creams SET ice_cream_name = ?, is_active = ? WHERE ice_cream_id = ?",
            ps -> {
                ps.setString(1, i.getIce_cream_name().trim());
                ps.setBoolean(2, i.getIs_active()); // PHẢI CÓ DÒNG NÀY ĐỂ LƯU TRẠNG THÁI
                ps.setInt(3, i.getIce_cream_id());  // Điều kiện WHERE
            }
        );
    }
    public List<IceCream> findAll() {
        List<IceCream> list = find("SELECT * FROM ice_creams", null);
        System.out.println("DEBUG: Số lượng sản phẩm tìm thấy trong DB là: " + (list != null ? list.size() : "NULL"));
        return list;
    }
}