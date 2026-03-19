package com.kipcollo.demo;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class Sales {
    private final VBox view;

//    public Sales() {
//        view = new VBox(15);
//        view.setPadding(new Insets(20));
//
//        Label title = new Label("Sales");
//
//        TextField product = new TextField();
//        product.setPromptText("Product Name");
//
//        TextField quantity = new TextField();
//        quantity.setPromptText("Quantity");
//
//        Button add = new Button("Add to Cart");
//
//        TableView<String> table = new TableView<>();
//        table.setPlaceholder(new Label("No items"));
//
//        Button checkout = new Button("Checkout");
//
//        view.getChildren().addAll(title, product, quantity, add, table, checkout);
//    }

//    public Sales() {
//        view = new VBox(15);
//        view.setPadding(new Insets(20));
//
//        Label title = new Label("Sales");
//
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
//        table.getItems().addAll(
//                new SaleRecord("Bread", "Jane", 100),
//                new SaleRecord("TV", "Mike", 40000)
//        );
//
//        view.getChildren().addAll(title, table);
//    }
//
//    public VBox getView() {
//        return view;
//    }

    public Sales() {
        view = new VBox(15);
        view.setPadding(new Insets(20));

        Label title = new Label("Sales");

        Button addBtn = new Button("New Sale");

        TableView<SaleRecord> table = new TableView<>();

        TableColumn<SaleRecord, String> p = new TableColumn<>("Product");
        p.setCellValueFactory(d -> d.getValue().productProperty());

        TableColumn<SaleRecord, String> c = new TableColumn<>("Customer");
        c.setCellValueFactory(d -> d.getValue().customerProperty());

        TableColumn<SaleRecord, Number> a = new TableColumn<>("Amount");
        a.setCellValueFactory(d -> d.getValue().amountProperty());

        table.getColumns().addAll(p, c, a);

        table.getItems().addAll(
                new SaleRecord("Bread", "Jane", 100),
                new SaleRecord("TV", "Mike", 40000)
        );

        addBtn.setOnAction(e -> showForm());

        view.getChildren().addAll(title, addBtn, table);
    }

    private void showForm() {
        view.getChildren().clear();

        Label title = new Label("Create Sale");

        TextField product = new TextField();
        product.setPromptText("Product");

        TextField qty = new TextField();
        qty.setPromptText("Quantity");

        Button save = new Button("Add");
        Button back = new Button("Back");

        back.setOnAction(e -> view.getChildren().setAll(new Sales().getView().getChildren()));

        view.getChildren().addAll(title, product, qty, save, back);
    }

    public VBox getView() { return view; }
}
