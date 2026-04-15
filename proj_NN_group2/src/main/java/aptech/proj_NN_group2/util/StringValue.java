package aptech.proj_NN_group2.util;

public final class StringValue {

    public static final String VIEW_LOGIN = "/aptech/proj_NN_group2/auth/login.fxml";
    public static final String VIEW_FORGOT_PASSWORD = "/aptech/proj_NN_group2/auth/forgot_password.fxml";

    public static final String VIEW_MAIN_MENU = "/aptech/proj_NN_group2/main_menu.fxml";

    public static final String VIEW_USER_MANAGEMENT = "/aptech/proj_NN_group2/admin/user_management.fxml";
    public static final String VIEW_USER_EDIT = "/aptech/proj_NN_group2/admin/UserEdit.fxml";
    public static final String VIEW_ADMIN_PROFILE = "/aptech/proj_NN_group2/admin/AdminProfile.fxml";

    public static final String VIEW_CREATE_BATCH = "/aptech/proj_NN_group2/production/create_batch.fxml";

    public static final String VIEW_PRODUCTION_PROCESS = "/aptech/proj_NN_group2/production_process.fxml";
    public static final String VIEW_RECIPE_MANAGEMENT = "/aptech/proj_NN_group2/recipe_management.fxml";
    public static final String VIEW_WAREHOUSE_DASHBOARD = "/aptech/proj_NN_group2/warehouse_dashboard.fxml";

    public static final String VIEW_SALEMAN_CREATE_ISSUE = "/aptech/proj_NN_group2/saleman/create_issue.fxml";
    public static final String VIEW_SALEMAN_FINISHED_PRODUCT_WAREHOUSE = "/aptech/proj_NN_group2/saleman/finished_product_warehouse.fxml";
    public static final String VIEW_SALEMAN_WAREHOUSE_DASHBOARD = "/aptech/proj_NN_group2/saleman/saleman_warehouse_dashboard.fxml";

    public static final String VIEW_INGREDIENT_ISSUE_REQUEST = "/aptech/proj_NN_group2/production/ingredient_issue_request";
    public static final String VIEW_CONFIRM_RECEIVED_INGREDIENT = "/aptech/proj_NN_group2/production/confirm_received_ingredient";
    public static final String VIEW_STAGE_DETAIL = "/aptech/proj_NN_group2/production/stage_detail";

    private static final String CTR_PACK = "aptech.proj_NN_group2.controller.";
    public static final String CTR_LOGIN= CTR_PACK + "LoginController";
    public static final String CTR_ADMIN = CTR_PACK + "AdminController";
    public static final String CTR_CREATE_BATCH = CTR_PACK + "CreateBatchController";
    public static final String CTR_RECIPE_MANAGEMENT = CTR_PACK + "RecipeManagementController";
    public static final String CTR_PRODUCTION_PROCESS = CTR_PACK + "ProductionProcessController";
    
    private StringValue() {
    }
}