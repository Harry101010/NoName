package aptech.proj_NN_group2.controller;

import aptech.proj_NN_group2.util.DashboardDAO;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardController {
    @FXML private Label lblTongMeKem, lblCanhBaoKho, lblClock;
    @FXML private ListView<String> lvNguyenLieuSapHet;

    public void initialize() {
        loadDashboardData();
        startClock();
    }

    @FXML
    private void handleRefresh() {
        loadDashboardData();
    }

    private void loadDashboardData() {
        DashboardDAO dao = new DashboardDAO();
        
        // 1. Cập nhật số mẻ kem
        lblTongMeKem.setText(String.valueOf(dao.getSlanMeKemHomNay()));
        
        // 2. Cập nhật danh sách cảnh báo
        List<String> list = dao.getNguyenLieuSapHet();
        lvNguyenLieuSapHet.getItems().clear();
        lvNguyenLieuSapHet.getItems().addAll(list);
        
        // 3. Cập nhật số lượng cảnh báo đỏ
        lblCanhBaoKho.setText(String.valueOf(list.size()));
    }

    private void startClock() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy");
            lblClock.setText(LocalDateTime.now().format(formatter));
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }
}