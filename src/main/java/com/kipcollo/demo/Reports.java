package com.kipcollo.demo;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class Reports {
    private final VBox view;

    public Reports() {
        view = new VBox(20);
        view.setPadding(new Insets(20));

        Label title = new Label("Reports");
        Label info = new Label("Analytics and reports will appear here");

        view.getChildren().addAll(title, info);
    }

    public VBox getView() {
        return view;
    }
}
