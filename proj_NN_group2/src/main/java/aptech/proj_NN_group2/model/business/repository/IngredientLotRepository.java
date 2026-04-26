package aptech.proj_NN_group2.model.business.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import aptech.proj_NN_group2.model.business.BaseRepository;
import aptech.proj_NN_group2.model.entity.ingredient.IngredientLot;
import aptech.proj_NN_group2.model.interfaces.ICreate;

public class IngredientLotRepository extends BaseRepository<IngredientLot> implements ICreate<IngredientLot> {

    // 1. Logic Ghi (Create)
	@Override
	public boolean create(IngredientLot lot) {
	    String sql = """
	        INSERT INTO ingredient_lots 
	        (ingredient_id, import_date, expiry_date, received_quantity, remaining_quantity, supplier_id) 
	        VALUES (?, ?, ?, ?, ?, ?)
	    """;
	    
	    return executeUpdate(sql, ps -> {
	        ps.setInt(1, lot.getIngredientId());       // Dùng getIngredientId()
	        ps.setTimestamp(2, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
	        ps.setDate(3, java.sql.Date.valueOf(lot.getExpiryDate())); // Dùng getExpiryDate()
	        ps.setDouble(4, lot.getReceivedQuantity()); // Dùng getReceivedQuantity()
	        ps.setDouble(5, lot.getRemainingQuantity()); // Dùng getRemainingQuantity()
	        ps.setInt(6, lot.getSupplierId());         // Dùng getSupplierId()
	    });
	}

    // 2. Logic Đọc phục vụ FIFO (Dùng lại từ file InventoryRepository cũ)
    public List<IngredientLot> findValidLotsByIngredientId(int ingredientId) {
        String sql = "SELECT * FROM ingredient_lots WHERE ingredient_id = ? AND remaining_quantity > 0 ORDER BY expiry_date ASC";
        return find(sql, ps -> ps.setInt(1, ingredientId));
    }

    // 3. Logic Mapping (Chuyển từ ResultSet sang Object)
    @Override
    protected IngredientLot map(ResultSet rs) throws SQLException {
        IngredientLot lot = new IngredientLot();
        lot.setLotId(rs.getInt("lot_id"));
        lot.setIngredientId(rs.getInt("ingredient_id"));
        
        if (rs.getDate("import_date") != null)
            lot.setImportDate(rs.getDate("import_date").toLocalDate());
        if (rs.getDate("expiry_date") != null)
            lot.setExpiryDate(rs.getDate("expiry_date").toLocalDate());
            
        lot.setReceivedQuantity(rs.getDouble("received_quantity"));
        lot.setRemainingQuantity(rs.getDouble("remaining_quantity"));
        lot.setSupplierId(rs.getInt("supplier_id"));
        return lot;
    }
}