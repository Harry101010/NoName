package aptech.proj_NN_group2.controller;

import aptech.proj_NN_group2.model.ThanhPhamModel;
import aptech.proj_NN_group2.util.TestConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.*;

public class ThanhPhamController {
    @FXML private TableView<ThanhPhamModel> tableThanhPham;
    @FXML private TableColumn<ThanhPhamModel, Integer> colId, colSoLuong;
    @FXML private TableColumn<ThanhPhamModel, String> colTen, colNgaySX, colHSD;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTen.setCellValueFactory(new PropertyValueFactory<>("tenKem"));
        colSoLuong.setCellValueFactory(new PropertyValueFactory<>("soLuong"));
        colNgaySX.setCellValueFactory(new PropertyValueFactory<>("ngaySanXuat"));
        colHSD.setCellValueFactory(new PropertyValueFactory<>("hanSuDung"));

        loadData();
    }

    private void loadData() {
        ObservableList<ThanhPhamModel> list = FXCollections.observableArrayList();
        // Câu lệnh SQL khớp với các cột bạn đang có trong Database
        String sql = "SELECT id_thanh_pham, ten_kem, so_luong_hop, ngay_san_xuat, han_su_dung_kem FROM KhoThanhPham";

        try (Connection con = new TestConnection().getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new ThanhPhamModel(
                    rs.getInt("id_thanh_pham"),
                    rs.getString("ten_kem"),
                    rs.getInt("so_luong_hop"),
                    rs.getString("ngay_san_xuat"),
                    rs.getString("han_su_dung_kem")
                ));
            }
            tableThanhPham.setItems(list);
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void handleRefresh() { loadData(); }
}