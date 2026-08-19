package DAO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import Exception.DataAccessException;
import Exception.InvalidActionException;
import Model.Product;
import Model.Purchase;
import db.DatabaseConnection;

public class Purchasedao {
	
	 public Purchase addPurchase(int userId, int productId, int quantity) throws InvalidActionException {
	        try (Connection connection = DatabaseConnection.getConnection()) {
	            connection.setAutoCommit(false);
	            try {
	                Product product = findProductForUpdate(connection, productId);
	                if (product.getQuantity() < quantity) {
	                    throw new IllegalArgumentException("Only " + product.getQuantity() + " item(s) available in stock.");
	                }

	                BigDecimal totalAmount = product.getPrice().multiply(BigDecimal.valueOf(quantity));
	                int purchaseId = insertPurchase(connection, userId, product, quantity, totalAmount);
	                reduceProductStock(connection, productId, quantity);
	                connection.commit();

	                return new Purchase(
	                        purchaseId,
	                        userId,
	                        productId,
	                        productId,
	                        product.getName(),
	                        quantity,
	                        product.getPrice(),
	                        totalAmount,
	                        LocalDateTime.now()
	                );
	            } catch (IllegalArgumentException exception) {
	                rollback(connection);
	                throw exception;
	            } catch (SQLException exception) {
	                rollback(connection);
	                throw new DataAccessException("Unable to complete purchase.");
	            } finally {
	                connection.setAutoCommit(true);
	            }
	        } catch (SQLException exception) {
	            throw new DataAccessException("Unable to open database transaction.");
	        }
	    }

	    public List<Purchase> findByUserId(int userId) {
	        String sql = """
	                SELECT purchase_id, user_id, product_id, product_id_snapshot, product_name_snapshot,
	                       quantity, unit_price, total_amount, purchased_at
	                FROM purchases
	                WHERE user_id = ?
	                ORDER BY purchased_at DESC, purchase_id DESC
	                """;
	        List<Purchase> purchases = new ArrayList<>();
	        try (Connection connection = DatabaseConnection.getConnection();
	             PreparedStatement statement = connection.prepareStatement(sql)) {
	            statement.setInt(1, userId);
	            try (ResultSet resultSet = statement.executeQuery()) {
	                while (resultSet.next()) {
	                    purchases.add(mapPurchase(resultSet));
	                }
	            }
	            return purchases;
	        } catch (SQLException exception) {
	            throw new DataAccessException("Unable to fetch purchase history.");
	        }
	    }

	    public List<Purchase> findByUsername(String username) {
	        String sql = """
	                SELECT p.purchase_id, p.user_id, p.product_id, p.product_id_snapshot, p.product_name_snapshot,
	                       p.quantity, p.unit_price, p.total_amount, p.purchased_at
	                FROM purchases p
	                INNER JOIN users u ON u.user_id = p.user_id
	                WHERE u.username = ?
	                ORDER BY p.purchased_at DESC, p.purchase_id DESC
	                """;
	        List<Purchase> purchases = new ArrayList<>();
	        try (Connection connection = DatabaseConnection.getConnection();
	             PreparedStatement statement = connection.prepareStatement(sql)) {
	            statement.setString(1, username);
	            try (ResultSet resultSet = statement.executeQuery()) {
	                while (resultSet.next()) {
	                    purchases.add(mapPurchase(resultSet));
	                }
	            }
	            return purchases;
	        } catch (SQLException exception) {
	            throw new DataAccessException("Unable to fetch user purchase history.");
	        }
	    }

	    private Product findProductForUpdate(Connection connection, int productId) throws SQLException, InvalidActionException {
	        String sql = """
	                SELECT product_id, name, description, price, quantity
	                FROM products
	                WHERE product_id = ?
	                FOR UPDATE
	                """;
	        try (PreparedStatement statement = connection.prepareStatement(sql)) {
	            statement.setInt(1, productId);
	            try (ResultSet resultSet = statement.executeQuery()) {
	                if (resultSet.next()) {
	                    return new Product(
	                            resultSet.getInt("product_id"),
	                            resultSet.getString("name"),
	                            resultSet.getString("description"),
	                            resultSet.getBigDecimal("price"),
	                            resultSet.getInt("quantity")
	                    );
	                }
	                throw new InvalidActionException("Invalid product ID. Product was not found.");
	            }
	        }
	    }

	    private int insertPurchase(Connection connection, int userId, Product product, int quantity, BigDecimal totalAmount) throws SQLException {
	        String sql = """
	                INSERT INTO purchases
	                    (user_id, product_id, product_id_snapshot, product_name_snapshot, quantity, unit_price, total_amount)
	                VALUES (?, ?, ?, ?, ?, ?, ?)
	                """;
	        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
	            statement.setInt(1, userId);
	            statement.setInt(2, product.getProductId());
	            statement.setInt(3, product.getProductId());
	            statement.setString(4, product.getName());
	            statement.setInt(5, quantity);
	            statement.setBigDecimal(6, product.getPrice());
	            statement.setBigDecimal(7, totalAmount);
	            statement.executeUpdate();

	            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
	                if (generatedKeys.next()) {
	                    return generatedKeys.getInt(1);
	                }
	            }
	            throw new SQLException("Purchase ID was not returned.");
	        }
	    }

	    private void reduceProductStock(Connection connection, int productId, int quantity) throws SQLException {
	        String sql = "UPDATE products SET quantity = quantity - ? WHERE product_id = ?";
	        try (PreparedStatement statement = connection.prepareStatement(sql)) {
	            statement.setInt(1, quantity);
	            statement.setInt(2, productId);
	            statement.executeUpdate();
	        }
	    }

	    private void rollback(Connection connection) throws InvalidActionException {
	        try {
	            connection.rollback();
	        } catch (SQLException exception) {
	            throw new InvalidActionException("Purchase failed and rollback also failed.");
	        }
	    }

	    private Purchase mapPurchase(ResultSet resultSet) throws SQLException {
	        Object productIdValue = resultSet.getObject("product_id");
	        Integer productId = productIdValue == null ? null : ((Number) productIdValue).intValue();
	        Timestamp timestamp = resultSet.getTimestamp("purchased_at");
	        LocalDateTime purchasedAt = timestamp == null ? null : timestamp.toLocalDateTime();

	        return new Purchase(
	                resultSet.getInt("purchase_id"),
	                resultSet.getInt("user_id"),
	                productId,
	                resultSet.getInt("product_id_snapshot"),
	                resultSet.getString("product_name_snapshot"),
	                resultSet.getInt("quantity"),
	                resultSet.getBigDecimal("unit_price"),
	                resultSet.getBigDecimal("total_amount"),
	                purchasedAt
	        );
	    }

}
