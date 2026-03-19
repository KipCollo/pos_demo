package com.kipcollo.demo;

import javafx.beans.property.*;

public class SaleRecord {

    private final SimpleStringProperty product;
    private final SimpleStringProperty customer;
    private final SimpleDoubleProperty amount;

    public SaleRecord(String product, String customer, double amount) {
        this.product = new SimpleStringProperty(product);
        this.customer = new SimpleStringProperty(customer);
        this.amount = new SimpleDoubleProperty(amount);
    }

    public StringProperty productProperty() { return product; }
    public StringProperty customerProperty() { return customer; }
    public DoubleProperty amountProperty() { return amount; }
}