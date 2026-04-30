package aptech.proj_NN_group2.view.ui;

import aptech.proj_NN_group2.model.entity.production_stage.ProductionSummary;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;

public class ProgressTableCell extends TableCell<ProductionSummary, Double> {
    private final HBox container = new HBox(5);
    private final ProgressBar progressBar = new ProgressBar();
    private final Label percentLabel = new Label();

    public ProgressTableCell() {
        container.setAlignment(Pos.CENTER_LEFT);
        container.getChildren().addAll(progressBar, percentLabel);
        progressBar.setPrefWidth(100);
    }

    @Override
    protected void updateItem(Double progress, boolean empty) {
        super.updateItem(progress, empty);

        if (empty || progress == null) {
            setGraphic(null);
        } else {
            // Lấy dữ liệu của dòng hiện tại
            ProductionSummary data = getTableRow().getItem();
            
            // Cập nhật giá trị
            progressBar.setProgress(progress / 100.0);
            percentLabel.setText(String.format("%.0f%%", progress));

            // Logic đổi màu
            if (data != null && data.isFailed()) {
                // Màu đỏ cho trạng thái Failed
                progressBar.setStyle("-fx-accent: #e74c3c;"); 
            } else {
                // Màu xanh mặc định cho trạng thái bình thường
                progressBar.setStyle("-fx-accent: #2ecc71;"); 
            }

            setGraphic(container);
        }
    }
}