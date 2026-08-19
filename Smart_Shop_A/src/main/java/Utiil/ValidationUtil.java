package Utiil;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class ValidationUtil {

	 private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
	    private static final Pattern MOBILE_PATTERN = Pattern.compile("^\\d{10}$");

	    private ValidationUtil() {
	    }

	    public static boolean isBlank(String value) {
	        return value == null || value.trim().isEmpty();
	    }

	    public static String requireText(String label, String value, int maxLength) {
	        if (isBlank(value)) {
	            throw new IllegalArgumentException(label + " cannot be empty.");
	        }
	        String trimmed = value.trim();
	        if (trimmed.length() > maxLength) {
	            throw new IllegalArgumentException(label + " must be " + maxLength + " characters or fewer.");
	        }
	        return trimmed;
	    }

	    public static void requireEmail(String email) {
	        if (!EMAIL_PATTERN.matcher(email).matches()) {
	            throw new IllegalArgumentException("Please enter a valid email address.");
	        }
	    }

	    public static void requireMobile(String mobile) {
	        if (!MOBILE_PATTERN.matcher(mobile).matches()) {
	            throw new IllegalArgumentException("Mobile number must be 10 digits.");
	        }
	    }

	    public static void requirePositiveQuantity(int quantity) {
	        if (quantity <= 0) {
	            throw new IllegalArgumentException("Quantity must be greater than zero.");
	        }
	    }

	    public static void requireNonNegativeQuantity(int quantity) {
	        if (quantity < 0) {
	            throw new IllegalArgumentException("Quantity cannot be negative.");
	        }
	    }

	    public static void requireNonNegativePrice(BigDecimal price) {
	        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
	            throw new IllegalArgumentException("Price cannot be negative.");
	        }
	    }
}
