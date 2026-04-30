package aptech.proj_NN_group2.util;

import javafx.event.ActionEvent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public final class ThemeMenuFactory {

    private static final String THEME_MENU_TEXT = "Giao diện";
    private static final String THEME_MENU_ID = "app-theme-menu";

    private ThemeMenuFactory() {
    }

    public static Menu createThemeMenu() {
        Menu menu = new Menu(THEME_MENU_TEXT);
        menu.setId(THEME_MENU_ID);
        menu.getItems().addAll(
                createLightItem(),
                createDarkItem(),
                new SeparatorMenuItem(),
                createToggleItem()
        );
        return menu;
    }

    public static void installInto(MenuBar menuBar) {
        if (menuBar == null) {
            return;
        }

        for (Menu menu : menuBar.getMenus()) {
            if (THEME_MENU_ID.equals(menu.getId()) || THEME_MENU_TEXT.equalsIgnoreCase(menu.getText())) {
                menu.setId(THEME_MENU_ID);
                menu.getItems().setAll(
                        createLightItem(),
                        createDarkItem(),
                        new SeparatorMenuItem(),
                        createToggleItem()
                );
                return;
            }
        }

        menuBar.getMenus().add(0, createThemeMenu());
    }

    private static MenuItem createLightItem() {
        MenuItem item = new MenuItem("Sáng");
        item.setOnAction(ThemeMenuFactory::handleLight);
        return item;
    }

    private static MenuItem createDarkItem() {
        MenuItem item = new MenuItem("Tối");
        item.setOnAction(ThemeMenuFactory::handleDark);
        return item;
    }

    private static MenuItem createToggleItem() {
        MenuItem item = new MenuItem("Đổi giao diện");
        item.setOnAction(ThemeMenuFactory::handleToggle);
        return item;
    }

    private static void handleLight(ActionEvent event) {
        ThemeManager.useTheme(ThemeManager.Theme.LIGHT, event);
    }

    private static void handleDark(ActionEvent event) {
        ThemeManager.useTheme(ThemeManager.Theme.DARK, event);
    }

    private static void handleToggle(ActionEvent event) {
        ThemeManager.toggleTheme(event);
    }
}