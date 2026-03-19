package com.kipcollo.demo;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class Orders {
    private final VBox view;

    public Orders() {
        view = new VBox(15);
        view.setPadding(new Insets(20));

        Label title = new Label("Orders");

        TableView<String> table = new TableView<>();
        table.setPlaceholder(new Label("No orders available"));

        view.getChildren().addAll(title, table);
    }

    public VBox getView() {
        return view;
    }
}