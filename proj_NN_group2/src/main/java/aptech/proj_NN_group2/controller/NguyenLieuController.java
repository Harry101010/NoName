package aptech.proj_NN_group2.controller;

import aptech.proj_NN_group2.util.NguyenLieuDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;
import java.sql.Date;

public class NguyenLieuController {

    @FXML private TextField txtTen;
    @FXML private TextField txtNguonGoc;
    @FXML private TextField txtSoLuong;
    @FXML private TextField txtGia;
    @FXML private ComboBox<String> cbDonVi;
    @FXML private DatePicker dpHanSD;

    @FXML
    public void initialize() {
        cbDonVi.getItems().addAll("kg", "lít", "gram", "hộp", "thùng");
        cbDonVi.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleThem() {
        // 1. Lấy dữ liệu từ giao diện
        String ten = txtTen.getText();
        String nguonGoc = txtNguonGoc.getText();
        String donVi = cbDonVi.getValue();
        LocalDate hanSD = dpHanSD.getValue();

        // Kiểm tra validation cơ bản
        if (ten.isEmpty() || txtSoLuong.getText().isEmpty() || hanSD == null) {
            showAlert("Lỗi", "Vui lòng nhập đủ Tên, Số lượng và Hạn sử dụng!");
            return;
        }

        try {
            double soLuong = Double.parseDouble(txtSoLuong.getText());
            double gia = Double.parseDouble(txtGia.getText());

            // 2. GỌI LOGIC SQL TỪ DAO (Lễ tân gọi đầu bếp)
            NguyenLieuDAO dao = new NguyenLieuDAO();
            boolean thanhCong = dao.insert(ten, nguonGoc, donVi, soLuong, Date.valueOf(hanSD), gia);

            if (thanhCong) {
                showAlert("Thành công", "Đã nhập kho nguyên liệu: " + ten);
                clearFields();
            } else {
                showAlert("Thất bại", "Không thể lưu vào cơ sở dữ liệu!");
            }
        } catch (NumberFormatException e) {
            showAlert("Lỗi", "Số lượng và Giá phải là số!");
        }
    }

    private void clearFields() {
        txtTen.clear(); txtNguonGoc.clear(); txtSoLuong.clear(); txtGia.clear();
        dpHanSD.setValue(null);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}