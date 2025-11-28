package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import models.*;
import managers.*;

public class Transaction {
    private int transactionId;
    private int fromUserId;
    private String fromUsername;
    private int toUserId;
    private String toUsername;
    private double amount;
    private String type; // DEPOSIT, WITHDRAW, TRANSFER, PURCHASE, SALARY
    private String description;
    private String timestamp;
    private String status;

    // Constructor
    public Transaction(int transactionId, int fromUserId, String fromUsername,
                      int toUserId, String toUsername, double amount,
                      String type, String description, String timestamp, String status) {
        this.transactionId = transactionId;
        this.fromUserId = fromUserId;
        this.fromUsername = fromUsername;
        this.toUserId = toUserId;
        this.toUsername = toUsername;
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.timestamp = timestamp;
        this.status = status;
    }

    // Getters and Setters
    public int getTransactionId() { return transactionId; }
    public int getFromUserId() { return fromUserId; }
    public String getFromUsername() { return fromUsername; }
    public int getToUserId() { return toUserId; }
    public String getToUsername() { return toUsername; }
    public double getAmount() { return amount; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public String getTimestamp() { return timestamp; }
    public String getStatus() { return status; }
    
    public void setStatus(String status) { this.status = status; }

    // CSV conversion
    public String toCSVLine() {
        return String.format("%d,%d,%s,%d,%s,%.2f,%s,%s,%s,%s",
            transactionId, fromUserId, fromUsername, toUserId, toUsername,
            amount, type, description.replace(",", ";"), timestamp, status);
    }

    // Display format
    public String displayString() {
        return String.format("""
            Transaction ID: %d
            Type: %s
            From: %s (ID: %d)
            To: %s (ID: %d)
            Amount: $%.2f
            Description: %s
            Timestamp: %s
            Status: %s
            """, transactionId, type, fromUsername, fromUserId,
            toUsername, toUserId, amount, description, timestamp, status);
    }

    public static String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}

