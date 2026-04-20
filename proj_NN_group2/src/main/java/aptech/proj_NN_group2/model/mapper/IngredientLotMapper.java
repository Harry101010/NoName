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
        lot.setImportDate(rs.getString("import_date"));
        lot.setExpiryDate(rs.getString("expiry_date"));
        lot.setSupplierName(rs.getString("supplier_name"));

        return lot;
    }
}