package aptech.proj_NN_group2.controller.production;

import aptech.proj_NN_group2.util.CurrentUser;
import aptech.proj_NN_group2.util.NavigationUtil;
import aptech.proj_NN_group2.util.StringValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainMenuController {

    @FXML private Label lblUserInfo;

    @FXML
    public void initialize() {
        if (lblUserInfo != null && CurrentUser.isLoggedIn()) {
            lblUserInfo.setText(CurrentUser.getUser().getUsername()
                    + "  |  " + CurrentUser.getRoleName());
        }
    }

    @FXML
    private void goToCreateBatch(ActionEvent event) {
        NavigationUtil.goTo(event, StringValue.VIEW_CREATE_BATCH, "Tạo mẻ sản xuất");
    }

    @FXML
    private void goToProductionDashboard(ActionEvent event) {
        NavigationUtil.goTo(event, StringValue.VIEW_PRODUCTION_DASHBOARD, "Quản lý sản xuất");
    }

    @FXML
    private void goToProductionProcess(ActionEvent event) {
        NavigationUtil.goTo(event, StringValue.VIEW_PRODUCTION_PROCESS, "Tiến trình sản xuất");
    }

    @FXML
    private void goToIngredientIssueRequest(ActionEvent event) {
        NavigationUtil.goTo(event, StringValue.VIEW_INGREDIENT_ISSUE_REQUEST, "Yêu cầu xuất nguyên liệu");
    }

    @FXML
    private void goToConfirmReceivedIngredient(ActionEvent event) {
        NavigationUtil.goTo(event, StringValue.VIEW_CONFIRM_RECEIVED_INGREDIENT, "Xác nhận nhận nguyên liệu");
    }

    @FXML
    private void goToStageDetail(ActionEvent event) {
        NavigationUtil.goTo(event, StringValue.VIEW_STAGE_DETAIL, "Ghi nhận công đoạn");
    }
    
    @FXML
    private void goToRecipeManagement(ActionEvent event) {
        NavigationUtil.goTo(event, StringValue.VIEW_RECIPE_MANAGEMENT, "Quản lý công thức");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
    	NavigationUtil.logout(event);
    }
}