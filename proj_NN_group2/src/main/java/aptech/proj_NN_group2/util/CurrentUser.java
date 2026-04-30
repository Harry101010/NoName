package aptech.proj_NN_group2.util;

import aptech.proj_NN_group2.model.entity.User;

public final class CurrentUser {

    private static volatile User user;

    private CurrentUser() {
    }

    public static void setUser(User u) {
        user = u;
    }

    public static User getUser() {
        return user;
    }

    public static User requireUser() {
        if (user == null) {
            throw new IllegalStateException("No user is currently logged in.");
        }
        return user;
    }

    public static void clear() {
        user = null;
    }

    public static boolean isLoggedIn() {
        return user != null;
    }

    public static int getUserId() {
        return user != null ? user.getUserId() : -1;
    }

    public static String getUsername() {
        return user != null && user.getUsername() != null ? user.getUsername() : "";
    }

    public static String getRoleName() {
        return user != null && user.getRoleName() != null ? user.getRoleName() : "";
    }

    public static int getRoleId() {
        return user != null ? user.getRoleId() : -1;
    }

    public static boolean hasUserId(int userId) {
        return user != null && user.getUserId() == userId;
    }

    public static boolean hasRoleId(int roleId) {
        return user != null && user.getRoleId() == roleId;
    }

    public static boolean hasRoleName(String roleName) {
        if (user == null || roleName == null) {
            return false;
        }
        return roleName.equalsIgnoreCase(getRoleName());
    }

    public static boolean isAdmin() {
        return hasRoleId(1) || "Admin".equalsIgnoreCase(getRoleName());
    }
}
