package com.kipcollo.demo;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class Customers {
    private final VBox view;
//
//    public Customers() {
//        view = new VBox(15);
//        view.setPadding(new Insets(20));
//
//        Label title = new Label("Customers");
//
//        TextField name = new TextField();
//        name.setPromptText("Name");
//
//        TextField phone = new TextField();
//        phone.setPromptText("Phone");
//
//        Button add = new Button("Add Customer");
//
//        view.getChildren().addAll(title, name, phone, add);
//    }
//
//    public VBox getView() {
//        return view;
//    }

//    public Customers() {
//        view = new VBox(15);
//        view.setPadding(new Insets(20));
//
//        Label title = new Label("Customers");
//
//        ListView<String> list = new ListView<>();
//        list.getItems().addAll("John", "Alice", "Bob", "Eve");
//
//        view.getChildren().addAll(title, list);
//    }
//
//    public VBox getView() {
//        return view;
//    }

    public Customers() {
        view = new VBox(15);
        view.setPadding(new Insets(20));

        Label title = new Label("Customers");

        Button addBtn = new Button("Add Customer");

        ListView<String> list = new ListView<>();
        list.getItems().addAll("John", "Alice", "Bob", "Eve");

        addBtn.setOnAction(e -> showForm());

        view.getChildren().addAll(title, addBtn, list);
    }

    private void showForm() {
        view.getChildren().clear();

        Label title = new Label("Add Customer");

        TextField name = new TextField();
        name.setPromptText("Name");

        TextField phone = new TextField();
        phone.setPromptText("Phone");

        Button save = new Button("Save");
        Button back = new Button("Back");

        back.setOnAction(e -> view.getChildren().setAll(new Customers().getView().getChildren()));

        view.getChildren().addAll(title, name, phone, save, back);
    }

    public VBox getView() { return view; }
}
