package com.kipcollo.demo;

import javafx.application.Application;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;


public class POSHomePage extends Application {

//    @Override
//    public void start(Stage stage) {
//        BorderPane root = new BorderPane();
//
//        // Top Navbar
//        HBox navbar = new HBox(20);
//        navbar.setPadding(new Insets(10));
//        navbar.setStyle("-fx-background-color: #2c3e50;");
//
//        Label title = new Label("POS System");
//        title.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
//
//        Button salesBtn = new Button("Sales");
//        Button productsBtn = new Button("Products");
//        Button reportsBtn = new Button("Reports");
//        Button logoutBtn = new Button("Logout");
//
//        navbar.getChildren().addAll(title, salesBtn, productsBtn, reportsBtn, logoutBtn);
//        root.setTop(navbar);
//
//        // Left Sidebar
//        VBox sidebar = new VBox(15);
//        sidebar.setPadding(new Insets(15));
//        sidebar.setStyle("-fx-background-color: #34495e;");
//
//        Button dashboardBtn = new Button("Dashboard");
//        Button customersBtn = new Button("Customers");
//        Button ordersBtn = new Button("Orders");
//
//        sidebar.getChildren().addAll(dashboardBtn, customersBtn, ordersBtn);
//        root.setLeft(sidebar);
//
//        // Center Content
//        VBox centerContent = new VBox(20);
//        centerContent.setPadding(new Insets(20));
//
//        Label welcome = new Label("Welcome to POS Dashboard");
//        welcome.setStyle("-fx-font-size: 20px;");
//
//        HBox stats = new HBox(20);
//        stats.setAlignment(Pos.CENTER);
//
//        VBox salesCard = createCard("Total Sales", "KES 0");
//        VBox productsCard = createCard("Products", "0");
//        VBox customersCard = createCard("Customers", "0");
//
//        stats.getChildren().addAll(salesCard, productsCard, customersCard);
//
//        centerContent.getChildren().addAll(welcome, stats);
//        root.setCenter(centerContent);
//
//        // Scene
//        Scene scene = new Scene(root, 900, 600);
//        stage.setTitle("POS Home Page");
//        stage.setScene(scene);
//        stage.show();
//    }
//
//    private VBox createCard(String title, String value) {
//        VBox card = new VBox(10);
//        card.setPadding(new Insets(15));
//        card.setStyle("-fx-background-color: #ecf0f1; -fx-border-radius: 5; -fx-background-radius: 5;");
//
//        Label titleLabel = new Label(title);
//        Label valueLabel = new Label(value);
//        valueLabel.setStyle("-fx-font-size: 18px;");
//
//        card.getChildren().addAll(titleLabel, valueLabel);
//        return card;
//    }
//
//    public static void main(String[] args) {
//        launch();
//    }

    private BorderPane root;

    @Override
    public void start(Stage stage) {
        root = new BorderPane();

        root.setTop(createNavbar());
        root.setLeft(createSidebar());

        setCenter(new Dashboard().getView());

        Scene scene = new Scene(root, 1100, 700);
        stage.setTitle("POS System");
        stage.setScene(scene);
        stage.show();
    }

    private HBox createNavbar() {
        HBox navbar = new HBox(20);
        navbar.setPadding(new Insets(10));
        navbar.setStyle("-fx-background-color: #2c3e50;");

        Label title = new Label("POS System");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = new Button("Logout");

        navbar.getChildren().addAll(title, spacer, logoutBtn);
        return navbar;
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(15));
        sidebar.setStyle("-fx-background-color: #34495e;");

        Button dashboardBtn = new Button("Dashboard");
        Button customersBtn = new Button("Customers");
        Button ordersBtn = new Button("Orders");
        Button salesBtn = new Button("Sales");
        Button productsBtn = new Button("Products");
        Button reportsBtn = new Button("Reports");

        dashboardBtn.setOnAction(e -> setCenter(new Dashboard().getView()));
        customersBtn.setOnAction(e -> setCenter(new Customers().getView()));
        ordersBtn.setOnAction(e -> setCenter(new Orders().getView()));
        salesBtn.setOnAction(e -> setCenter(new Sales().getView()));
        productsBtn.setOnAction(e -> setCenter(new Products().getView()));
        reportsBtn.setOnAction(e -> setCenter(new Reports().getView()));

        sidebar.getChildren().addAll(
                dashboardBtn,
                customersBtn,
                ordersBtn,
                salesBtn,
                productsBtn,
                reportsBtn
        );

        return sidebar;
    }

    private void setCenter(Pane pane) {
        root.setCenter(pane);
    }

    public static void main(String[] args) {
        launch();
    }


}