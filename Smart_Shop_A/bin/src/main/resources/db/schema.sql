CREATE DATABASE IF NOT EXISTS vikas
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE vikas;

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
);

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
);

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
);
