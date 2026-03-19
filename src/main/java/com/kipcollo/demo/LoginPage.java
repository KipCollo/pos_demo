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

public class LoginPage {

    private final Stage stage;

    public LoginPage(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(60));
        root.setStyle("-fx-background-color: #1a252f;");

        // Shop header
        Label shopName = new Label("🔧 HardwareHub POS");
        shopName.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        shopName.setTextFill(Color.WHITE);

        Label subtitle = new Label("Hardware Shop Point of Sale System");
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setTextFill(Color.web("#95a5a6"));

        // Login card
        VBox card = new VBox(16);
        card.setPadding(new Insets(30));
        card.setMaxWidth(380);
        card.setStyle("-fx-background-color: #2c3e50; -fx-background-radius: 10;");

        Label loginLabel = new Label("Sign In");
        loginLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        loginLabel.setTextFill(Color.WHITE);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-prompt-text-fill: #7f8c8d; -fx-background-radius: 5; -fx-padding: 10;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-prompt-text-fill: #7f8c8d; -fx-background-radius: 5; -fx-padding: 10;");

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.web("#e74c3c"));
        errorLabel.setFont(Font.font("Arial", 12));

        Button loginBtn = new Button("LOGIN");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 12;");

        // Hint label
        Label hint = new Label("Demo: admin/admin123 · cashier1/cash1pass · cashier2/cash2pass");
        hint.setFont(Font.font("Arial", 11));
        hint.setTextFill(Color.web("#7f8c8d"));
        hint.setWrapText(true);

        loginBtn.setOnAction(e -> {
            String user = usernameField.getText().trim();
            String pass = passwordField.getText().trim();
            if (user.isEmpty() || pass.isEmpty()) {
                errorLabel.setText("Please enter username and password.");
                return;
            }
            User loggedIn = DatabaseManager.getInstance().authenticate(user, pass);
            if (loggedIn == null) {
                errorLabel.setText("Invalid username or password.");
                passwordField.clear();
            } else {
                openMainApp(loggedIn);
            }
        });

        // Allow Enter key to submit
        passwordField.setOnAction(e -> loginBtn.fire());

        card.getChildren().addAll(loginLabel, usernameField, passwordField, errorLabel, loginBtn, hint);

        root.getChildren().addAll(shopName, subtitle, card);

        Scene scene = new Scene(root, 520, 560);
        stage.setTitle("HardwareHub POS – Login");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private void openMainApp(User user) {
        POSHomePage homePage = new POSHomePage(user);
        homePage.show(stage);
    }
}
