
package aptech.proj_NN_group2.model.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import aptech.proj_NN_group2.model.entity.IngredientExportRequest;
import aptech.proj_NN_group2.model.interfaces.IMapper;

public class IngredientExportRequestMapper implements IMapper<IngredientExportRequest> {

    @Override
    public IngredientExportRequest RowMap(ResultSet rs) throws SQLException {
        IngredientExportRequest r = new IngredientExportRequest();

        // ===== DATA FROM TABLE =====
        r.setIngredient_export_request_id(rs.getInt("ingredient_export_request_id"));
        r.setProduction_order_id(rs.getInt("production_order_id"));
        r.setRequested_by((Integer) rs.getObject("requested_by"));
        r.setRequested_at(rs.getTimestamp("requested_at"));
        r.setRequest_status(rs.getString("request_status"));
        r.setNote(rs.getString("note"));

        // ===== DATA FROM JOIN =====
        // (giữ try-catch + thêm check an toàn)
        if (hasColumn(rs, "ice_cream_name")) {
            try {
                r.setIce_cream_name(rs.getString("ice_cream_name"));
            } catch (SQLException ignored) {}
        }

        if (hasColumn(rs, "planned_output_kg")) {
            try {
                r.setPlanned_output_kg(rs.getDouble("planned_output_kg"));
            } catch (SQLException ignored) {}
        }

        if (hasColumn(rs, "order_status")) {
            try {
                r.setOrder_status(rs.getString("order_status"));
            } catch (SQLException ignored) {}
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

