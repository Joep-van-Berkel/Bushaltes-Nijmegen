package View;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class MenuBarView extends MenuBar {

    public MenuBarView(MyScene scene) {
        // Create Menus
        Menu homeMenu = new Menu("Home");
        Menu locatiesMenu = new Menu("Locaties");
        Menu medewerkersMenu = new Menu("Medewerkers");
        Menu adminMenu = new Menu("Admin");

        // Create MenuItems
        MenuItem homeMenuItem = new MenuItem("Home");
        MenuItem locatiesMenuItem = new MenuItem("Locaties");
        MenuItem medewerkersMenuItem = new MenuItem("Medewerkers");
        MenuItem adminMenuItem = new MenuItem("Admin");

        // Add MenuItems to Menus
        homeMenu.getItems().add(homeMenuItem);
        locatiesMenu.getItems().add(locatiesMenuItem);
        medewerkersMenu.getItems().add(medewerkersMenuItem);
        adminMenu.getItems().add(adminMenuItem);

        // Add Menus to MenuBar
        this.getMenus().addAll(homeMenu, locatiesMenu, medewerkersMenu, adminMenu);

        // Add event handlers for MenuItems
        homeMenuItem.setOnAction(e -> scene.goToHomeView());
        locatiesMenuItem.setOnAction(e -> scene.goToLocatiesView());
        medewerkersMenuItem.setOnAction(e -> scene.goToMedewerkerView());
        adminMenuItem.setOnAction(e -> scene.goToAdminView());
    }
}