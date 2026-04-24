
package aptech.proj_NN_group2.model.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import aptech.proj_NN_group2.model.entity.IngredientExportReceipt;
import aptech.proj_NN_group2.model.interfaces.IMapper;

public class IngredientExportReceiptMapper implements IMapper<IngredientExportReceipt> {

    @Override
    public IngredientExportReceipt RowMap(ResultSet rs) throws SQLException {
        IngredientExportReceipt r = new IngredientExportReceipt();

        // ===== DATA FROM TABLE =====
        r.setIngredient_export_receipt_id(rs.getInt("ingredient_export_receipt_id"));
        r.setIngredient_export_request_id(rs.getInt("ingredient_export_request_id"));
        r.setApproved_by((Integer) rs.getObject("approved_by"));
        r.setCreated_at(rs.getTimestamp("created_at"));
        r.setReceipt_status(rs.getString("receipt_status"));
        r.setNote(rs.getString("note"));

        // ===== DATA FROM JOIN =====
        if (hasColumn(rs, "ice_cream_name")) {
            r.setIce_cream_name(rs.getString("ice_cream_name"));
        }

        if (hasColumn(rs, "planned_output_kg")) {
            r.setPlanned_output_kg(rs.getDouble("planned_output_kg"));
        }

        if (hasColumn(rs, "request_status")) {
            r.setRequest_status(rs.getString("request_status"));
        }

        return r;
    }

    // ===== HELPER =====
    private boolean hasColumn(ResultSet rs, String columnName) {
        try {
            rs.findColumn(columnName);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}

