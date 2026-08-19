package ui;

import Model.Product;
import Model.Purchase;
import Model.User;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class ConsoleTables {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private ConsoleTables() {
    }

    public static void printProducts(List<Product> products) {
        if (products.isEmpty()) {
            System.out.println("No products found.");
            return;
        }

        System.out.printf("%-10s | %-24s | %-52s | %12s | %-8s%n",
                "Product ID", "Name", "Description", "Price", "Quantity");
        System.out.println("-".repeat(118));
        for (Product product : products) {
            System.out.printf("%-10d | %-24s | %-52s | %12s | %-8d%n",
                    product.getProductId(),
                    limit(product.getName(), 24),
                    limit(product.getDescription(), 52),
                    money(product.getPrice()),
                    product.getQuantity());
        }
    }

    public static void printProductDetails(Product product) {
        System.out.println("Product Name: " + product.getName());
        System.out.println("Description: " + product.getDescription());
        System.out.println("Price: " + money(product.getPrice()));
        System.out.println("Available Quantity: " + product.getQuantity());
    }

    public static void printUsers(List<User> users) {
        if (users.isEmpty()) {
            System.out.println("No registered users found.");
            return;
        }

        System.out.printf("%-8s | %-16s | %-24s | %-16s | %-30s | %-10s%n",
                "User ID", "Username", "Name", "City", "Email", "Mobile");
        System.out.println("-".repeat(119));
        for (User user : users) {
            System.out.printf("%-8d | %-16s | %-24s | %-16s | %-30s | %-10s%n",
                    user.getUserId(),
                    limit(user.getUsername(), 16),
                    limit(user.getFullName(), 24),
                    limit(user.getCity(), 16),
                    limit(user.getEmail(), 30),
                    user.getMobile());
        }
    }

    public static void printCartItems(List<Purchase> purchases) {
        if (purchases.isEmpty()) {
            System.out.println("No purchased/cart items found yet.");
            return;
        }

        BigDecimal total = BigDecimal.ZERO;
        System.out.printf("%-28s | %-8s | %12s | %12s%n",
                "Product Name", "Quantity", "Price", "Subtotal");
        System.out.println("-".repeat(72));
        for (Purchase purchase : purchases) {
            total = total.add(purchase.getTotalAmount());
            System.out.printf("%-28s | %-8d | %12s | %12s%n",
                    limit(purchase.getProductName(), 28),
                    purchase.getQuantity(),
                    money(purchase.getUnitPrice()),
                    money(purchase.getTotalAmount()));
        }
        System.out.println("-".repeat(72));
        System.out.println("Total Amount >> " + money(total));
    }

    public static void printPurchaseHistory(List<Purchase> purchases) {
        if (purchases.isEmpty()) {
            System.out.println("No purchase history found.");
            return;
        }

        System.out.printf("%-8s | %-16s | %-28s | %-8s | %12s | %12s%n",
                "Order ID", "Date", "Product Name", "Quantity", "Price", "Total");
        System.out.println("-".repeat(99));
        for (Purchase purchase : purchases) {
            System.out.printf("%-8d | %-16s | %-28s | %-8d | %12s | %12s%n",
                    purchase.getPurchaseId(),
                    formatDate(purchase),
                    limit(purchase.getProductName(), 28),
                    purchase.getQuantity(),
                    money(purchase.getUnitPrice()),
                    money(purchase.getTotalAmount()));
        }
    }

    public static void printAdminPurchaseHistory(List<Purchase> purchases) {
        if (purchases.isEmpty()) {
            System.out.println("No purchase history found for this user.");
            return;
        }

        System.out.printf("%-10s | %-28s | %-8s | %-16s | %12s%n",
                "Product ID", "Name", "Quantity", "Date", "Price");
        System.out.println("-".repeat(84));
        for (Purchase purchase : purchases) {
            System.out.printf("%-10d | %-28s | %-8d | %-16s | %12s%n",
                    purchase.getProductIdSnapshot(),
                    limit(purchase.getProductName(), 28),
                    purchase.getQuantity(),
                    formatDate(purchase),
                    money(purchase.getUnitPrice()));
        }
    }

    public static String money(BigDecimal amount) {
        if (amount == null) {
            return "Rs 0.00";
        }
        return "Rs " + amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private static String formatDate(Purchase purchase) {
        if (purchase.getPurchasedAt() == null) {
            return "N/A";
        }
        return purchase.getPurchasedAt().format(DATE_TIME_FORMATTER);
    }

    private static String limit(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength - 3) + "...";
    }
}
