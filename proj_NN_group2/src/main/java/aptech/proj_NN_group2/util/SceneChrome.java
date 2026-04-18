package aptech.proj_NN_group2.util;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

final class SceneChrome {

    private SceneChrome() {
    }

    static Parent installThemeMenu(Parent root) {
        List<MenuBar> menuBars = new ArrayList<>();
        collectMenuBars(root, menuBars);

        if (!menuBars.isEmpty()) {
            menuBars.forEach(ThemeMenuFactory::installInto);
            return root;
        }

        MenuBar menuBar = new MenuBar();
        ThemeMenuFactory.installInto(menuBar);

        if (root instanceof BorderPane borderPane) {
            Node top = borderPane.getTop();
            if (top == null) {
                borderPane.setTop(menuBar);
            } else {
                VBox topBox = new VBox(menuBar, top);
                topBox.getStyleClass().add("scene-top");
                borderPane.setTop(topBox);
            }
            return borderPane;
        }

        BorderPane wrapper = new BorderPane(root);
        wrapper.setTop(menuBar);
        return wrapper;
    }

    private static void collectMenuBars(Node node, List<MenuBar> out) {
        if (node == null) {
            return;
        }

        if (node instanceof MenuBar menuBar) {
            out.add(menuBar);
            return;
        }

        if (node instanceof ScrollPane scrollPane) {
            collectMenuBars(scrollPane.getContent(), out);
            return;
        }

        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                collectMenuBars(child, out);
            }
        }
    }
}