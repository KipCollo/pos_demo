package com.kipcollo.demo;

import javafx.beans.property.*;

public class CartItem {

    private final Product product;
    private final IntegerProperty quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = new SimpleIntegerProperty(quantity);
    }

    public Product getProduct() { return product; }
    public String getName() { return product.getName(); }
    public double getUnitPrice() { return product.getPrice(); }

    public int getQuantity() { return quantity.get(); }
    public IntegerProperty quantityProperty() { return quantity; }
    public void setQuantity(int q) { quantity.set(q); }

    public double getSubtotal() { return product.getPrice() * quantity.get(); }

    public DoubleProperty subtotalProperty() {
        DoubleProperty prop = new SimpleDoubleProperty(getSubtotal());
        quantity.addListener((obs, o, n) -> prop.set(product.getPrice() * n.intValue()));
        return prop;
    }
}
