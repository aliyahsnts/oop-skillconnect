package managers;

import models.Product;

public class ProductManager extends BaseManager<Product> {

    public ProductManager(String csvFilePath) {
        super(csvFilePath, "productId,productName,description,price,quantity,status");
    }

    // =========================================
    //        IMPLEMENT ABSTRACT METHODS
    // =========================================

    @Override
    protected Product parseEntity(String[] parts) {
        int id = Integer.parseInt(parts[0].trim());
        String name = parts[1].trim();
        String desc = parts[2].trim();
        double price = Double.parseDouble(parts[3].trim());
        int qty = Integer.parseInt(parts[4].trim());
        String status = parts[5].trim();
        
        return new Product(id, name, desc, price, qty, status);
    }

    @Override
    protected String toCSVLine(Product product) {
        return product.toCSVLine();
    }

    @Override
    protected int getId(Product product) {
        return product.getProductId();
    }

    @Override
    protected int getMinimumColumns() {
        return 6;
    }

    @Override
    protected String getEntityName() {
        return "product";
    }

    @Override
    protected int getStartingId() {
        return 1001;
    }

    // =========================================
    //           CREATE & UPDATE
    // =========================================

    public Product create(String name, String description, double price, int quantity) {
        int id = nextId();
        String status = quantity > 0 ? "Available" : "Out of Stock";
        Product p = new Product(id, name, description, price, quantity, status);
        entities.add(p);
        persist();
        return p;
    }

    public boolean update(int id, String name, String description, 
                         Double price, Integer quantity, String status) {
        Product p = findById(id);
        if (p == null) return false;
        
        if (name != null && !name.isEmpty()) 
            p.setProductName(name);
        if (description != null && !description.isEmpty()) 
            p.setDescription(description);
        if (price != null) 
            p.setPrice(price);
        if (quantity != null) {
            p.setQuantity(quantity);
            p.setStatus(quantity > 0 ? "Available" : "Out of Stock");
        }
        if (status != null && !status.isEmpty()) 
            p.setStatus(status);
            
        persist();
        return true;
    }
}