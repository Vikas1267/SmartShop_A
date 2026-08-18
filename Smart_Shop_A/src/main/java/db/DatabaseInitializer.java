package db;

import Exception.DataAccessException;
import Model.Product;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public final class DatabaseInitializer {
    private static final String CREATE_USERS_TABLE = """
            CREATE TABLE IF NOT EXISTS users (
                user_id INT AUTO_INCREMENT PRIMARY KEY,
                first_name VARCHAR(50) NOT NULL,
                last_name VARCHAR(50) NOT NULL,
                username VARCHAR(50) NOT NULL UNIQUE,
                password_hash VARCHAR(128) NOT NULL,
                password_salt VARCHAR(64) NOT NULL,
                city VARCHAR(80) NOT NULL,
                email VARCHAR(120) NOT NULL UNIQUE,
                mobile VARCHAR(10) NOT NULL UNIQUE,
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
            """;

    private static final String CREATE_PRODUCTS_TABLE = """
            CREATE TABLE IF NOT EXISTS products (
                product_id INT PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                description VARCHAR(500) NOT NULL,
                price DECIMAL(10, 2) NOT NULL,
                quantity INT NOT NULL,
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                CONSTRAINT chk_products_price CHECK (price >= 0),
                CONSTRAINT chk_products_quantity CHECK (quantity >= 0)
            )
            """;

    private static final String CREATE_PURCHASES_TABLE = """
            CREATE TABLE IF NOT EXISTS purchases (
                purchase_id INT AUTO_INCREMENT PRIMARY KEY,
                user_id INT NOT NULL,
                product_id INT NULL,
                product_id_snapshot INT NOT NULL,
                product_name_snapshot VARCHAR(100) NOT NULL,
                quantity INT NOT NULL,
                unit_price DECIMAL(10, 2) NOT NULL,
                total_amount DECIMAL(12, 2) NOT NULL,
                purchased_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                CONSTRAINT chk_purchases_quantity CHECK (quantity > 0),
                CONSTRAINT chk_purchases_unit_price CHECK (unit_price >= 0),
                CONSTRAINT chk_purchases_total_amount CHECK (total_amount >= 0),
                CONSTRAINT fk_purchases_users FOREIGN KEY (user_id)
                    REFERENCES users (user_id)
                    ON DELETE CASCADE,
                CONSTRAINT fk_purchases_products FOREIGN KEY (product_id)
                    REFERENCES products (product_id)
                    ON DELETE SET NULL
            )
            """;

    private static final List<Product> DEFAULT_PRODUCTS = List.of(
            new Product(101, "Wireless Mouse", "Ergonomic 2.4 GHz wireless mouse with adjustable DPI", new BigDecimal("699.00"), 25),
            new Product(102, "Mechanical Keyboard", "Compact blue-switch mechanical keyboard with backlight", new BigDecimal("2499.00"), 15),
            new Product(103, "USB-C Charger", "Fast charging 30W USB-C power adapter", new BigDecimal("1199.00"), 30),
            new Product(104, "Bluetooth Headphones", "Over-ear headphones with 30 hour battery life", new BigDecimal("3499.00"), 12),
            new Product(105, "Laptop Stand", "Foldable aluminum laptop stand for desk setup", new BigDecimal("899.00"), 20)
    );

    public void initialize() {
        try {
            createDatabase();
            try (Connection connection = DatabaseConnection.getConnection()) {
                dropIncompatibleTablesIfNecessary(connection);
                execute(connection, CREATE_USERS_TABLE);
                execute(connection, CREATE_PRODUCTS_TABLE);
                execute(connection, CREATE_PURCHASES_TABLE);
                seedProductsWhenEmpty(connection);
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Database setup failed. Please check MySQL service and credentials.", exception);
        }
    }

    private void dropIncompatibleTablesIfNecessary(Connection connection) throws SQLException {
        if (isTableMissingRequiredColumn(connection, "users", "user_id")) {
            execute(connection, "DROP TABLE IF EXISTS purchases");
            execute(connection, "DROP TABLE IF EXISTS users");
        }
        if (isTableMissingRequiredColumn(connection, "products", "product_id")) {
            execute(connection, "DROP TABLE IF EXISTS purchases");
            execute(connection, "DROP TABLE IF EXISTS products");
        }
        if (isTableMissingRequiredColumn(connection, "purchases", "purchase_id")) {
            execute(connection, "DROP TABLE IF EXISTS purchases");
        }
    }

    private boolean isTableMissingRequiredColumn(Connection connection, String tableName, String requiredColumn) throws SQLException {
        String sql = """
                SELECT 1
                FROM INFORMATION_SCHEMA.COLUMNS
                WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? AND COLUMN_NAME = ?
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, DatabaseConnection.config().getDatabaseName());
            statement.setString(2, tableName);
            statement.setString(3, requiredColumn);
            try (ResultSet resultSet = statement.executeQuery()) {
                boolean columnExists = resultSet.next();
                if (!columnExists) {
                    return tableExists(connection, tableName);
                }
                return false;
            }
        }
    }

    private boolean tableExists(Connection connection, String tableName) throws SQLException {
        String sql = """
                SELECT 1
                FROM INFORMATION_SCHEMA.TABLES
                WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, DatabaseConnection.config().getDatabaseName());
            statement.setString(2, tableName);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private void createDatabase() throws SQLException {
        String databaseName = DatabaseConnection.config().getDatabaseName();
        String sql = "CREATE DATABASE IF NOT EXISTS `" + databaseName + "` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
        try (Connection connection = DatabaseConnection.getServerConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    private void execute(Connection connection, String sql) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    private void seedProductsWhenEmpty(Connection connection) throws SQLException {
        String countSql = "SELECT COUNT(*) FROM products";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(countSql)) {
            resultSet.next();
            if (resultSet.getInt(1) > 0) {
                return;
            }
        }

        String insertSql = """
                INSERT INTO products (product_id, name, description, price, quantity)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
            for (Product product : DEFAULT_PRODUCTS) {
                statement.setInt(1, product.getProductId());
                statement.setString(2, product.getName());
                statement.setString(3, product.getDescription());
                statement.setBigDecimal(4, product.getPrice());
                statement.setInt(5, product.getQuantity());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }
}
