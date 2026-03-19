package com.kipcollo.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class AdminManagement {

    private final VBox view;
    private final User currentUser;
    private final DatabaseManager db = DatabaseManager.getInstance();

    private final ObservableList<User> userList = FXCollections.observableArrayList();
    private final TableView<User> userTable = new TableView<>();

    public AdminManagement(User currentUser) {
        this.currentUser = currentUser;

        view = new VBox(16);
        view.setPadding(new Insets(24));
        view.setStyle("-fx-background-color: #ecf0f1;");

        Label header = new Label("⚙ Admin Panel");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        VBox.setVgrow(tabs, Priority.ALWAYS);

        Tab usersTab = new Tab("👥 User Management", buildUsersPanel());
        Tab productsTab = new Tab("📦 Product Management", new Products(currentUser).getView());
        Tab systemTab = new Tab("ℹ System Info", buildSystemInfoPanel());

        tabs.getTabs().addAll(usersTab, productsTab, systemTab);
        view.getChildren().addAll(header, tabs);
    }

    public VBox getView() { return view; }

    // ── Users Panel ───────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private VBox buildUsersPanel() {
        VBox panel = new VBox(12);
        panel.setPadding(new Insets(16));

        // Toolbar
        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        Button addBtn = new Button("＋ Add User");
        addBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 8 14 8 14;");
        addBtn.setOnAction(e -> showUserForm(null));
        toolbar.getChildren().add(addBtn);

        // Table
        userTable.setItems(userList);
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        userTable.setPlaceholder(new Label("No users found"));
        VBox.setVgrow(userTable, Priority.ALWAYS);

        TableColumn<User, Number> idCol = new TableColumn<>("#");
        idCol.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getId()));
        idCol.setMaxWidth(50);

        TableColumn<User, String> nameCol = new TableColumn<>("Display Name");
        nameCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getDisplayName()));

        TableColumn<User, String> userCol = new TableColumn<>("Username");
        userCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getUsername()));

        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getRole()));
        roleCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setTextFill("admin".equalsIgnoreCase(item) ? Color.web("#e74c3c") : Color.web("#2980b9"));
                setStyle("-fx-font-weight: bold;");
            }
        });

        TableColumn<User, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button delBtn = new Button("🗑 Delete");
            {
                delBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 4; -fx-font-size: 11;");
                delBtn.setOnAction(e -> {
                    User u = getTableView().getItems().get(getIndex());
                    if (u.getUsername().equals(currentUser.getUsername())) {
                        new Alert(Alert.AlertType.WARNING, "You cannot delete your own account.").showAndWait();
                        return;
                    }
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "Delete user \"" + u.getUsername() + "\"?", ButtonType.YES, ButtonType.NO);
                    confirm.showAndWait().ifPresent(bt -> {
                        if (bt == ButtonType.YES) {
                            db.deleteUser(u.getId());
                            loadUsers();
                        }
                    });
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : delBtn);
            }
        });

        userTable.getColumns().addAll(idCol, nameCol, userCol, roleCol, actionCol);
        loadUsers();

        panel.getChildren().addAll(toolbar, userTable);
        return panel;
    }

    private void loadUsers() {
        userList.clear();
        List<User> users = db.getAllUsers();
        userList.addAll(users);
    }

    private void showUserForm(User user) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New User");
        dialog.setHeaderText(null);

        ButtonType saveType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField displayField = new TextField();
        displayField.setPromptText("e.g. John Doe");
        TextField usernameField = new TextField();
        usernameField.setPromptText("e.g. johndoe");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        ComboBox<String> roleBox = new ComboBox<>(FXCollections.observableArrayList(
                "admin", "cashier1", "cashier2"));
        roleBox.setValue("cashier1");

        grid.addRow(0, new Label("Display Name:"), displayField);
        grid.addRow(1, new Label("Username:"),     usernameField);
        grid.addRow(2, new Label("Password:"),     passField);
        grid.addRow(3, new Label("Role:"),         roleBox);

        Label errLbl = new Label();
        errLbl.setTextFill(Color.web("#e74c3c"));
        grid.add(errLbl, 0, 4, 2, 1);

        dialog.getDialogPane().setContent(grid);

        Button saveBtn = (Button) dialog.getDialogPane().lookupButton(saveType);
        saveBtn.addEventFilter(javafx.event.ActionEvent.ACTION, ev -> {
            String display  = displayField.getText().trim();
            String username = usernameField.getText().trim();
            String password = passField.getText().trim();
            String role     = roleBox.getValue();

            if (display.isEmpty() || username.isEmpty() || password.isEmpty()) {
                errLbl.setText("All fields are required.");
                ev.consume();
                return;
            }
            boolean ok = db.addUser(username, password, role, display);
            if (!ok) {
                errLbl.setText("Username may already exist.");
                ev.consume();
            } else {
                loadUsers();
            }
        });

        dialog.showAndWait();
    }

    // ── System Info ───────────────────────────────────────────────────────────

    private VBox buildSystemInfoPanel() {
        VBox panel = new VBox(12);
        panel.setPadding(new Insets(20));

        Label title = new Label("System Information");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(10);
        grid.setPadding(new Insets(12));
        grid.setStyle("-fx-background-color: white; -fx-background-radius: 8;");

        addInfoRow(grid, 0, "Application",    "HardwareHub POS v1.0");
        addInfoRow(grid, 1, "Java Version",   System.getProperty("java.version"));
        addInfoRow(grid, 2, "JavaFX",         System.getProperty("javafx.version", "21+"));
        addInfoRow(grid, 3, "OS",             System.getProperty("os.name"));
        addInfoRow(grid, 4, "Database",       "SQLite (pos_hardware.db)");
        addInfoRow(grid, 5, "Logged In",      currentUser.getDisplayName() + " (" + currentUser.getRole() + ")");
        addInfoRow(grid, 6, "Total Products", String.valueOf(db.getTotalProducts()));
        addInfoRow(grid, 7, "Total Tx",       String.valueOf(db.getTotalTransactions()));
        addInfoRow(grid, 8, "Total Revenue",  String.format("KES %.2f", db.getTotalRevenue()));

        panel.getChildren().addAll(title, grid);
        return panel;
    }

    private void addInfoRow(GridPane grid, int row, String key, String value) {
        Label k = new Label(key + ":");
        k.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        k.setTextFill(Color.web("#7f8c8d"));

        Label v = new Label(value);
        v.setFont(Font.font("Arial", 13));

        grid.addRow(row, k, v);
    }
}
