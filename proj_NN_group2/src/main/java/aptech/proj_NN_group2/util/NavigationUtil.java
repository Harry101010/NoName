package aptech.proj_NN_group2.util;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.scene.Node;

public final class NavigationUtil {

    private NavigationUtil() {
    }

    public static void goTo(ActionEvent event, String fxmlPath, String title) {
        try {
            SceneManager.switchScene(event, fxmlPath, title);
        } catch (IOException e) {
            // QUAN TRỌNG: In toàn bộ Stack Trace ra Console để xem lỗi thực sự
            e.printStackTrace(); 
            // Hiển thị thông báo thân thiện hơn cho người dùng
            DialogUtil.error("Lỗi điều hướng", "Không thể tải giao diện: " + fxmlPath);
        }
    }

    public static void goTo(Node ownerNode, String fxmlPath, String title) {
        try {
            SceneManager.switchScene(ownerNode, fxmlPath, title);
        } catch (IOException e) {
            // QUAN TRỌNG: In toàn bộ Stack Trace ra Console để xem lỗi thực sự
            e.printStackTrace();
            DialogUtil.error(ownerNode, "Lỗi điều hướng", "Không thể tải giao diện: " + fxmlPath);
        }
    }

    public static void logout(ActionEvent event) {
        CurrentUser.clear();
        goTo(event, StringValue.VIEW_LOGIN, "Đăng nhập hệ thống");
    }

    public static void logout(Node ownerNode) {
        CurrentUser.clear();
        goTo(ownerNode, StringValue.VIEW_LOGIN, "Đăng nhập hệ thống");
    }
}