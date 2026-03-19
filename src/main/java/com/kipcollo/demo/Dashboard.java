package com.kipcollo.demo;

import javafx.geometry.Insets;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Dashboard {

//    private final VBox view;
//
//    public Dashboard() {
//        view = new VBox(20);
//        view.setPadding(new Insets(20));
//
//        Label header = new Label("Dashboard Analytics");
//        header.setStyle("-fx-font-size: 22px;");
//
//        HBox cards = new HBox(20);
//        cards.getChildren().addAll(
//                createCard("Total Sales", "KES 45,000"),
//                createCard("Products", "120"),
//                createCard("Customers", "85")
//        );
//
//        LineChart<String, Number> lineChart = createLineChart();
//        PieChart pieChart = createPieChart();
//
//        HBox charts = new HBox(20, lineChart, pieChart);
//
//        view.getChildren().addAll(header, cards, charts);
//    }
//
//    public VBox getView() {
//        return view;
//    }
//
//    private VBox createCard(String title, String value) {
//        VBox card = new VBox(10);
//        card.setPadding(new Insets(15));
//        card.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 5;");
//
//        Label t = new Label(title);
//        Label v = new Label(value);
//        v.setStyle("-fx-font-size: 18px;");
//
//        card.getChildren().addAll(t, v);
//        return card;
//    }
//
//
//
//    private PieChart createPieChart() {
//        PieChart chart = new PieChart();
//        chart.setTitle("Product Categories");
//
//        chart.getData().add(new PieChart.Data("Electronics", 30));
//        chart.getData().add(new PieChart.Data("Groceries", 40));
//        chart.getData().add(new PieChart.Data("Clothing", 20));
//        chart.getData().add(new PieChart.Data("Others", 10));
//
//        return chart;
//    }
//
//    private LineChart<String, Number> createLineChart() {
//        CategoryAxis xAxis = new CategoryAxis();
//        NumberAxis yAxis = new NumberAxis();
//
//        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
//        chart.setTitle("Weekly Sales Trend");
//
//        XYChart.Series<String, Number> series = new XYChart.Series<>();
//
//        series.getData().add(new XYChart.Data<>("Mon", 5000));
//        series.getData().add(new XYChart.Data<>("Tue", 7000));
//        series.getData().add(new XYChart.Data<>("Wed", 6000));
//        series.getData().add(new XYChart.Data<>("Thu", 8000));
//        series.getData().add(new XYChart.Data<>("Fri", 10000));
//
//        chart.getData().add(series);
//        return chart;
//    }
//
//
//    private TableView<SaleRecord> createRecentSalesTable() {
//        TableView<SaleRecord> table = new TableView<>();
//
//        TableColumn<SaleRecord, String> productCol = new TableColumn<>("Product");
//        productCol.setCellValueFactory(data -> data.getValue().productProperty());
//
//        TableColumn<SaleRecord, String> customerCol = new TableColumn<>("Customer");
//        customerCol.setCellValueFactory(data -> data.getValue().customerProperty());
//
//        TableColumn<SaleRecord, Number> amountCol = new TableColumn<>("Amount");
//        amountCol.setCellValueFactory(data -> data.getValue().amountProperty());
//
//        table.getColumns().addAll(productCol, customerCol, amountCol);
//
//        // Dummy data
//        table.getItems().addAll(
//                new SaleRecord("Laptop", "John", 50000),
//                new SaleRecord("Milk", "Alice", 200),
//                new SaleRecord("Shirt", "Bob", 1500),
//                new SaleRecord("Phone", "Eve", 30000)
//        );
//
//        return table;
//    }

    private final VBox view;

    public Dashboard() {
        view = new VBox(20);
        view.setPadding(new Insets(20));

        Label header = new Label("Dashboard Analytics");
        header.setStyle("-fx-font-size: 22px;");

        HBox cards = new HBox(20,
                createCard("Total Sales", "KES 45,000"),
                createCard("Products", "120"),
                createCard("Customers", "85")
        );

        HBox charts = new HBox(20, createLineChart(), createBarChart());

        HBox tables = new HBox(20, createCustomersTable(), createOrdersTable());

        view.getChildren().addAll(header, cards, charts, tables);
    }

    public VBox getView() { return view; }

    private VBox createCard(String title, String value) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 5;");
        card.getChildren().addAll(new Label(title), new Label(value));
        return card;
    }

    private LineChart<String, Number> createLineChart() {
        CategoryAxis x = new CategoryAxis();
        NumberAxis y = new NumberAxis();
        LineChart<String, Number> chart = new LineChart<>(x, y);
        chart.setTitle("Sales Trend");

        XYChart.Series<String, Number> s = new XYChart.Series<>();
        s.getData().add(new XYChart.Data<>("Mon", 5000));
        s.getData().add(new XYChart.Data<>("Tue", 7000));
        s.getData().add(new XYChart.Data<>("Wed", 6000));
        s.getData().add(new XYChart.Data<>("Thu", 8000));
        s.getData().add(new XYChart.Data<>("Fri", 10000));

        chart.getData().add(s);
        return chart;
    }

    private BarChart<String, Number> createBarChart() {
        CategoryAxis x = new CategoryAxis();
        NumberAxis y = new NumberAxis();
        BarChart<String, Number> chart = new BarChart<>(x, y);
        chart.setTitle("Product Sales");

        XYChart.Series<String, Number> s = new XYChart.Series<>();
        s.getData().add(new XYChart.Data<>("Electronics", 30));
        s.getData().add(new XYChart.Data<>("Groceries", 50));
        s.getData().add(new XYChart.Data<>("Clothing", 20));

        chart.getData().add(s);
        return chart;

    }

    private TableView<String> createCustomersTable() {
        TableView<String> table = new TableView<>();
        table.setPlaceholder(new Label("Customers"));
        table.getItems().addAll("John", "Alice", "Bob");
        return table;
    }

    private TableView<String> createOrdersTable() {
        TableView<String> table = new TableView<>();
        table.setPlaceholder(new Label("Orders"));
        table.getItems().addAll("Order1", "Order2", "Order3");
        return table;
    }
}

