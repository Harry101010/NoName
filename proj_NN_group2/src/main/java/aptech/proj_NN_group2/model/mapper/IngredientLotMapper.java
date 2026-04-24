package aptech.proj_NN_group2.model.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import aptech.proj_NN_group2.model.entity.IngredientLot;

public class IngredientLotMapper {

	public static IngredientLot map(ResultSet rs) throws SQLException {
	    IngredientLot lot = new IngredientLot();

	    lot.setLotId(rs.getInt("lot_id"));
	    lot.setIngredientId(rs.getInt("ingredient_id"));
	    lot.setIngredientName(rs.getString("ingredient_name"));
	    lot.setUnitName(rs.getString("unit_name"));
	    lot.setRemainingQuantity(rs.getDouble("remaining_quantity"));
	    lot.setReceivedQuantity(rs.getDouble("received_quantity"));
//	    lot.setStorageCondition(rs.getString("storage_condition"));

	    // ✅ FIX DATE
	    if (rs.getDate("import_date") != null) {
	        lot.setImportDate(rs.getDate("import_date").toLocalDate());
	    }

	    if (rs.getDate("expiry_date") != null) {
	        lot.setExpiryDate(rs.getDate("expiry_date").toLocalDate());
	    }

	    lot.setSupplierName(rs.getString("supplier_name"));

	    return lot;
	}

}