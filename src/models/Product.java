package models;

public class Product {
    private final int productId;
    private String productName;
    private String description;
    private double price;
    private int quantity;
    private String status; // Available, Out of Stock

    public Product(int productId, String productName, String description, double price, int quantity, String status) {
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.status = status;
    }

    // Getters
    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public String getStatus() { return status; }

    // Setters
    public void setProductName(String productName) { this.productName = productName; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(double price) { this.price = price; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setStatus(String status) { this.status = status; }

    public String toCSVLine() {
        return productId + "," + escape(productName) + "," + escape(description) + "," + 
               price + "," + quantity + "," + escape(status);
    }

    public String displayString() {
        return "Product ID: " + productId +
               "\nProduct Name: " + productName +
               "\nDescription: " + description +
               "\nPrice: $" + price +
               "\nQuantity: " + quantity +
               "\nStatus: " + status;
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace(",", " ");
    }
}