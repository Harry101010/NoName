package aptech.proj_NN_group2.util;

public class StringValue {
    public static final String VIEW_LOGIN = "login";
    public static final String VIEW_USER_MANAGEMENT = "user_management";
    public static final String VIEW_CREATE_BATCH = "/aptech/proj_NN_group2/production/create_batch";
    public static final String VIEW_RECIPE_MANAGEMENT = "recipe_management";
    public static final String VIEW_PRODUCTION_PROCESS = "production_process";
    public static final String VIEW_MAIN_MENU = "main_menu";
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
