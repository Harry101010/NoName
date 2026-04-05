package aptech.proj_NN_group2.controller;

import aptech.proj_NN_group2.model.NguyenLieuModel;
import aptech.proj_NN_group2.util.NguyenLieuDAO;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;
import java.sql.Date;

public class NguyenLieuController {

    // Form Nhập liệu
    @FXML private TextField txtTen, txtNguonGoc, txtSoLuong, txtGia, txtSearch;
    @FXML private ComboBox<String> cbDonVi;
    @FXML private DatePicker dpHanSD;

    // Bảng danh sách
    @FXML private TableView<NguyenLieuModel> tableNguyenLieu;
    @FXML private TableColumn<NguyenLieuModel, Integer> colId;
    @FXML private TableColumn<NguyenLieuModel, String> colTen, colDonVi, colNgayNhap, colNguonGoc;
    @FXML private TableColumn<NguyenLieuModel, Double> colSoLuong;
    @FXML private TableColumn<NguyenLieuModel, Date> colHSD;
    @FXML private Label lblStatus;

    private ObservableList<NguyenLieuModel> masterData;

    @FXML
    public void initialize() {
        // 1. Cài đặt ComboBox
        cbDonVi.getItems().setAll("kg", "lít", "gram", "hộp", "thùng");
        cbDonVi.getSelectionModel().selectFirst();

        // 2. Cấu hình các cột bảng
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTen.setCellValueFactory(new PropertyValueFactory<>("ten"));
        colNguonGoc.setCellValueFactory(new PropertyValueFactory<>("nguonGoc"));
        colSoLuong.setCellValueFactory(new PropertyValueFactory<>("soLuong"));
        colDonVi.setCellValueFactory(new PropertyValueFactory<>("donVi"));
        colHSD.setCellValueFactory(new PropertyValueFactory<>("hanSuDung"));
        colNgayNhap.setCellValueFactory(new PropertyValueFactory<>("ngayNhap"));

        // 3. Tải dữ liệu
        loadTableData();
    }

    private void loadTableData() {
        NguyenLieuDAO dao = new NguyenLieuDAO();
        masterData = dao.getAll();
        
        // Cài đặt Filter tìm kiếm nhanh
        FilteredList<NguyenLieuModel> filteredData = new FilteredList<>(masterData, p -> true);
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(nl -> {
                if (newVal == null || newVal.isEmpty()) return true;
                return nl.getTen().toLowerCase().contains(newVal.toLowerCase());
            });
        });

        tableNguyenLieu.setItems(filteredData);
        lblStatus.setText("Tổng số lô hàng: " + masterData.size());
    }

    @FXML
    private void handleThem() {
        try {
            String ten = txtTen.getText();
            double sl = Double.parseDouble(txtSoLuong.getText());
            double gia = Double.parseDouble(txtGia.getText());
            LocalDate hanSD = dpHanSD.getValue();

            if (ten.isEmpty() || hanSD == null) throw new Exception();

            NguyenLieuDAO dao = new NguyenLieuDAO();
            if (dao.insert(ten, txtNguonGoc.getText(), cbDonVi.getValue(), sl, Date.valueOf(hanSD), gia)) {
                new Alert(Alert.AlertType.INFORMATION, "Đã nhập kho thành công!").show();
                loadTableData();
                clearFields();
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Vui lòng nhập đúng và đủ thông tin!").show();
        }
    }

    @FXML
    private void handleRefresh() {
        loadTableData();
        txtSearch.clear();
    }

    private void clearFields() {
        txtTen.clear(); txtNguonGoc.clear(); txtSoLuong.clear(); txtGia.clear();
        dpHanSD.setValue(null);
    }
}