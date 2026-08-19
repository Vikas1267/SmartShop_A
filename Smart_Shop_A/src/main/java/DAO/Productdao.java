package DAO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import Exception.DataAccessException;
import Exception.ProductNotFoundException;
import Model.Product;
import db.DatabaseConnection;


public class Productdao {

	public List<Product> findAllSortedByName() {
        String sql = """
                SELECT product_id, name, description, price, quantity
                FROM products
                ORDER BY name, product_id
                """;
        return findProducts(sql);
    }

    public List<Product> searchByKeyword(String keyword) throws ProductNotFoundException {
        String sql = """
                SELECT product_id, name, description, price, quantity
                FROM products
                WHERE LOWER(name) LIKE ? OR LOWER(description) LIKE ?
                ORDER BY name, product_id
                """;
        List<Product> products = new ArrayList<>();
        String likeKeyword = "%" + keyword.toLowerCase() + "%";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, likeKeyword);
            statement.setString(2, likeKeyword);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    products.add(mapProduct(resultSet));
                }
            }
            return products;
        } catch (SQLException exception) {
            throw new ProductNotFoundException("Unable to search products.");
        }
    }

    public Optional<Product> findById(int productId) throws ProductNotFoundException {
        String sql = """
                SELECT product_id, name, description, price, quantity
                FROM products
                WHERE product_id = ?
                """;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, productId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapProduct(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException exception) {
            throw new ProductNotFoundException("Unable to fetch product.");
        }
    }

    public boolean existsById(int productId) {
        String sql = "SELECT 1 FROM products WHERE product_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, productId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to check product ID.");
        }
    }

    public void add(Product product) {
        String sql = """
                INSERT INTO products (product_id, name, description, price, quantity)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, product.getProductId());
            statement.setString(2, product.getName());
            statement.setString(3, product.getDescription());
            statement.setBigDecimal(4, product.getPrice());
            statement.setInt(5, product.getQuantity());
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to add product.");
        }
    }

    public boolean updateName(int productId, String name) {
        return updateStringField("UPDATE products SET name = ? WHERE product_id = ?", productId, name);
    }

    public boolean updateDescription(int productId, String description) {
        return updateStringField("UPDATE products SET description = ? WHERE product_id = ?", productId, description);
    }

    public boolean updatePrice(int productId, BigDecimal price) {
        String sql = "UPDATE products SET price = ? WHERE product_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBigDecimal(1, price);
            statement.setInt(2, productId);
            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to update product price.");
        }
    }

    public boolean updateQuantity(int productId, int quantity) {
        String sql = "UPDATE products SET quantity = ? WHERE product_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, quantity);
            statement.setInt(2, productId);
            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to update product quantity.");
        }
    }

    public boolean deleteById(int productId) {
        String sql = "DELETE FROM products WHERE product_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, productId);
            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to delete product.");
        }
    }

    private List<Product> findProducts(String sql) {
        List<Product> products = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                products.add(mapProduct(resultSet));
            }
            return products;
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to fetch products.");
        }
    }

    private boolean updateStringField(String sql, int productId, String value) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, value);
            statement.setInt(2, productId);
            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to update product.");
        }
    }

    private Product mapProduct(ResultSet resultSet) throws SQLException {
        return new Product(
                resultSet.getInt("product_id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getBigDecimal("price"),
                resultSet.getInt("quantity")
        );
    }
}
