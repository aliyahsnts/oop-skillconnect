package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private final int transactionId;
    private final int userId;
    private final String username;
    private final int productId;
    private final String productName;
    private final int quantity;
    private final double totalAmount;
    private final String timestamp;
    private String status; // Completed, Pending, Cancelled

    public Transaction(int transactionId, int userId, String username, int productId, 
                      String productName, int quantity, double totalAmount, String timestamp, String status) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.username = username;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.timestamp = timestamp;
        this.status = status;
    }

    // Getters
    public int getTransactionId() { return transactionId; }
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public double getTotalAmount() { return totalAmount; }
    public String getTimestamp() { return timestamp; }
    public String getStatus() { return status; }

    // Setter
    public void setStatus(String status) { this.status = status; }

    public static String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String toCSVLine() {
        return transactionId + "," + userId + "," + escape(username) + "," + productId + "," + 
               escape(productName) + "," + quantity + "," + totalAmount + "," + 
               escape(timestamp) + "," + escape(status);
    }

    public String displayString() {
        return "Transaction ID: " + transactionId +
               "\nUser: " + username + " (ID: " + userId + ")" +
               "\nProduct: " + productName + " (ID: " + productId + ")" +
               "\nQuantity: " + quantity +
               "\nTotal Amount: $" + totalAmount +
               "\nTimestamp: " + timestamp +
               "\nStatus: " + status;
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace(",", " ");
    }
}