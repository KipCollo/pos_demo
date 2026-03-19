package com.kipcollo.demo;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class Products {
    private final VBox view;

//    public Products() {
//        view = new VBox(15);
//        view.setPadding(new Insets(20));
//
//        Label title = new Label("Products");
//
//        TextField name = new TextField();
//        name.setPromptText("Product Name");
//
//        TextField price = new TextField();
//        price.setPromptText("Price");
//
//        Button add = new Button("Add Product");
//
//        TableView<String> table = new TableView<>();
//
//        view.getChildren().addAll(title, name, price, add, table);
//    }

//    public VBox getView() {
//        return view;
//    }
public Products() {
    view = new VBox(15);
    view.setPadding(new Insets(20));

    Label title = new Label("Products");

    Button addBtn = new Button("Add Product");

    TableView<String> table = new TableView<>();
    table.setPlaceholder(new Label("Sample products loaded"));

    addBtn.setOnAction(e -> showForm());

    view.getChildren().addAll(title, addBtn, table);
}

    private void showForm() {
        view.getChildren().clear();

        Label title = new Label("Add Product");

        TextField name = new TextField();
        name.setPromptText("Product Name");

        TextField price = new TextField();
        price.setPromptText("Price");

        Button save = new Button("Save");
        Button back = new Button("Back");

        back.setOnAction(e -> view.getChildren().setAll(new Products().getView().getChildren()));

        view.getChildren().addAll(title, name, price, save, back);
    }

    public VBox getView() { return view; }


//    public Products() {
//        view = new VBox(15);
//        view.setPadding(new Insets(20));
//
//        Label title = new Label("Products");
//
//        TableView<String> table = new TableView<>();
//        table.setPlaceholder(new Label("Sample products loaded"));
//
//        view.getChildren().addAll(title, table);
//    }
//
//    public VBox getView() {
//        return view;
//    }
}
