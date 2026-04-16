package aptech.proj_NN_group2.util;

import java.util.List;

public final class UserRoleUtil {

    public static final int ROLE_ADMIN = 1;
    public static final int ROLE_PRODUCTION_MANAGER = 2;
    public static final int ROLE_WAREHOUSE_MANAGER = 3;
    public static final int ROLE_SALESMAN = 4;
    public static final int ROLE_STAFF = 5;

    public static final String ADMIN = "Admin";
    public static final String PRODUCTION_MANAGER = "Trưởng sản xuất";
    public static final String WAREHOUSE_MANAGER = "Quản lý kho";
    public static final String SALESMAN = "Nhân viên kinh doanh";
    public static final String STAFF = "Staff";

    private UserRoleUtil() {
    }

    public static List<String> editableRoles() {
        return List.of(PRODUCTION_MANAGER, WAREHOUSE_MANAGER, SALESMAN, STAFF);
    }

    public static List<String> allRoles() {
        return List.of(ADMIN, PRODUCTION_MANAGER, WAREHOUSE_MANAGER, SALESMAN, STAFF);
    }

    public static int toRoleId(String roleName) {
        if (roleName == null) {
            return ROLE_STAFF;
        }

        return switch (roleName.trim()) {
            case ADMIN -> ROLE_ADMIN;
            case PRODUCTION_MANAGER -> ROLE_PRODUCTION_MANAGER;
            case WAREHOUSE_MANAGER -> ROLE_WAREHOUSE_MANAGER;
            case SALESMAN -> ROLE_SALESMAN;
            default -> ROLE_STAFF;
        };
    }

    public static boolean isAdminRole(String roleName) {
        return roleName != null && ADMIN.equalsIgnoreCase(roleName.trim());
    }
}