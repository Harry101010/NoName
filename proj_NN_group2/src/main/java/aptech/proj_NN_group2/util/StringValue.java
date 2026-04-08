package aptech.proj_NN_group2.util;

public class StringValue {
	public static final String VIEW_LOGIN = "login";
    public static final String VIEW_USER_MANAGEMENT = "user_management";
    public static final String VIEW_CREATE_BATCH = "create_batch";

    private static final String CTR_PACK = "aptech.proj_NN_group2.controller.";
    public static final String CTR_LOGIN= CTR_PACK + "LoginController";
    public static final String CTR_ADMIN = CTR_PACK + "AdminController";
    public static final String CTR_CREATE_BATCH = CTR_PACK + "CreateBatchController";
    
    private StringValue() {
    }
}
