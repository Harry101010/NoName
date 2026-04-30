package aptech.proj_NN_group2.util;

import java.util.Optional;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Window;

public final class DialogUtil {

    private DialogUtil() {
    }

    public static void info(String title, String content) {
        show(Alert.AlertType.INFORMATION, null, title, null, content);
    }

    public static void info(Node owner, String title, String content) {
        show(Alert.AlertType.INFORMATION, ownerWindow(owner), title, null, content);
    }

    public static void warning(String title, String content) {
        show(Alert.AlertType.WARNING, null, title, null, content);
    }

    public static void warning(Node owner, String title, String content) {
        show(Alert.AlertType.WARNING, ownerWindow(owner), title, null, content);
    }

    public static void error(String title, String content) {
        show(Alert.AlertType.ERROR, null, title, null, content);
    }

    public static void error(Node owner, String title, String content) {
        show(Alert.AlertType.ERROR, ownerWindow(owner), title, null, content);
    }

    public static boolean confirm(Node owner, String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        Window ownerWindow = ownerWindow(owner);
        if (ownerWindow != null) {
            alert.initOwner(ownerWindow);
        }
        alert.setTitle(title != null ? title : "");
        alert.setHeaderText(null);
        alert.setContentText(content != null ? content : "");

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public static boolean confirmYesNo(Node owner, String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        Window ownerWindow = ownerWindow(owner);
        if (ownerWindow != null) {
            alert.initOwner(ownerWindow);
        }
        alert.setTitle(title != null ? title : "");
        alert.setHeaderText(null);
        alert.setContentText(content != null ? content : "");
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.YES;
    }

    public static void exception(Node owner, String title, Throwable ex) {
        String message = ex == null ? "Unknown error" : ex.getMessage();
        show(Alert.AlertType.ERROR, ownerWindow(owner), title, null, message);
    }

    private static void show(Alert.AlertType type, Window owner, String title, String header, String content) {
        Alert alert = new Alert(type);
        if (owner != null) {
            alert.initOwner(owner);
        }
        alert.setTitle(title != null ? title : "");
        alert.setHeaderText(header);
        alert.setContentText(content != null ? content : "");
        alert.showAndWait();
    }

    private static Window ownerWindow(Node owner) {
        if (owner == null || owner.getScene() == null) {
            return null;
        }
        return owner.getScene().getWindow();
    }
}