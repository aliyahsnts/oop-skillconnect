package ui.handlers;

import models.Product;
import models.Jobseeker;
import managers.ProductManager;
import utils.MenuPrinter;
import utils.AsciiTable;

import java.util.List;
import java.util.Scanner;

public class MarketplaceHandler {
    private final Jobseeker jobseeker;
    private final ProductManager pm;
    private final Scanner scanner;

    public MarketplaceHandler(Jobseeker jobseeker, ProductManager pm, Scanner scanner) {
        this.jobseeker = jobseeker;
        this.pm = pm;
        this.scanner = scanner;
    }

    public void showMenu() {
        while (true) {
            MenuPrinter.printHeader("MARKETPLACE");
            System.out.printf("  Wallet Balance: $%.2f%n%n", jobseeker.getMoney());
            MenuPrinter.printOption("1", "View All Products");
            MenuPrinter.printOption("2", "Purchase Product");
            MenuPrinter.printOption("0", "Back");
            MenuPrinter.prompt("Enter choice");

            switch (readInt()) {
                case 1 -> viewAllProducts();
                case 2 -> purchaseProduct();
                case 0 -> { return; }
                default -> MenuPrinter.error("Invalid selection.");
            }
        }
    }

    private void viewAllProducts() {
        List<Product> products = pm.findAll().stream()
                                   .filter(p -> p.getStatus().equalsIgnoreCase("Available"))
                                   .toList();

        if (products.isEmpty()) {
            MenuPrinter.info("No products available.");
            MenuPrinter.pause();
            return;
        }

        /* bullet-proof table */
        AsciiTable.print(products,
                new String[]{"ID", "Product Name", "Price", "Qty", "Status"},
                new int[]{4, 26, 8, 5, 12},
                p -> new String[]{
                        String.valueOf(p.getProductId()),
                        p.getProductName(),
                        String.format("$%.2f", p.getPrice()),
                        String.valueOf(p.getQuantity()),
                        p.getStatus()
                });
        MenuPrinter.pause();
    }

    private void purchaseProduct() {
        viewAllProducts();

        MenuPrinter.prompt("Enter Product ID (or 0 to cancel)");
        int id = readInt();
        if (id == 0) return;

        Product product = pm.findById(id);
        if (product == null || !product.getStatus().equalsIgnoreCase("Available")) {
            MenuPrinter.error("Product not found or not available.");
            return;
        }

        MenuPrinter.prompt("Enter quantity");
        int qty = readInt();

        if (qty <= 0) {
            MenuPrinter.error("Invalid quantity.");
            return;
        }
        if (qty > product.getQuantity()) {
            MenuPrinter.error("Not enough stock. Available: " + product.getQuantity());
            return;
        }

        double total = product.getPrice() * qty;
        if (jobseeker.getMoney() < total) {
            MenuPrinter.error("Insufficient funds. Total cost: $" + total);
            return;
        }

        /* process purchase */
        jobseeker.setMoney(jobseeker.getMoney() - total);
        pm.update(id, null, null, null, product.getQuantity() - qty, null);

        MenuPrinter.success("Purchase completed!");
        MenuPrinter.info("Product : " + product.getProductName());
        MenuPrinter.info("Quantity: " + qty);
        MenuPrinter.info("Total   : $" + total);
        MenuPrinter.info("Balance : $" + jobseeker.getMoney());
        MenuPrinter.pause();
    }

    private int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                MenuPrinter.error("Please enter a valid number.");
            }
        }
    }
}