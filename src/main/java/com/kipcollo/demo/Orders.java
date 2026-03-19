package com.kipcollo.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class Orders {

    private final VBox view;
    private final DatabaseManager db = DatabaseManager.getInstance();

    public Orders() {
        view = new VBox(16);
        view.setPadding(new Insets(24));
        view.setStyle("-fx-background-color: #ecf0f1;");

        Label header = new Label("📋 Orders");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // Summary row
        HBox summaryRow = new HBox(20);
        summaryRow.getChildren().addAll(
                summaryCard("Total Orders", String.valueOf(db.getTotalTransactions()), "#2980b9"),
                summaryCard("Total Revenue", String.format("KES %.2f", db.getTotalRevenue()), "#27ae60")
        );

        // Orders table (sourced from DB transactions)
        TableView<SaleRecord> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPlaceholder(new Label("No orders found"));
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<SaleRecord, String> dateCol = new TableColumn<>("Date/Time");
        dateCol.setCellValueFactory(d -> d.getValue().productProperty());

        TableColumn<SaleRecord, String> cashierCol = new TableColumn<>("Cashier");
        cashierCol.setCellValueFactory(d -> d.getValue().customerProperty());

        TableColumn<SaleRecord, Number> amtCol = new TableColumn<>("Amount (KES)");
        amtCol.setCellValueFactory(d -> d.getValue().amountProperty());
        amtCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("%.2f", item.doubleValue()));
            }
        });

        table.getColumns().addAll(dateCol, cashierCol, amtCol);

        List<SaleRecord> records = db.getRecentTransactions(50);
        table.getItems().addAll(records);

        view.getChildren().addAll(header, summaryRow, table);
    }

    private VBox summaryCard(String title, String value, String color) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(14));
        card.setPrefWidth(200);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; " +
                "-fx-border-color: " + color + "; -fx-border-width: 0 0 0 4; -fx-border-radius: 0 8 8 0;");
        Label t = new Label(title);
        t.setFont(Font.font("Arial", 11));
        t.setTextFill(Color.web("#7f8c8d"));
        Label v = new Label(value);
        v.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        v.setTextFill(Color.web(color));
        card.getChildren().addAll(t, v);
        return card;
    }

    public VBox getView() { return view; }
}
