package com.kipcollo.demo;

import javafx.beans.property.*;

public class Product {

    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty category;
    private final StringProperty barcode;
    private final DoubleProperty price;
    private final IntegerProperty stock;
    private final StringProperty description;

    public Product(int id, String name, String category, String barcode, double price, int stock, String description) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.category = new SimpleStringProperty(category);
        this.barcode = new SimpleStringProperty(barcode);
        this.price = new SimpleDoubleProperty(price);
        this.stock = new SimpleIntegerProperty(stock);
        this.description = new SimpleStringProperty(description);
    }

    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }

    public String getName() { return name.get(); }
    public StringProperty nameProperty() { return name; }
    public void setName(String v) { name.set(v); }

    public String getCategory() { return category.get(); }
    public StringProperty categoryProperty() { return category; }
    public void setCategory(String v) { category.set(v); }

    public String getBarcode() { return barcode.get(); }
    public StringProperty barcodeProperty() { return barcode; }
    public void setBarcode(String v) { barcode.set(v); }

    public double getPrice() { return price.get(); }
    public DoubleProperty priceProperty() { return price; }
    public void setPrice(double v) { price.set(v); }

    public int getStock() { return stock.get(); }
    public IntegerProperty stockProperty() { return stock; }
    public void setStock(int v) { stock.set(v); }

    public String getDescription() { return description.get(); }
    public StringProperty descriptionProperty() { return description; }
    public void setDescription(String v) { description.set(v); }
}
