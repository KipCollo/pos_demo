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

import java.util.List;

public class Products {

    private static final String[] CATEGORIES = {
            "All", "Furniture", "Construction Tools", "Hardware Supplies", "Electrical", "Plumbing"
    };

    private final VBox view;
    private final User currentUser;
    private final DatabaseManager db = DatabaseManager.getInstance();

    private final TableView<Product> table = new TableView<>();
    private final ObservableList<Product> productList = FXCollections.observableArrayList();
    private final TextField searchField = new TextField();
    private final ComboBox<String> categoryFilter = new ComboBox<>();

    public Products(User currentUser) {
        this.currentUser = currentUser;

        view = new VBox(16);
        view.setPadding(new Insets(24));
        view.setStyle("-fx-background-color: #ecf0f1;");

        Label header = new Label("📦 Products");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        HBox toolbar = buildToolbar();
        buildTable();
        loadProducts("All", "");

        VBox.setVgrow(table, Priority.ALWAYS);
        view.getChildren().addAll(header, toolbar, table);
    }

    public VBox getView() { return view; }

    // ── Toolbar ───────────────────────────────────────────────────────────────

    private HBox buildToolbar() {
        HBox bar = new HBox(12);
        bar.setAlignment(Pos.CENTER_LEFT);

        searchField.setPromptText("🔍  Search products...");
        searchField.setPrefWidth(220);
        searchField.setStyle("-fx-background-radius: 5; -fx-padding: 8;");
        searchField.textProperty().addListener((obs, o, n) -> applyFilter());

        categoryFilter.getItems().addAll(CATEGORIES);
        categoryFilter.setValue("All");
        categoryFilter.setStyle("-fx-background-radius: 5;");
        categoryFilter.setOnAction(e -> applyFilter());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        bar.getChildren().addAll(new Label("Category:"), categoryFilter, searchField, spacer);

        if (currentUser.isAdmin()) {
            Button addBtn = new Button("＋ Add Product");
            addBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 8 14 8 14;");
            addBtn.setOnAction(e -> showProductForm(null));
            bar.getChildren().add(addBtn);
        }

        return bar;
    }

    // ── Table ─────────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private void buildTable() {
        table.setItems(productList);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setStyle("-fx-background-color: white;");
        table.setPlaceholder(new Label("No products found"));

        TableColumn<Product, Number> idCol = new TableColumn<>("#");
        idCol.setCellValueFactory(d -> d.getValue().idProperty());
        idCol.setMaxWidth(50);

        TableColumn<Product, String> nameCol = new TableColumn<>("Product Name");
        nameCol.setCellValueFactory(d -> d.getValue().nameProperty());

        TableColumn<Product, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(d -> d.getValue().categoryProperty());

        TableColumn<Product, String> barcodeCol = new TableColumn<>("Barcode");
        barcodeCol.setCellValueFactory(d -> d.getValue().barcodeProperty());

        TableColumn<Product, Number> priceCol = new TableColumn<>("Price (KES)");
        priceCol.setCellValueFactory(d -> d.getValue().priceProperty());
        priceCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("%.2f", item.doubleValue()));
            }
        });

        TableColumn<Product, Number> stockCol = new TableColumn<>("Stock");
        stockCol.setCellValueFactory(d -> d.getValue().stockProperty());
        stockCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item.toString());
                int stock = item.intValue();
                setTextFill(stock < 10 ? Color.web("#e74c3c") : stock < 25 ? Color.web("#e67e22") : Color.web("#27ae60"));
            }
        });

        TableColumn<Product, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(d -> d.getValue().descriptionProperty());

        table.getColumns().addAll(idCol, nameCol, catCol, barcodeCol, priceCol, stockCol, descCol);

        if (currentUser.isAdmin()) {
            TableColumn<Product, Void> actionCol = new TableColumn<>("Actions");
            actionCol.setCellFactory(col -> new TableCell<>() {
                private final Button editBtn   = new Button("✏ Edit");
                private final Button deleteBtn = new Button("🗑 Delete");
                private final HBox box = new HBox(6, editBtn, deleteBtn);

                {
                    editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 4; -fx-font-size: 11;");
                    deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 4; -fx-font-size: 11;");
                    editBtn.setOnAction(e -> showProductForm(getTableView().getItems().get(getIndex())));
                    deleteBtn.setOnAction(e -> confirmDelete(getTableView().getItems().get(getIndex())));
                }

                @Override protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : box);
                }
            });
            table.getColumns().add(actionCol);
        }
    }

    // ── Data Loading ──────────────────────────────────────────────────────────

    private void loadProducts(String category, String query) {
        productList.clear();
        List<Product> results;
        if (query != null && !query.isBlank()) {
            results = db.searchProducts(query);
        } else if (category == null || category.equals("All")) {
            results = db.getAllProducts();
        } else {
            results = db.getProductsByCategory(category);
        }
        productList.addAll(results);
    }

    private void applyFilter() {
        loadProducts(categoryFilter.getValue(), searchField.getText().trim());
    }

    // ── Product Form ──────────────────────────────────────────────────────────

    private void showProductForm(Product product) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(product == null ? "Add Product" : "Edit Product");
        dialog.setHeaderText(null);

        ButtonType saveType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField     = new TextField(product != null ? product.getName() : "");
        ComboBox<String> catBox = new ComboBox<>(FXCollections.observableArrayList(
                "Furniture", "Construction Tools", "Hardware Supplies", "Electrical", "Plumbing"));
        catBox.setValue(product != null ? product.getCategory() : "Furniture");
        TextField barcodeField  = new TextField(product != null ? product.getBarcode() : "");
        TextField priceField    = new TextField(product != null ? String.valueOf(product.getPrice()) : "");
        TextField stockField    = new TextField(product != null ? String.valueOf(product.getStock()) : "");
        TextField descField     = new TextField(product != null ? product.getDescription() : "");

        grid.addRow(0, new Label("Name:"),     nameField);
        grid.addRow(1, new Label("Category:"), catBox);
        grid.addRow(2, new Label("Barcode:"),  barcodeField);
        grid.addRow(3, new Label("Price:"),    priceField);
        grid.addRow(4, new Label("Stock:"),    stockField);
        grid.addRow(5, new Label("Desc:"),     descField);

        Label errorLbl = new Label();
        errorLbl.setTextFill(Color.web("#e74c3c"));
        grid.add(errorLbl, 0, 6, 2, 1);

        dialog.getDialogPane().setContent(grid);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveType);
        saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, ev -> {
            try {
                String name     = nameField.getText().trim();
                String cat      = catBox.getValue();
                String barcode  = barcodeField.getText().trim();
                double price    = Double.parseDouble(priceField.getText().trim());
                int stock       = Integer.parseInt(stockField.getText().trim());
                String desc     = descField.getText().trim();

                if (name.isEmpty() || barcode.isEmpty()) throw new IllegalArgumentException("Name and barcode are required.");
                if (price < 0) throw new IllegalArgumentException("Price cannot be negative.");
                if (stock < 0) throw new IllegalArgumentException("Stock cannot be negative.");

                boolean ok;
                if (product == null) {
                    ok = db.addProduct(name, cat, barcode, price, stock, desc);
                } else {
                    ok = db.updateProduct(product.getId(), name, cat, barcode, price, stock, desc);
                }

                if (!ok) {
                    errorLbl.setText("Save failed. Barcode may already exist.");
                    ev.consume();
                } else {
                    applyFilter();
                }
            } catch (NumberFormatException ex) {
                errorLbl.setText("Invalid price or stock value.");
                ev.consume();
            } catch (IllegalArgumentException ex) {
                errorLbl.setText(ex.getMessage());
                ev.consume();
            }
        });

        dialog.showAndWait();
    }

    private void confirmDelete(Product product) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete \"" + product.getName() + "\"?", ButtonType.YES, ButtonType.NO);
        alert.setHeaderText("Confirm Deletion");
        alert.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                db.deleteProduct(product.getId());
                applyFilter();
            }
        });
    }
}

