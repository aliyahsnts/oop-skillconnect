package managers;

import java.nio.file.*;
import java.util.*;
import models.Transaction;
import utils.CSVHelper;

public class TransactionManager {
    private final Path csvPath;
    private final String HEADER = "transactionId,userId,username,productId,productName,quantity,totalAmount,timestamp,status";
    private final List<Transaction> transactions = new ArrayList<>();

    public TransactionManager(String csvFilePath) {
        this.csvPath = Paths.get(csvFilePath);
        CSVHelper.ensureFileWithHeader(csvPath, HEADER);
        load();
    }

    private void load() {
        transactions.clear();
        List<String> lines = CSVHelper.readAllLines(csvPath);
        if (lines.size() <= 1) return;

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;
            String[] p = CSVHelper.split(line);
            if (p.length < 9) continue;

            try {
                int transId = Integer.parseInt(p[0].trim());
                int userId = Integer.parseInt(p[1].trim());
                String username = p[2].trim();
                int prodId = Integer.parseInt(p[3].trim());
                String prodName = p[4].trim();
                int qty = Integer.parseInt(p[5].trim());
                double amount = Double.parseDouble(p[6].trim());
                String timestamp = p[7].trim();
                String status = p[8].trim();
                transactions.add(new Transaction(transId, userId, username, prodId, prodName, qty, amount, timestamp, status));
            } catch (NumberFormatException e) {
                System.err.println("Skipping invalid transaction line: " + line);
            }
        }
    }

    public List<Transaction> findAll() {
        return new ArrayList<>(transactions);
    }

    public Transaction findById(int id) {
        return transactions.stream().filter(t -> t.getTransactionId() == id).findFirst().orElse(null);
    }

    public Transaction create(int userId, String username, int productId, String productName, int quantity, double totalAmount) {
        int id = nextId();
        String timestamp = Transaction.getCurrentTimestamp();
        Transaction t = new Transaction(id, userId, username, productId, productName, quantity, totalAmount, timestamp, "Completed");
        transactions.add(t);
        persist();
        return t;
    }

    public boolean updateStatus(int id, String newStatus) {
        Transaction t = findById(id);
        if (t == null) return false;
        t.setStatus(newStatus);
        persist();
        return true;
    }

    public int nextId() {
        return transactions.stream().mapToInt(Transaction::getTransactionId).max().orElse(3000) + 1;
    }

    private void persist() {
        List<String> out = new ArrayList<>();
        out.add(HEADER);
        for (Transaction t : transactions) {
            out.add(t.toCSVLine());
        }
        CSVHelper.writeAllLines(csvPath, out);
    }
}