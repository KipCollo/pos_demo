package com.kipcollo.demo;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class POSHomePage {

    private final User currentUser;
    private BorderPane root;
    private Stage stage;

    public POSHomePage(User currentUser) {
        this.currentUser = currentUser;
    }

    public void show(Stage stage) {
        this.stage = stage;
        root = new BorderPane();
        root.setStyle("-fx-background-color: #ecf0f1;");

        root.setTop(createNavbar());
        root.setLeft(createSidebar());
        setCenter(new Dashboard(currentUser).getView());

        Scene scene = new Scene(root, 1200, 750);
        stage.setTitle("HardwareHub POS – " + currentUser.getDisplayName());
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }

    private HBox createNavbar() {
        HBox navbar = new HBox(16);
        navbar.setPadding(new Insets(12, 20, 12, 20));
        navbar.setAlignment(Pos.CENTER_LEFT);
        navbar.setStyle("-fx-background-color: #1a252f;");

        Label shopIcon = new Label("🔧");
        shopIcon.setFont(Font.font("Arial", 22));

        Label shopName = new Label("HardwareHub POS");
        shopName.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        shopName.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label roleTag = new Label(currentUser.getRole().toUpperCase());
        roleTag.setPadding(new Insets(3, 8, 3, 8));
        roleTag.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-background-radius: 4; -fx-font-size: 11;");

        Label userLabel = new Label("👤 " + currentUser.getDisplayName());
        userLabel.setTextFill(Color.web("#bdc3c7"));
        userLabel.setFont(Font.font("Arial", 13));

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 6 14 6 14;");
        logoutBtn.setOnAction(e -> logout());

        navbar.getChildren().addAll(shopIcon, shopName, spacer, roleTag, userLabel, logoutBtn);
        return navbar;
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(4);
        sidebar.setPadding(new Insets(16, 8, 16, 8));
        sidebar.setPrefWidth(175);
        sidebar.setStyle("-fx-background-color: #2c3e50;");

        Label menuLabel = new Label("MENU");
        menuLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        menuLabel.setTextFill(Color.web("#7f8c8d"));
        menuLabel.setPadding(new Insets(0, 0, 8, 8));

        sidebar.getChildren().add(menuLabel);

        addNavButton(sidebar, "📊 Dashboard",  () -> setCenter(new Dashboard(currentUser).getView()));
        addNavButton(sidebar, "🛒 Sales",       () -> setCenter(new Sales(currentUser).getView()));
        addNavButton(sidebar, "📦 Products",    () -> setCenter(new Products(currentUser).getView()));
        addNavButton(sidebar, "👥 Customers",   () -> setCenter(new Customers().getView()));
        addNavButton(sidebar, "📋 Orders",      () -> setCenter(new Orders().getView()));
        addNavButton(sidebar, "📈 Reports",     () -> setCenter(new Reports().getView()));

        if (currentUser.isAdmin()) {
            Separator sep = new Separator();
            sep.setStyle("-fx-background-color: #34495e;");
            VBox.setMargin(sep, new Insets(8, 0, 8, 0));
            sidebar.getChildren().add(sep);

            Label adminLabel = new Label("ADMIN");
            adminLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
            adminLabel.setTextFill(Color.web("#7f8c8d"));
            adminLabel.setPadding(new Insets(0, 0, 8, 8));
            sidebar.getChildren().add(adminLabel);

            addNavButton(sidebar, "⚙ Admin Panel", () -> setCenter(new AdminManagement(currentUser).getView()));
        }

        return sidebar;
    }

    private void addNavButton(VBox sidebar, String label, Runnable action) {
        Button btn = new Button(label);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(10, 12, 10, 12));
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #bdc3c7; -fx-font-size: 13; -fx-cursor: hand; -fx-background-radius: 6;");

        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 13; -fx-cursor: hand; -fx-background-radius: 6;"));
        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #bdc3c7; -fx-font-size: 13; -fx-cursor: hand; -fx-background-radius: 6;"));
        btn.setOnAction(e -> action.run());

        sidebar.getChildren().add(btn);
    }

    private void setCenter(Pane pane) {
        root.setCenter(pane);
    }

    private void logout() {
        LoginPage loginPage = new LoginPage(stage);
        loginPage.show();
    }
}
