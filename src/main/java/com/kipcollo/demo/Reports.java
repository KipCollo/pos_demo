package com.kipcollo.demo;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.chart.*;

public class Reports {
    private final VBox view;

    public Reports() {
        view = new VBox(20);
        view.setPadding(new Insets(20));

        Label header = new Label("Reports Dashboard");
        header.setStyle("-fx-font-size: 22px;");

        // Sales summary card
        VBox salesCard = createCard("Total Sales", "KES 120,000");
        VBox productsCard = createCard("Total Products", "150");
        VBox customersCard = createCard("Total Customers", "90");

        HBox cards = new HBox(20, salesCard, productsCard, customersCard);

        // Charts
        HBox charts = new HBox(20, createSalesBarChart(), createCategoryPieChart());

        // Recent Orders Table
        TableView<String> ordersTable = new TableView<>();
        ordersTable.setPlaceholder(new Label("Recent orders will appear here"));
        ordersTable.getItems().addAll("Order001", "Order002", "Order003");

        // Customer Feedback Table
        TableView<String> feedbackTable = new TableView<>();
        feedbackTable.setPlaceholder(new Label("Customer feedback"));
        feedbackTable.getItems().addAll("Good service", "Late delivery", "Excellent support");

        HBox tables = new HBox(20, ordersTable, feedbackTable);

        view.getChildren().addAll(header, cards, charts, tables);
    }

    public VBox getView() { return view; }

    private VBox createCard(String title, String value) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 5;");
        Label t = new Label(title);
        Label v = new Label(value);
        v.setStyle("-fx-font-size: 18px;");
        card.getChildren().addAll(t, v);
        return card;
    }

    private BarChart<String, Number> createSalesBarChart() {
        CategoryAxis x = new CategoryAxis();
        NumberAxis y = new NumberAxis();
        BarChart<String, Number> chart = new BarChart<>(x, y);
        chart.setTitle("Monthly Sales");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Jan", 20000));
        series.getData().add(new XYChart.Data<>("Feb", 30000));
        series.getData().add(new XYChart.Data<>("Mar", 25000));
        series.getData().add(new XYChart.Data<>("Apr", 35000));

        chart.getData().add(series);
        return chart;
    }

    private PieChart createCategoryPieChart() {
        PieChart chart = new PieChart();
        chart.getData().add(new PieChart.Data("Electronics", 40));
        chart.getData().add(new PieChart.Data("Groceries", 35));
        chart.getData().add(new PieChart.Data("Clothing", 15));
        chart.getData().add(new PieChart.Data("Others", 10));
        return chart;
    }
}
