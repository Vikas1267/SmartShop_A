package Model;

import java.math.BigDecimal;

public class Product {
    private final int productId;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final int quantity;

    public Product(int productId, String name, String description, BigDecimal price, int quantity) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
    }

    public int getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }
}
