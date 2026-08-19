package ui;

import java.math.BigDecimal;
import java.util.Scanner;

public class ConsoleInput {
    private final Scanner scanner;

    public ConsoleInput(Scanner scanner) {
        this.scanner = scanner;
    }

    public String readLine(String prompt) {
        System.out.print(prompt);
        if (!scanner.hasNextLine()) {
            throw new IllegalStateException("Input stream closed.");
        }
        return scanner.nextLine().trim();
    }

    public String readRequiredLine(String prompt) {
        while (true) {
            String value = readLine(prompt);
            if (!value.isBlank()) {
                return value;
            }
            System.out.println("Input cannot be empty.");
        }
    }

    public int readInt(String prompt) {
        while (true) {
            String value = readRequiredLine(prompt);
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException exception) {
                System.out.println("Invalid input! Please enter numeric values only.");
            }
        }
    }

    public BigDecimal readBigDecimal(String prompt) {
        while (true) {
            String value = readRequiredLine(prompt);
            try {
                return new BigDecimal(value);
            } catch (NumberFormatException exception) {
                System.out.println("Invalid input! Please enter a valid amount.");
            }
        }
    }

    public boolean readYesNo(String prompt) {
        while (true) {
            String value = readRequiredLine(prompt);
            if (value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("y")) {
                return true;
            }
            if (value.equalsIgnoreCase("no") || value.equalsIgnoreCase("n")) {
                return false;
            }
            System.out.println("Please enter Yes or No.");
        }
    }
}
