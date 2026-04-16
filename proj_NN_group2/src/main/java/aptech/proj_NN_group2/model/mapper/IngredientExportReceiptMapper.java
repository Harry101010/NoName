package aptech.proj_NN_group2.model.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import aptech.proj_NN_group2.model.entity.IngredientExportReceipt;
import aptech.proj_NN_group2.model.interfaces.IMapper;

public class IngredientExportReceiptMapper implements IMapper<IngredientExportReceipt> {
    @Override
    public IngredientExportReceipt RowMap(ResultSet rs) throws SQLException {
        IngredientExportReceipt r = new IngredientExportReceipt();
        r.setIngredient_export_receipt_id(rs.getInt("ingredient_export_receipt_id"));
        r.setIngredient_export_request_id(rs.getInt("ingredient_export_request_id"));
        r.setApproved_by((Integer) rs.getObject("approved_by"));
        r.setCreated_at(rs.getTimestamp("created_at"));
        r.setReceipt_status(rs.getString("receipt_status"));
        r.setNote(rs.getString("note"));
        try { r.setIce_cream_name(rs.getString("ice_cream_name")); } catch (SQLException ignored) {}
        try { r.setPlanned_output_kg(rs.getDouble("planned_output_kg")); } catch (SQLException ignored) {}
        try { r.setRequest_status(rs.getString("request_status")); } catch (SQLException ignored) {}
        return r;
    }
}
