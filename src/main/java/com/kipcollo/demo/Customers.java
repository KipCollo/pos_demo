package com.kipcollo.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Customers {

    private final VBox view;

    public Customers() {
        view = new VBox(16);
        view.setPadding(new Insets(24));
        view.setStyle("-fx-background-color: #ecf0f1;");

        Label header = new Label("👥 Customers");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("🔍  Search customers...");
        searchField.setPrefWidth(220);
        searchField.setStyle("-fx-background-radius: 5; -fx-padding: 8;");

        toolbar.getChildren().add(searchField);

        // Sample customer table
        TableView<String[]> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<String[], String> idCol    = new TableColumn<>("#");
        idCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[0]));
        idCol.setMaxWidth(50);

        TableColumn<String[], String> nameCol  = new TableColumn<>("Name");
        nameCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[1]));

        TableColumn<String[], String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[2]));

        TableColumn<String[], String> txCol    = new TableColumn<>("Transactions");
        txCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[3]));

        TableColumn<String[], String> totalCol = new TableColumn<>("Total Spent (KES)");
        totalCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[4]));

        table.getColumns().addAll(idCol, nameCol, phoneCol, txCol, totalCol);

        ObservableList<String[]> data = FXCollections.observableArrayList(
                new String[]{"1", "John Kamau",    "+254 712 345678", "5", "28,500.00"},
                new String[]{"2", "Alice Wanjiku",  "+254 722 456789", "3", "14,200.00"},
                new String[]{"3", "Bob Otieno",     "+254 733 567890", "7", "55,000.00"},
                new String[]{"4", "Eve Muthoni",    "+254 744 678901", "2", "7,800.00"},
                new String[]{"5", "Charles Kiprop", "+254 755 789012", "4", "32,100.00"},
                new String[]{"6", "Faith Achieng",  "+254 766 890123", "1", "4,500.00"},
                new String[]{"7", "David Njoroge",  "+254 777 901234", "6", "41,750.00"},
                new String[]{"8", "Grace Chebet",   "+254 788 012345", "2", "9,300.00"}
        );

        table.setItems(data);

        searchField.textProperty().addListener((obs, o, n) -> {
            if (n.isBlank()) {
                table.setItems(data);
            } else {
                String lower = n.toLowerCase();
                ObservableList<String[]> filtered = FXCollections.observableArrayList();
                data.stream().filter(r -> r[1].toLowerCase().contains(lower) || r[2].contains(lower))
                        .forEach(filtered::add);
                table.setItems(filtered);
            }
        });

        view.getChildren().addAll(header, toolbar, table);
    }

    public VBox getView() { return view; }
}

