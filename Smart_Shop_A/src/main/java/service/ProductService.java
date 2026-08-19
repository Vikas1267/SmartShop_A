package com.vikas.service;

import com.vikas.dao.ProductDao;
import com.vikas.model.Product;
import com.vikas.util.ValidationUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ProductService {
    private final ProductDao productDao;

    public ProductService(ProductDao productDao) {
        this.productDao = productDao;
    }

    public List<Product> listProducts() {
        return productDao.findAllSortedByName();
    }

    public List<Product> searchProducts(String keyword) {
        keyword = ValidationUtil.requireText("Search keyword", keyword, 100);
        return productDao.searchByKeyword(keyword);
    }

    public Optional<Product> findProductById(int productId) {
        return productDao.findById(productId);
    }

    public Product addProduct(int productId, String name, String description, BigDecimal price, int quantity) {
        validateProductId(productId);
        name = ValidationUtil.requireText("Product name", name, 100);
        description = ValidationUtil.requireText("Product description", description, 500);
        ValidationUtil.requireNonNegativePrice(price);
        ValidationUtil.requireNonNegativeQuantity(quantity);

        if (productDao.existsById(productId)) {
            throw new IllegalArgumentException("Product ID already exists. Please use a different ID.");
        }

        Product product = new Product(productId, name, description, price, quantity);
        productDao.add(product);
        return product;
    }

    public void updateProductName(int productId, String name) {
        validateProductId(productId);
        name = ValidationUtil.requireText("Product name", name, 100);
        ensureUpdated(productDao.updateName(productId, name));
    }

    public void updateProductDescription(int productId, String description) {
        validateProductId(productId);
        description = ValidationUtil.requireText("Product description", description, 500);
        ensureUpdated(productDao.updateDescription(productId, description));
    }

    public void updateProductPrice(int productId, BigDecimal price) {
        validateProductId(productId);
        ValidationUtil.requireNonNegativePrice(price);
        ensureUpdated(productDao.updatePrice(productId, price));
    }

    public void updateProductQuantity(int productId, int quantity) {
        validateProductId(productId);
        ValidationUtil.requireNonNegativeQuantity(quantity);
        ensureUpdated(productDao.updateQuantity(productId, quantity));
    }

    public void deleteProduct(int productId) {
        validateProductId(productId);
        ensureUpdated(productDao.deleteById(productId));
    }

    private void validateProductId(int productId) {
        if (productId <= 0) {
            throw new IllegalArgumentException("Product ID must be greater than zero.");
        }
    }

    private void ensureUpdated(boolean updated) {
        if (!updated) {
            throw new IllegalArgumentException("Product was not found.");
        }
    }
}
