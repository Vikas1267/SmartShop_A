package ui;

import com.dao.Productdao;
import com.dao.Purchasedao;
import com.dao.Userdao;
import Exception.DataAccessException;
import Model.Admin;
import Model.Product;
import Model.Purchase;
import Model.User;
import service.AdminService;
import service.AuthService;
import service.ProductService;
import service.PurchaseService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleApp {
    private final ConsoleInput input;
    private final AuthService authService;
    private final AdminService adminService;
    private final ProductService productService;
    private final PurchaseService purchaseService;

    public ConsoleApp() {
        UserDao userDao = new UserDao();
        ProductDao productDao = new ProductDao();
        PurchaseDao purchaseDao = new PurchaseDao();

        this.input = new ConsoleInput(new Scanner(System.in));
        this.authService = new AuthService(userDao);
        this.adminService = new AdminService(userDao);
        this.productService = new ProductService(productDao);
        this.purchaseService = new PurchaseService(purchaseDao);
    }

    public void start() {
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = input.readInt("Enter your choice >> ");
            switch (choice) {
                case 1 -> runSafely(this::registerUser);
                case 2 -> runSafely(this::loginUser);
                case 3 -> runSafely(this::loginAdmin);
                case 4 -> runSafely(this::guestMenu);
                case 5 -> runSafely(this::viewProducts);
                case 6 -> runSafely(this::searchProducts);
                case 7 -> running = false;
                default -> System.out.println("Invalid choice! Please enter a valid option number.");
            }
        }
        System.out.println("Thank you for using Smart Shop. Goodbye!");
    }

    private void printMainMenu() {
        System.out.println();
        System.out.println("Welcome to E-Commerce Console Application");
        System.out.println("Please choose an option:");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Admin Login");
        System.out.println("4. Browse as Guest");
        System.out.println("5. View Products");
        System.out.println("6. Search Product");
        System.out.println("7. Exit");
    }

    private void registerUser() {
        System.out.println();
        System.out.println("Welcome to User Registration");
        String firstName = input.readRequiredLine("Enter First Name >> ");
        String lastName = input.readRequiredLine("Enter Last Name >> ");
        String username = input.readRequiredLine("Enter Username >> ");
        String password = input.readRequiredLine("Enter Password >> ");
        String city = input.readRequiredLine("Enter City >> ");
        String email = input.readRequiredLine("Enter Email ID >> ");
        String mobile = input.readRequiredLine("Enter Mobile Number >> ");

        System.out.println("Checking for duplicate username...");
        User user = authService.register(firstName, lastName, username, password, city, email, mobile);
        System.out.println("Registration successful. Your user ID is " + user.getUserId() + ".");
    }

    private void loginUser() {
        System.out.println();
        System.out.println("Login");
        String username = input.readRequiredLine("Enter Username >> ");
        String password = input.readRequiredLine("Enter Password >> ");

        System.out.println("Verifying credentials...");
        Optional<User> user = authService.login(username, password);
        if (user.isPresent()) {
            System.out.println("Login successful. Welcome, " + user.get().getFirstName() + "!");
            userMenu(user.get());
        } else {
            System.out.println("Login failed. Please check your username and password.");
        }
    }

    private void userMenu(User user) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println();
            System.out.println("User Operations");
            System.out.println("1. View Products");
            System.out.println("2. Search Product");
            System.out.println("3. View Product Details");
            System.out.println("4. Add Product to Cart");
            System.out.println("5. View Cart Items");
            System.out.println("6. View All Past Orders");
            System.out.println("7. Logout");
            int choice = input.readInt("Enter your choice >> ");

            switch (choice) {
                case 1 -> runSafely(this::viewProducts);
                case 2 -> runSafely(this::searchProducts);
                case 3 -> runSafely(this::viewProductDetails);
                case 4 -> runSafely(() -> addProductToCart(user));
                case 5 -> runSafely(() -> viewCartItems(user));
                case 6 -> runSafely(() -> viewPurchaseHistory(user));
                case 7 -> loggedIn = false;
                default -> System.out.println("Invalid choice! Please enter a valid option number.");
            }
        }
    }

    private void loginAdmin() {
        System.out.println();
        System.out.println("Admin Login");
        String username = input.readRequiredLine("Enter Username >> ");
        String password = input.readRequiredLine("Enter Password >> ");

        System.out.println("Verifying credentials...");
        Optional<Admin> admin = adminService.login(username, password);
        if (admin.isPresent()) {
            System.out.println("Admin login successful. Welcome, " + admin.get().getUsername() + "!");
            adminMenu();
        } else {
            System.out.println("Admin login failed. Please check your credentials.");
        }
    }

    private void adminMenu() {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println();
            System.out.println("Admin Operations");
            System.out.println("1. Add New Product");
            System.out.println("2. View Product Stock");
            System.out.println("3. View Products");
            System.out.println("4. Update Product Details");
            System.out.println("5. Delete Product from Inventory");
            System.out.println("6. View Registered Users");
            System.out.println("7. View User Purchase History");
            System.out.println("8. Logout");
            int choice = input.readInt("Enter your choice >> ");

            switch (choice) {
                case 1 -> runSafely(this::addNewProduct);
                case 2 -> runSafely(this::viewProductStock);
                case 3 -> runSafely(this::viewProducts);
                case 4 -> runSafely(this::updateProductDetails);
                case 5 -> runSafely(this::deleteProduct);
                case 6 -> runSafely(this::viewRegisteredUsers);
                case 7 -> runSafely(this::viewUserPurchaseHistory);
                case 8 -> loggedIn = false;
                default -> System.out.println("Invalid choice! Please enter a valid option number.");
            }
        }
    }

    private void guestMenu() {
        boolean browsing = true;
        while (browsing) {
            System.out.println();
            System.out.println("Guest Operations");
            System.out.println("You are browsing as a guest. Guests can only view products.");
            System.out.println("1. View Products");
            System.out.println("2. Search Product");
            System.out.println("3. View Product Details");
            System.out.println("4. Add to Cart");
            System.out.println("5. Back");
            int choice = input.readInt("Enter your choice >> ");

            switch (choice) {
                case 1 -> runSafely(this::viewProducts);
                case 2 -> runSafely(this::searchProducts);
                case 3 -> runSafely(this::viewProductDetails);
                case 4 -> System.out.println("Guests can only view products but are not allowed to make purchases.");
                case 5 -> browsing = false;
                default -> System.out.println("Invalid choice! Please enter a valid option number.");
            }
        }
    }

    private void viewProducts() {
        System.out.println();
        System.out.println("Displaying all products in sorted order:");
        ConsoleTables.printProducts(productService.listProducts());
    }

    private void searchProducts() {
        System.out.println();
        System.out.println("Search Products by Name or Keyword");
        String keyword = input.readRequiredLine("Enter product name or keyword to search >> ");
        System.out.println("Showing matching products:");
        ConsoleTables.printProducts(productService.searchProducts(keyword));
    }

    private void viewProductDetails() {
        System.out.println();
        System.out.println("View Product Details by ID");
        int productId = input.readInt("ID to view details >> ");
        Optional<Product> product = productService.findProductById(productId);
        if (product.isPresent()) {
            ConsoleTables.printProductDetails(product.get());
        } else {
            System.out.println("Invalid product ID. Product was not found.");
        }
    }

    private void addProductToCart(User user) {
        System.out.println();
        System.out.println("Add Product to Cart (Save as Purchase)");
        int productId = input.readInt("Enter the Product ID to purchase >> ");
        int quantity = input.readInt("Enter the Quantity >> ");
        System.out.println("Checking stock availability...");
        System.out.println("Adding product to your cart and saving purchase in database...");

        Purchase purchase = purchaseService.addProductToCart(user, productId, quantity);
        System.out.println("Product added successfully!");
        System.out.println("Purchase ID: " + purchase.getPurchaseId());
        System.out.println("Total Amount >> " + ConsoleTables.money(purchase.getTotalAmount()));
    }

    private void viewCartItems(User user) {
        System.out.println();
        System.out.println("View Cart Item");
        System.out.println("Fetching your cart/purchased items...");
        ConsoleTables.printCartItems(purchaseService.viewCartItems(user));
    }

    private void viewPurchaseHistory(User user) {
        System.out.println();
        System.out.println("View All Past Orders");
        System.out.println("Fetching your complete purchase history...");
        ConsoleTables.printPurchaseHistory(purchaseService.viewCartItems(user));
    }

    private void addNewProduct() {
        System.out.println();
        System.out.println("Add New Product");
        int productId = input.readInt("Enter New Product ID >> ");
        String name = input.readRequiredLine("Enter Product Name >> ");
        String description = input.readRequiredLine("Enter Product Description >> ");
        BigDecimal price = input.readBigDecimal("Enter Product Price >> ");
        int quantity = input.readInt("Enter Product Quantity >> ");
        System.out.println("Saving product to the database...");
        productService.addProduct(productId, name, description, price, quantity);
        System.out.println("Product saved successfully.");
    }

    private void viewProductStock() {
        System.out.println();
        System.out.println("View Product Stock");
        int productId = input.readInt("Enter Product ID to check stock >> ");
        Optional<Product> product = productService.findProductById(productId);
        if (product.isPresent()) {
            System.out.println("Available Quantity >> " + product.get().getQuantity());
        } else {
            System.out.println("Invalid product ID. Product was not found.");
        }
    }

    private void updateProductDetails() {
        System.out.println();
        System.out.println("Update Product Details");
        int productId = input.readInt("Enter the Product ID to update >> ");
        System.out.println("Select field to update:");
        System.out.println("1. Name");
        System.out.println("2. Description");
        System.out.println("3. Price");
        System.out.println("4. Quantity");
        int choice = input.readInt("Enter your choice >> ");

        switch (choice) {
            case 1 -> {
                String value = input.readRequiredLine("Enter new value >> ");
                System.out.println("Updating product in database...");
                productService.updateProductName(productId, value);
            }
            case 2 -> {
                String value = input.readRequiredLine("Enter new value >> ");
                System.out.println("Updating product in database...");
                productService.updateProductDescription(productId, value);
            }
            case 3 -> {
                BigDecimal value = input.readBigDecimal("Enter new value >> ");
                System.out.println("Updating product in database...");
                productService.updateProductPrice(productId, value);
            }
            case 4 -> {
                int value = input.readInt("Enter new value >> ");
                System.out.println("Updating product in database...");
                productService.updateProductQuantity(productId, value);
            }
            default -> {
                System.out.println("Invalid choice! Please enter a valid option number.");
                return;
            }
        }
        System.out.println("Product updated successfully.");
    }

    private void deleteProduct() {
        System.out.println();
        System.out.println("Delete Product from Inventory");
        int productId = input.readInt("Enter the Product ID to delete >> ");
        Optional<Product> product = productService.findProductById(productId);
        if (product.isEmpty()) {
            System.out.println("Invalid product ID. Product was not found.");
            return;
        }
        ConsoleTables.printProductDetails(product.get());
        boolean confirmed = input.readYesNo("Are you sure you want to delete this product? (Yes/No) >> ");
        if (!confirmed) {
            System.out.println("Deletion cancelled.");
            return;
        }

        System.out.println("Deleting product from database...");
        productService.deleteProduct(productId);
        System.out.println("Product deleted successfully. Existing purchase history is retained.");
    }

    private void viewRegisteredUsers() {
        System.out.println();
        System.out.println("View Registered Users");
        System.out.println("Displaying all registered users:");
        ConsoleTables.printUsers(adminService.listRegisteredUsers());
    }

    private void viewUserPurchaseHistory() {
        System.out.println();
        System.out.println("View User Purchase History");
        String username = input.readRequiredLine("Enter the Username to view their purchase history >> ");
        System.out.println("Fetching purchase data...");
        List<Purchase> purchases = purchaseService.viewUserPurchaseHistory(username);
        ConsoleTables.printAdminPurchaseHistory(purchases);
    }

    private void runSafely(Runnable action) {
        try {
            action.run();
        } catch (IllegalArgumentException | IllegalStateException exception) {
            System.out.println(exception.getMessage());
        } catch (DataAccessException exception) {
            System.out.println(exception.getMessage());
            if (exception.getCause() != null && exception.getCause().getMessage() != null) {
                System.out.println("Details: " + exception.getCause().getMessage());
            }
        } catch (Exception exception) {
            System.out.println("An unexpected error occurred: " + exception.getMessage());
        }
    }
}
