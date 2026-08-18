package Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Purchase {
    private final int purchaseId;
    private final int userId;
    private final Integer productId;
    private final int productIdSnapshot;
    private final String productName;
    private final int quantity;
    private final BigDecimal unitPrice;
    private final BigDecimal totalAmount;
    private final LocalDateTime purchasedAt;

    public Purchase(
            int purchaseId,
            int userId,
            Integer productId,
            int productIdSnapshot,
            String productName,
            int quantity,
            BigDecimal unitPrice,
            BigDecimal totalAmount,
            LocalDateTime purchasedAt
    ) {
        this.purchaseId = purchaseId;
        this.userId = userId;
        this.productId = productId;
        this.productIdSnapshot = productIdSnapshot;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalAmount = totalAmount;
        this.purchasedAt = purchasedAt;
    }

    public int getPurchaseId() {
        return purchaseId;
    }

    public int getUserId() {
        return userId;
    }

    public Integer getProductId() {
        return productId;
    }

    public int getProductIdSnapshot() {
        return productIdSnapshot;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getPurchasedAt() {
        return purchasedAt;
    }
}
