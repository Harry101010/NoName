package aptech.proj_NN_group2.util;

import java.net.URL;
import java.util.Objects;
import java.util.prefs.Preferences;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.Window;

public final class ThemeManager {

    public enum Theme {
        LIGHT("/aptech/proj_NN_group2/css/light.css"),
        DARK("/aptech/proj_NN_group2/css/dark.css");

        private final String cssPath;

        Theme(String cssPath) {
            this.cssPath = cssPath;
        }

        public String getCssPath() {
            return cssPath;
        }

        public static Theme fromName(String value) {
            if (value == null) {
                return LIGHT;
            }
            for (Theme theme : values()) {
                if (theme.name().equalsIgnoreCase(value.trim())) {
                    return theme;
                }
            }
            return LIGHT;
        }
    }

    private static final String PREF_KEY_THEME = "theme";
    private static final Preferences PREFS = Preferences.userNodeForPackage(ThemeManager.class);

    private static Theme currentTheme = Theme.fromName(PREFS.get(PREF_KEY_THEME, Theme.LIGHT.name()));

    private ThemeManager() {
    }

    public static Theme getCurrentTheme() {
        return currentTheme;
    }

    public static void setTheme(Theme theme) {
        currentTheme = Objects.requireNonNull(theme, "theme must not be null");
        PREFS.put(PREF_KEY_THEME, currentTheme.name());
    }

    public static void useTheme(Theme theme, ActionEvent event) {
        setTheme(theme);
        applyTheme(event);
    }

    public static void toggleTheme() {
        setTheme(currentTheme == Theme.LIGHT ? Theme.DARK : Theme.LIGHT);
    }

    public static void toggleTheme(ActionEvent event) {
        toggleTheme();
        applyTheme(event);
    }

    public static void applyTheme(Scene scene) {
        if (scene == null) {
            return;
        }

        String stylesheet = resolveStylesheet(currentTheme);
        scene.getStylesheets().removeIf(ThemeManager::isThemeStylesheet);
        if (!scene.getStylesheets().contains(stylesheet)) {
            scene.getStylesheets().add(stylesheet);
        }

        if (scene.getRoot() != null) {
            scene.getRoot().applyCss();
            scene.getRoot().requestLayout();
        }
    }

    public static void applyTheme(ActionEvent event) {
        Scene scene = resolveScene(event);
        if (scene != null) {
            applyTheme(scene);
        }
    }

    private static boolean isThemeStylesheet(String path) {
        return path != null && (path.endsWith("/css/light.css") || path.endsWith("/css/dark.css"));
    }

    private static String resolveStylesheet(Theme theme) {
        URL url = ThemeManager.class.getResource(theme.getCssPath());
        if (url == null) {
            throw new IllegalStateException("Theme stylesheet not found: " + theme.getCssPath());
        }
        return url.toExternalForm();
    }

    private static Scene resolveScene(ActionEvent event) {
        if (event == null || event.getSource() == null) {
            return null;
        }

        Object source = event.getSource();

        if (source instanceof Node node) {
            return node.getScene();
        }

        if (source instanceof MenuItem menuItem && menuItem.getParentPopup() != null) {
            Window window = menuItem.getParentPopup().getOwnerWindow();
            if (window != null) {
                return window.getScene();
            }
        }

        return null;
    }
}