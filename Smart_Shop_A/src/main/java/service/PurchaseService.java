package com.vikas.service;

import com.vikas.dao.PurchaseDao;
import com.vikas.model.Purchase;
import com.vikas.model.User;
import com.vikas.util.ValidationUtil;

import java.util.List;

public class PurchaseService {
    private final PurchaseDao purchaseDao;

    public PurchaseService(PurchaseDao purchaseDao) {
        this.purchaseDao = purchaseDao;
    }

    public Purchase addProductToCart(User user, int productId, int quantity) {
        if (user == null) {
            throw new IllegalArgumentException("Please login before making a purchase.");
        }
        if (productId <= 0) {
            throw new IllegalArgumentException("Product ID must be greater than zero.");
        }
        ValidationUtil.requirePositiveQuantity(quantity);
        return purchaseDao.addPurchase(user.getUserId(), productId, quantity);
    }

    public List<Purchase> viewCartItems(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Please login before viewing cart items.");
        }
        return purchaseDao.findByUserId(user.getUserId());
    }

    public List<Purchase> viewUserPurchaseHistory(String username) {
        username = ValidationUtil.requireText("Username", username, 50);
        return purchaseDao.findByUsername(username);
    }
}
