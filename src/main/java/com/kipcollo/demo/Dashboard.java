package com.kipcollo.demo;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class Dashboard {

    private final VBox view;
    private final User currentUser;
    private final DatabaseManager db = DatabaseManager.getInstance();

    public Dashboard(User currentUser) {
        this.currentUser = currentUser;
        view = new VBox(20);
        view.setPadding(new Insets(24));
        view.setStyle("-fx-background-color: #ecf0f1;");

        Label header = new Label("📊 Dashboard");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Label welcome = new Label("Welcome back, " + currentUser.getDisplayName() + "!");
        welcome.setFont(Font.font("Arial", 14));
        welcome.setTextFill(Color.web("#7f8c8d"));

        HBox cards = buildStatCards();

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox inner = new VBox(20);
        inner.getChildren().addAll(
                new HBox(20, createBarChart(), createPieChart()),
                buildRecentSalesTable()
        );
        scroll.setContent(inner);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        view.getChildren().addAll(header, welcome, cards, scroll);
    }

    public VBox getView() { return view; }

    private HBox buildStatCards() {
        double revenue = db.getTotalRevenue();
        int txCount   = db.getTotalTransactions();
        int products  = db.getTotalProducts();

        HBox cards = new HBox(20,
                createCard("💰 Total Revenue", String.format("KES %.2f", revenue), "#27ae60"),
                createCard("🛒 Total Transactions", String.valueOf(txCount), "#2980b9"),
                createCard("📦 Total Products", String.valueOf(products), "#8e44ad"),
                createCard("👤 Logged In As", currentUser.getRole().toUpperCase(), "#e67e22")
        );
        cards.setPadding(new Insets(0, 0, 8, 0));
        return cards;
    }

    private VBox createCard(String title, String value, String color) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(16));
        card.setPrefWidth(200);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; " +
                "-fx-border-color: " + color + "; -fx-border-width: 0 0 0 4; -fx-border-radius: 0 8 8 0;");

        Label t = new Label(title);
        t.setFont(Font.font("Arial", 12));
        t.setTextFill(Color.web("#7f8c8d"));

        Label v = new Label(value);
        v.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        v.setTextFill(Color.web(color));

        card.getChildren().addAll(t, v);
        return card;
    }

    private BarChart<String, Number> createBarChart() {
        CategoryAxis x = new CategoryAxis();
        NumberAxis y = new NumberAxis();
        x.setLabel("Day");
        y.setLabel("KES");
        BarChart<String, Number> chart = new BarChart<>(x, y);
        chart.setTitle("Weekly Sales Trend");
        chart.setPrefWidth(460);
        chart.setPrefHeight(280);

        XYChart.Series<String, Number> s = new XYChart.Series<>();
        s.setName("Sales (KES)");
        s.getData().add(new XYChart.Data<>("Mon", 12000));
        s.getData().add(new XYChart.Data<>("Tue", 18500));
        s.getData().add(new XYChart.Data<>("Wed", 15000));
        s.getData().add(new XYChart.Data<>("Thu", 22000));
        s.getData().add(new XYChart.Data<>("Fri", 30000));
        s.getData().add(new XYChart.Data<>("Sat", 25000));
        chart.getData().add(s);
        return chart;
    }

    private PieChart createPieChart() {
        PieChart chart = new PieChart();
        chart.setTitle("Sales by Category");
        chart.setPrefWidth(420);
        chart.setPrefHeight(280);
        chart.getData().addAll(
                new PieChart.Data("Furniture", 22),
                new PieChart.Data("Construction Tools", 28),
                new PieChart.Data("Hardware Supplies", 18),
                new PieChart.Data("Electrical", 20),
                new PieChart.Data("Plumbing", 12)
        );
        return chart;
    }

    private VBox buildRecentSalesTable() {
        VBox box = new VBox(8);
        Label title = new Label("Recent Transactions");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        TableView<SaleRecord> table = new TableView<>();
        table.setPrefHeight(200);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<SaleRecord, String> dateCol = new TableColumn<>("Date/Time");
        dateCol.setCellValueFactory(d -> d.getValue().productProperty());

        TableColumn<SaleRecord, String> cashierCol = new TableColumn<>("Cashier");
        cashierCol.setCellValueFactory(d -> d.getValue().customerProperty());

        TableColumn<SaleRecord, Number> amtCol = new TableColumn<>("Amount (KES)");
        amtCol.setCellValueFactory(d -> d.getValue().amountProperty());

        table.getColumns().addAll(dateCol, cashierCol, amtCol);

        List<SaleRecord> records = db.getRecentTransactions(10);
        table.getItems().addAll(records);
        if (records.isEmpty()) {
            table.setPlaceholder(new Label("No transactions yet"));
        }

        box.getChildren().addAll(title, table);
        return box;
    }
}


