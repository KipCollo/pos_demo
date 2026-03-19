package com.kipcollo.demo;

import javafx.geometry.Insets;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Reports {

    private final VBox view;
    private final DatabaseManager db = DatabaseManager.getInstance();

    public Reports() {
        view = new VBox(20);
        view.setPadding(new Insets(24));
        view.setStyle("-fx-background-color: #ecf0f1;");

        Label header = new Label("📈 Reports & Analytics");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        HBox cards = new HBox(20,
                createCard("💰 Total Revenue",    String.format("KES %.2f", db.getTotalRevenue()), "#27ae60"),
                createCard("🛒 Transactions",     String.valueOf(db.getTotalTransactions()), "#2980b9"),
                createCard("📦 Products in Stock", String.valueOf(db.getTotalProducts()), "#8e44ad"),
                createCard("📂 Categories",       "5", "#e67e22")
        );

        HBox charts = new HBox(20, createSalesBarChart(), createCategoryPieChart());

        VBox stockSection = buildLowStockSection();

        VBox recentSection = buildRecentTransactionsSection();

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox inner = new VBox(20, charts, stockSection, recentSection);
        scroll.setContent(inner);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        view.getChildren().addAll(header, cards, scroll);
    }

    public VBox getView() { return view; }

    private VBox createCard(String title, String value, String color) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(16));
        card.setPrefWidth(200);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; " +
                "-fx-border-color: " + color + "; -fx-border-width: 0 0 0 4; -fx-border-radius: 0 8 8 0;");
        Label t = new Label(title);
        t.setFont(Font.font("Arial", 11));
        t.setTextFill(Color.web("#7f8c8d"));
        Label v = new Label(value);
        v.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        v.setTextFill(Color.web(color));
        card.getChildren().addAll(t, v);
        return card;
    }

    private BarChart<String, Number> createSalesBarChart() {
        CategoryAxis x = new CategoryAxis();
        NumberAxis y = new NumberAxis();
        x.setLabel("Month");
        y.setLabel("KES");
        BarChart<String, Number> chart = new BarChart<>(x, y);
        chart.setTitle("Monthly Sales (KES)");
        chart.setPrefWidth(460);
        chart.setPrefHeight(300);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Revenue");
        series.getData().add(new XYChart.Data<>("Jan", 42000));
        series.getData().add(new XYChart.Data<>("Feb", 58000));
        series.getData().add(new XYChart.Data<>("Mar", 51000));
        series.getData().add(new XYChart.Data<>("Apr", 73000));
        series.getData().add(new XYChart.Data<>("May", 65000));
        series.getData().add(new XYChart.Data<>("Jun", 80000));
        chart.getData().add(series);
        return chart;
    }

    private PieChart createCategoryPieChart() {
        PieChart chart = new PieChart();
        chart.setTitle("Sales by Product Category");
        chart.setPrefWidth(420);
        chart.setPrefHeight(300);
        chart.getData().addAll(
                new PieChart.Data("Furniture", 22),
                new PieChart.Data("Construction Tools", 28),
                new PieChart.Data("Hardware Supplies", 18),
                new PieChart.Data("Electrical", 20),
                new PieChart.Data("Plumbing", 12)
        );
        return chart;
    }

    private VBox buildLowStockSection() {
        VBox box = new VBox(8);
        Label title = new Label("⚠ Low Stock Products (< 15 units)");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        TableView<Product> table = new TableView<>();
        table.setPrefHeight(180);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPlaceholder(new Label("All products have sufficient stock ✓"));

        TableColumn<Product, String> nameCol = new TableColumn<>("Product");
        nameCol.setCellValueFactory(d -> d.getValue().nameProperty());

        TableColumn<Product, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(d -> d.getValue().categoryProperty());

        TableColumn<Product, Number> stockCol = new TableColumn<>("Stock");
        stockCol.setCellValueFactory(d -> d.getValue().stockProperty());
        stockCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                setText(item.toString());
                setTextFill(item.intValue() < 10 ? Color.web("#e74c3c") : Color.web("#e67e22"));
                setStyle("-fx-font-weight: bold;");
            }
        });

        table.getColumns().addAll(nameCol, catCol, stockCol);

        db.getAllProducts().stream()
                .filter(p -> p.getStock() < 15)
                .forEach(table.getItems()::add);

        box.getChildren().addAll(title, table);
        return box;
    }

    private VBox buildRecentTransactionsSection() {
        VBox box = new VBox(8);
        Label title = new Label("Recent Transactions");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        TableView<SaleRecord> table = new TableView<>();
        table.setPrefHeight(200);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPlaceholder(new Label("No transactions yet"));

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
        table.getItems().addAll(db.getRecentTransactions(15));

        box.getChildren().addAll(title, table);
        return box;
    }
}

