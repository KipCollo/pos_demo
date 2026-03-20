package com.kipcollo.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class Sales {

    private static final String[] CATEGORIES = {
            "All", "Furniture", "Construction Tools", "Hardware Supplies", "Electrical", "Plumbing"
    };

    private final VBox view;
    private final User currentUser;
    private final DatabaseManager db = DatabaseManager.getInstance();

    // Cart state
    private final ObservableList<CartItem> cartItems = FXCollections.observableArrayList();
    private final TableView<CartItem> cartTable = new TableView<>();
    private final Label totalLabel = new Label("Total: KES 0.00");

    // Right-panel catalog
    private final ObservableList<Product> catalogList = FXCollections.observableArrayList();
    private final TableView<Product> catalogTable = new TableView<>();
    private final TextField searchField = new TextField();
    private final ComboBox<String> catFilter = new ComboBox<>();
    private final TextField barcodeField = new TextField();

    public Sales(User currentUser) {
        this.currentUser = currentUser;

        view = new VBox();
        view.setStyle("-fx-background-color: #ecf0f1;");

        Label header = new Label("🛒 Sales");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        header.setPadding(new Insets(16, 24, 8, 24));

        SplitPane split = new SplitPane();
        split.setDividerPositions(0.38);
        VBox.setVgrow(split, Priority.ALWAYS);
        split.getItems().addAll(buildCartPanel(), buildCatalogPanel());

        view.getChildren().addAll(header, split);
    }

    public VBox getView() { return view; }

    // ── Left Panel – Cart ─────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private VBox buildCartPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(16));
        panel.setStyle("-fx-background-color: white;");

        Label title = new Label("Shopping Cart");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        // Cart table
        cartTable.setItems(cartItems);
        cartTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        cartTable.setPlaceholder(new Label("Cart is empty"));
        VBox.setVgrow(cartTable, Priority.ALWAYS);

        TableColumn<CartItem, String> nameCol = new TableColumn<>("Product");
        nameCol.setCellValueFactory(d -> d.getValue().getProduct().nameProperty());

        TableColumn<CartItem, Number> qtyCol = new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(d -> d.getValue().quantityProperty());
        qtyCol.setMaxWidth(50);

        TableColumn<CartItem, Number> unitCol = new TableColumn<>("Unit (KES)");
        unitCol.setCellValueFactory(d -> d.getValue().getProduct().priceProperty());
        unitCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("%.2f", item.doubleValue()));
            }
        });

        TableColumn<CartItem, Void> subCol = new TableColumn<>("Subtotal");
        subCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setText(null); return; }
                CartItem ci = getTableView().getItems().get(getIndex());
                setText(String.format("KES %.2f", ci.getSubtotal()));
            }
        });

        TableColumn<CartItem, Void> removeCol = new TableColumn<>("");
        removeCol.setMaxWidth(36);
        removeCol.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("✕");
            { btn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 4; -fx-font-size: 10; -fx-padding: 2 5 2 5;");
              btn.setOnAction(e -> { cartItems.remove(getTableView().getItems().get(getIndex())); refreshTotal(); }); }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        cartTable.getColumns().addAll(nameCol, qtyCol, unitCol, subCol, removeCol);
        cartItems.addListener((javafx.collections.ListChangeListener<CartItem>) c -> refreshTotal());

        // Total
        totalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        totalLabel.setTextFill(Color.web("#2c3e50"));

        // Cart actions
        Button clearBtn    = new Button("🗑 Clear");
        Button checkoutBtn = new Button("💳 Checkout");
        clearBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 8 14 8 14;");
        checkoutBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 8 18 8 18;");

        clearBtn.setOnAction(e -> { cartItems.clear(); refreshTotal(); });
        checkoutBtn.setOnAction(e -> showPaymentDialog());

        HBox cartActions = new HBox(10, clearBtn, new Region(), checkoutBtn);
        HBox.setHgrow(cartActions.getChildren().get(1), Priority.ALWAYS);

        panel.getChildren().addAll(title, cartTable, totalLabel, cartActions);
        return panel;
    }

    // ── Right Panel – Product Catalog ─────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private VBox buildCatalogPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(16));
        panel.setStyle("-fx-background-color: #fafafa;");

        Label title = new Label("Product Catalog");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        // Barcode scan bar
        HBox scanBar = new HBox(8);
        scanBar.setAlignment(Pos.CENTER_LEFT);
        barcodeField.setPromptText("📷  Scan or enter barcode...");
        barcodeField.setPrefWidth(240);
        barcodeField.setStyle("-fx-background-radius: 5; -fx-padding: 8;");
        Button scanBtn = new Button("Add to Cart");
        scanBtn.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 8 14 8 14;");
        barcodeField.setOnAction(e -> scanBarcode());
        scanBtn.setOnAction(e -> scanBarcode());
        scanBar.getChildren().addAll(new Label("Barcode:"), barcodeField, scanBtn);

        // Category + search
        HBox filterBar = new HBox(10);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        catFilter.getItems().addAll(CATEGORIES);
        catFilter.setValue("All");
        catFilter.setOnAction(e -> loadCatalog());
        searchField.setPromptText("🔍  Search...");
        searchField.setPrefWidth(200);
        searchField.setStyle("-fx-background-radius: 5; -fx-padding: 7;");
        searchField.textProperty().addListener((obs, o, n) -> loadCatalog());
        filterBar.getChildren().addAll(new Label("Filter:"), catFilter, searchField);

        // Catalog table
        catalogTable.setItems(catalogList);
        catalogTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        catalogTable.setPlaceholder(new Label("No products"));
        VBox.setVgrow(catalogTable, Priority.ALWAYS);

        TableColumn<Product, String> pNameCol = new TableColumn<>("Product");
        pNameCol.setCellValueFactory(d -> d.getValue().nameProperty());

        TableColumn<Product, String> pCatCol = new TableColumn<>("Category");
        pCatCol.setCellValueFactory(d -> d.getValue().categoryProperty());

        TableColumn<Product, String> pBcCol = new TableColumn<>("Barcode");
        pBcCol.setCellValueFactory(d -> d.getValue().barcodeProperty());

        TableColumn<Product, Number> pPriceCol = new TableColumn<>("Price (KES)");
        pPriceCol.setCellValueFactory(d -> d.getValue().priceProperty());
        pPriceCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("%.2f", item.doubleValue()));
            }
        });

        TableColumn<Product, Number> pStockCol = new TableColumn<>("Stock");
        pStockCol.setCellValueFactory(d -> d.getValue().stockProperty());

        TableColumn<Product, Void> addBtnCol = new TableColumn<>("Add");
        addBtnCol.setMaxWidth(60);
        addBtnCol.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("＋");
            { btn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-background-radius: 4; -fx-font-size: 13; -fx-padding: 2 8 2 8;");
              btn.setOnAction(e -> addToCart(getTableView().getItems().get(getIndex()))); }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        catalogTable.getColumns().addAll(pNameCol, pCatCol, pBcCol, pPriceCol, pStockCol, addBtnCol);

        // Quick category buttons
        HBox catBtns = new HBox(8);
        catBtns.setAlignment(Pos.CENTER_LEFT);
        for (String cat : CATEGORIES) {
            if (cat.equals("All")) continue;
            Button b = new Button(cat);
            b.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-background-radius: 4; -fx-font-size: 11; -fx-padding: 5 10 5 10;");
            b.setOnAction(ev -> { catFilter.setValue(cat); loadCatalog(); });
            catBtns.getChildren().add(b);
        }
        Button allBtn = new Button("All");
        allBtn.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-background-radius: 4; -fx-font-size: 11; -fx-padding: 5 10 5 10;");
        allBtn.setOnAction(ev -> { catFilter.setValue("All"); loadCatalog(); });
        catBtns.getChildren().add(0, allBtn);

        panel.getChildren().addAll(title, scanBar, filterBar, catBtns, catalogTable);

        loadCatalog();
        return panel;
    }

    // ── Logic ─────────────────────────────────────────────────────────────────

    private void loadCatalog() {
        catalogList.clear();
        String query = searchField.getText().trim();
        String cat   = catFilter.getValue();
        List<Product> results;
        if (!query.isBlank()) {
            results = db.searchProducts(query);
        } else if ("All".equals(cat)) {
            results = db.getAllProducts();
        } else {
            results = db.getProductsByCategory(cat);
        }
        catalogList.addAll(results);
    }

    private void scanBarcode() {
        String code = barcodeField.getText().trim();
        if (code.isEmpty()) return;
        Product p = db.findByBarcode(code);
        if (p == null) {
            new Alert(Alert.AlertType.WARNING, "No product found for barcode: " + code).showAndWait();
        } else {
            addToCart(p);
        }
        barcodeField.clear();
    }

    private void addToCart(Product product) {
        if (product.getStock() <= 0) {
            new Alert(Alert.AlertType.WARNING, product.getName() + " is out of stock.").showAndWait();
            return;
        }
        for (CartItem ci : cartItems) {
            if (ci.getProduct().getId() == product.getId()) {
                if (ci.getQuantity() >= product.getStock()) {
                    new Alert(Alert.AlertType.WARNING, "Not enough stock for " + product.getName()).showAndWait();
                    return;
                }
                ci.setQuantity(ci.getQuantity() + 1);
                cartTable.refresh();
                refreshTotal();
                return;
            }
        }
        cartItems.add(new CartItem(product, 1));
        refreshTotal();
    }

    private void refreshTotal() {
        double total = cartItems.stream().mapToDouble(CartItem::getSubtotal).sum();
        totalLabel.setText(String.format("Total: KES %.2f", total));
    }

    // ── Payment Dialog ────────────────────────────────────────────────────────

    private void showPaymentDialog() {
        if (cartItems.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Cart is empty.").showAndWait();
            return;
        }

        double total = cartItems.stream().mapToDouble(CartItem::getSubtotal).sum();

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Payment");

        VBox root = new VBox(14);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: #2c3e50;");
        root.setPrefWidth(380);

        Label title = new Label("💳 Payment");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        title.setTextFill(Color.WHITE);

        Label totalLbl = new Label(String.format("Amount Due: KES %.2f", total));
        totalLbl.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        totalLbl.setTextFill(Color.web("#e67e22"));

        Label methodLabel = new Label("Payment Method:");
        methodLabel.setTextFill(Color.web("#bdc3c7"));

        ComboBox<String> methodBox = new ComboBox<>(FXCollections.observableArrayList(
                "Cash", "M-Pesa", "Card", "Credit"));
        methodBox.setValue("Cash");
        methodBox.setMaxWidth(Double.MAX_VALUE);
        methodBox.setStyle("-fx-background-radius: 5;");

        // Cash-only fields
        Label paidLabel = new Label("Amount Paid (KES):");
        paidLabel.setTextFill(Color.web("#bdc3c7"));

        TextField paidField = new TextField();
        paidField.setPromptText("Enter amount paid");
        paidField.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 10;");

        Label changeLbl = new Label("Change: KES 0.00");
        changeLbl.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        changeLbl.setTextFill(Color.web("#2ecc71"));

        // Reference field for M-Pesa / Card
        Label refLabel = new Label("Reference / Transaction No.:");
        refLabel.setTextFill(Color.web("#bdc3c7"));

        TextField refField = new TextField();
        refField.setPromptText("Enter reference number");
        refField.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 10;");

        // Credit note field
        Label noteLabel = new Label("Customer / Credit Note:");
        noteLabel.setTextFill(Color.web("#bdc3c7"));

        TextField noteField = new TextField();
        noteField.setPromptText("Enter customer name or note");
        noteField.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 10;");

        paidField.textProperty().addListener((obs, o, n) -> {
            try {
                double paid   = Double.parseDouble(n);
                double change = paid - total;
                changeLbl.setText(String.format("Change: KES %.2f", Math.max(0, change)));
                changeLbl.setTextFill(change >= 0 ? Color.web("#2ecc71") : Color.web("#e74c3c"));
            } catch (NumberFormatException ignored) {
                changeLbl.setText("Change: KES 0.00");
            }
        });

        // Show/hide fields based on selected payment method
        Runnable updateFields = () -> {
            String m = methodBox.getValue();
            boolean isCash   = "Cash".equals(m);
            boolean isRef    = "M-Pesa".equals(m) || "Card".equals(m);
            boolean isCredit = "Credit".equals(m);

            paidLabel.setVisible(isCash);   paidLabel.setManaged(isCash);
            paidField.setVisible(isCash);   paidField.setManaged(isCash);
            changeLbl.setVisible(isCash);   changeLbl.setManaged(isCash);

            refLabel.setVisible(isRef);     refLabel.setManaged(isRef);
            refField.setVisible(isRef);     refField.setManaged(isRef);

            noteLabel.setVisible(isCredit); noteLabel.setManaged(isCredit);
            noteField.setVisible(isCredit); noteField.setManaged(isCredit);
        };
        methodBox.valueProperty().addListener((obs, o, n) -> updateFields.run());
        updateFields.run();

        Label errorLbl = new Label();
        errorLbl.setTextFill(Color.web("#e74c3c"));

        Button payBtn = new Button("✔ Complete Sale");
        payBtn.setMaxWidth(Double.MAX_VALUE);
        payBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 12;");

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setMaxWidth(Double.MAX_VALUE);
        cancelBtn.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 10;");
        cancelBtn.setOnAction(e -> dialog.close());

        payBtn.setOnAction(e -> {
            String method = methodBox.getValue();
            double paid;
            double change;

            if ("Cash".equals(method)) {
                try {
                    paid = Double.parseDouble(paidField.getText().trim());
                } catch (NumberFormatException ex) {
                    errorLbl.setText("Please enter a valid amount.");
                    return;
                }
                if (paid < total) {
                    errorLbl.setText("Insufficient amount paid.");
                    return;
                }
                change = paid - total;
            } else {
                // For M-Pesa, Card and Credit the tendered amount equals the total
                paid   = total;
                change = 0.0;
            }

            long txId = db.saveTransaction(currentUser.getUsername(), total, paid, change,
                    method, cartItems.stream().toList());
            dialog.close();
            showReceipt(txId, total, paid, change, method);
            cartItems.clear();
            refreshTotal();
            loadCatalog(); // refresh stock display
        });

        root.getChildren().addAll(title, totalLbl, methodLabel, methodBox,
                paidLabel, paidField, changeLbl,
                refLabel, refField,
                noteLabel, noteField,
                errorLbl, payBtn, cancelBtn);

        dialog.setScene(new Scene(root));
        dialog.showAndWait();
    }

    // ── Receipt ───────────────────────────────────────────────────────────────

    private void showReceipt(long txId, double total, double paid, double change, String paymentMethod) {
        // Take a snapshot of cartItems before they are cleared
        ObservableList<CartItem> snapshot = FXCollections.observableArrayList(cartItems);

        Stage receipt = new Stage();
        receipt.initModality(Modality.APPLICATION_MODAL);
        receipt.setTitle("Receipt #" + txId);

        // ── Printable receipt content ──────────────────────────────────────
        VBox receiptContent = new VBox(8);
        receiptContent.setPadding(new Insets(24));
        receiptContent.setStyle("-fx-background-color: white;");
        receiptContent.setPrefWidth(360);

        Label shopName = new Label("🔧 HardwareHub POS");
        shopName.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        shopName.setAlignment(Pos.CENTER);
        shopName.setMaxWidth(Double.MAX_VALUE);

        Label subtitle = new Label("Official Receipt");
        subtitle.setFont(Font.font("Arial", 12));
        subtitle.setTextFill(Color.web("#7f8c8d"));
        subtitle.setAlignment(Pos.CENTER);
        subtitle.setMaxWidth(Double.MAX_VALUE);

        Separator sep1 = new Separator();

        Label txLabel = new Label(String.format("Transaction #%d", txId));
        Label cashierLabel = new Label("Cashier: " + currentUser.getDisplayName());
        txLabel.setFont(Font.font("Arial", 11));
        cashierLabel.setFont(Font.font("Arial", 11));

        Separator sep2 = new Separator();

        VBox itemsBox = new VBox(4);
        for (CartItem ci : snapshot) {
            HBox row = new HBox();
            Label iName = new Label(ci.getName() + " x" + ci.getQuantity());
            iName.setFont(Font.font("Arial", 12));
            Region sp = new Region();
            HBox.setHgrow(sp, Priority.ALWAYS);
            Label iPrice = new Label(String.format("KES %.2f", ci.getSubtotal()));
            iPrice.setFont(Font.font("Arial", 12));
            row.getChildren().addAll(iName, sp, iPrice);
            itemsBox.getChildren().add(row);
        }

        Separator sep3 = new Separator();

        HBox totalRow = new HBox();
        Label tLabel = new Label("TOTAL");
        tLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        Region sp3 = new Region();
        HBox.setHgrow(sp3, Priority.ALWAYS);
        Label tValue = new Label(String.format("KES %.2f", total));
        tValue.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        totalRow.getChildren().addAll(tLabel, sp3, tValue);

        HBox methodRow = makeReceiptRow("Payment:", paymentMethod);

        Separator sep4 = new Separator();
        Label thanks = new Label("Thank you for your purchase!");
        thanks.setFont(Font.font("Arial", 12));
        thanks.setTextFill(Color.web("#7f8c8d"));
        thanks.setAlignment(Pos.CENTER);
        thanks.setMaxWidth(Double.MAX_VALUE);

        receiptContent.getChildren().addAll(shopName, subtitle, sep1, txLabel, cashierLabel,
                sep2, itemsBox, sep3, totalRow, methodRow);

        if ("Cash".equals(paymentMethod)) {
            receiptContent.getChildren().addAll(
                    makeReceiptRow("Amount Paid:", String.format("KES %.2f", paid)),
                    makeReceiptRow("Change Due:",  String.format("KES %.2f", change)));
        }

        receiptContent.getChildren().addAll(sep4, thanks);

        // ── Action buttons ─────────────────────────────────────────────────
        Button printBtn = new Button("🖨 Print Receipt");
        printBtn.setMaxWidth(Double.MAX_VALUE);
        printBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 10;");
        printBtn.setOnAction(e -> printReceipt(receiptContent, receipt));

        Button closeBtn = new Button("Close");
        closeBtn.setMaxWidth(Double.MAX_VALUE);
        closeBtn.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 10;");
        closeBtn.setOnAction(e -> receipt.close());

        HBox btnRow = new HBox(8, printBtn, closeBtn);
        btnRow.setPadding(new Insets(8, 0, 0, 0));
        HBox.setHgrow(printBtn, Priority.ALWAYS);
        HBox.setHgrow(closeBtn, Priority.ALWAYS);

        VBox root = new VBox(8, receiptContent, btnRow);
        root.setStyle("-fx-background-color: white;");
        root.setPadding(new Insets(0, 24, 24, 24));

        receipt.setScene(new Scene(root));
        receipt.showAndWait();
    }

    private void printReceipt(VBox receiptContent, Stage owner) {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "No printer found. Please connect a printer and try again.",
                    ButtonType.OK);
            alert.initOwner(owner);
            alert.showAndWait();
            return;
        }
        boolean proceed = job.showPrintDialog(owner);
        if (proceed) {
            job.printPage(receiptContent);
            job.endJob();
        }
    }

    private HBox makeReceiptRow(String label, String value) {
        HBox row = new HBox();
        Label l = new Label(label); l.setFont(Font.font("Arial", 12));
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label v = new Label(value); v.setFont(Font.font("Arial", 12));
        row.getChildren().addAll(l, sp, v);
        return row;
    }
}

