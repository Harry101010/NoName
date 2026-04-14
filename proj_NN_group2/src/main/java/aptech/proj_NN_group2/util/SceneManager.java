package aptech.proj_NN_group2.util;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.function.Consumer;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public final class SceneManager {

    private SceneManager() {
    }

    public static FXMLLoader createLoader(String fxmlPath) throws IOException {
        URL url = resolveUrl(fxmlPath);
        return new FXMLLoader(url);
    }

    public static Parent loadRoot(String fxmlPath) throws IOException {
        FXMLLoader loader = createLoader(fxmlPath);
        return loader.load();
    }

    public static void show(Stage stage, String fxmlPath, String title) throws IOException {
        Objects.requireNonNull(stage, "stage must not be null");

        Parent root = loadRoot(fxmlPath);
        stage.setScene(new Scene(root));
        stage.setTitle(title != null ? title : "");
        stage.centerOnScreen();
        stage.show();
    }

    public static void switchScene(Stage stage, String fxmlPath, String title) throws IOException {
        Objects.requireNonNull(stage, "stage must not be null");

        Parent root = loadRoot(fxmlPath);
        stage.setScene(new Scene(root));
        stage.setTitle(title != null ? title : stage.getTitle());
        stage.centerOnScreen();
        stage.show();
    }

    public static void switchScene(Node anchor, String fxmlPath, String title) throws IOException {
        switchScene(resolveStage(anchor), fxmlPath, title);
    }

    public static void switchScene(ActionEvent event, String fxmlPath, String title) throws IOException {
        switchScene(resolveStage(event), fxmlPath, title);
    }

    public static <T> T openModal(Node anchor, String fxmlPath, String title, Consumer<T> controllerInitializer) throws IOException {
        return openModal(resolveWindow(anchor), fxmlPath, title, controllerInitializer);
    }

    public static <T> T openModal(ActionEvent event, String fxmlPath, String title, Consumer<T> controllerInitializer) throws IOException {
        return openModal(resolveWindow(event), fxmlPath, title, controllerInitializer);
    }

    public static <T> T openModal(Node anchor, String fxmlPath, String title, Class<T> controllerType, Consumer<T> controllerInitializer) throws IOException {
        return openModal(resolveWindow(anchor), fxmlPath, title, controllerType, controllerInitializer);
    }

    public static <T> T openModal(ActionEvent event, String fxmlPath, String title, Class<T> controllerType, Consumer<T> controllerInitializer) throws IOException {
        return openModal(resolveWindow(event), fxmlPath, title, controllerType, controllerInitializer);
    }

    public static void closeWindow(Node node) {
        if (node == null || node.getScene() == null || node.getScene().getWindow() == null) {
            return;
        }
        ((Stage) node.getScene().getWindow()).close();
    }

    public static void closeWindow(ActionEvent event) {
        Object source = event.getSource();
        if (source instanceof Node node) {
            closeWindow(node);
        } else if (source instanceof MenuItem menuItem && menuItem.getParentPopup() != null) {
            Window window = menuItem.getParentPopup().getOwnerWindow();
            if (window instanceof Stage stage) {
                stage.close();
            }
        }
    }

    private static <T> T openModal(Window owner, String fxmlPath, String title, Consumer<T> controllerInitializer) throws IOException {
        FXMLLoader loader = createLoader(fxmlPath);
        Parent root = loader.load();

        @SuppressWarnings("unchecked")
        T controller = (T) loader.getController();

        if (controllerInitializer != null) {
            controllerInitializer.accept(controller);
        }

        Stage stage = new Stage();
        if (owner != null) {
            stage.initOwner(owner);
        }
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.setTitle(title != null ? title : "");
        stage.centerOnScreen();
        stage.showAndWait();

        return controller;
    }

    private static <T> T openModal(Window owner, String fxmlPath, String title, Class<T> controllerType, Consumer<T> controllerInitializer) throws IOException {
        Objects.requireNonNull(controllerType, "controllerType must not be null");

        FXMLLoader loader = createLoader(fxmlPath);
        Parent root = loader.load();
        T controller = controllerType.cast(loader.getController());

        if (controllerInitializer != null) {
            controllerInitializer.accept(controller);
        }

        Stage stage = new Stage();
        if (owner != null) {
            stage.initOwner(owner);
        }
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.setTitle(title != null ? title : "");
        stage.centerOnScreen();
        stage.showAndWait();

        return controller;
    }

    private static Stage resolveStage(Node node) {
        Objects.requireNonNull(node, "node must not be null");
        if (node.getScene() == null || node.getScene().getWindow() == null) {
            throw new IllegalStateException("Node is not attached to a scene.");
        }
        return (Stage) node.getScene().getWindow();
    }

    private static Stage resolveStage(ActionEvent event) {
        Objects.requireNonNull(event, "event must not be null");
        Object source = event.getSource();

        if (source instanceof Node node) {
            return resolveStage(node);
        }

        if (source instanceof MenuItem menuItem && menuItem.getParentPopup() != null) {
            Window window = menuItem.getParentPopup().getOwnerWindow();
            if (window instanceof Stage stage) {
                return stage;
            }
        }

        throw new IllegalArgumentException("Cannot resolve Stage from ActionEvent source.");
    }

    private static Window resolveWindow(Node node) {
        if (node == null || node.getScene() == null) {
            return null;
        }
        return node.getScene().getWindow();
    }

    private static Window resolveWindow(ActionEvent event) {
        Object source = event.getSource();

        if (source instanceof Node node) {
            return resolveWindow(node);
        }

        if (source instanceof MenuItem menuItem && menuItem.getParentPopup() != null) {
            return menuItem.getParentPopup().getOwnerWindow();
        }

        return null;
    }

    private static URL resolveUrl(String fxmlPath) throws IOException {
        String normalized = normalizePath(fxmlPath);
        URL url = SceneManager.class.getResource(normalized);
        if (url == null) {
            throw new IOException("FXML not found: " + normalized);
        }
        return url;
    }

    private static String normalizePath(String fxmlPath) {
        String path = Objects.requireNonNull(fxmlPath, "fxmlPath must not be null").trim();

        if (!path.endsWith(".fxml")) {
            path += ".fxml";
        }

        if (!path.startsWith("/")) {
            path = "/aptech/proj_NN_group2/" + path;
        }

        return path;
    }
}