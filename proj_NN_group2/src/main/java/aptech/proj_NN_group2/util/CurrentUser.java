package aptech.proj_NN_group2.util;

import aptech.proj_NN_group2.model.entity.User;

public class CurrentUser {
	private static User user;

	public static void setUser(User u) {
		user = u;
	}

	public static User getUser() {
		return user;
	}
	
	public static void clear() {
		user = null;
	}
	
	public static boolean isLoggedIn() {
		return user != null;
	}
	//2 Hàm hỗ trợ lấy username, roleName nhanh nếu chưa login thì trả về chuổi rỗng tránh bị null
	public static String getUsername() {
		return user != null ? user.getUsername() : "";
	}
	
	public static String getRoleName() {
		return user != null ? user.getRoleName() : "";
	}
}
