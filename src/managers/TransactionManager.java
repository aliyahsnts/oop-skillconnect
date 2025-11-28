// ============================================
// 2. Updated TransactionManager.java
// ============================================
package managers;

import models.Transaction;
import models.User;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionManager extends BaseManager<Transaction> {
    private UserManager um;

    public TransactionManager(String csvFilePath) {
        super(csvFilePath, "transactionId,fromUserId,fromUsername,toUserId,toUsername,amount,type,description,timestamp,status");
        this.um = null;
    }

    // Method to inject UserManager after construction
    public void setUserManager(UserManager um) {
        this.um = um;
    }

    @Override
    protected Transaction parseEntity(String[] parts) {
        int transId = Integer.parseInt(parts[0].trim());
        int fromUserId = Integer.parseInt(parts[1].trim());
        String fromUsername = parts[2].trim();
        int toUserId = Integer.parseInt(parts[3].trim());
        String toUsername = parts[4].trim();
        double amount = Double.parseDouble(parts[5].trim());
        String type = parts[6].trim();
        String description = parts[7].trim();
        String timestamp = parts[8].trim();
        String status = parts[9].trim();
        
        return new Transaction(transId, fromUserId, fromUsername, toUserId, 
                              toUsername, amount, type, description, timestamp, status);
    }

    @Override
    protected String toCSVLine(Transaction transaction) {
        return transaction.toCSVLine();
    }

    @Override
    protected int getId(Transaction transaction) {
        return transaction.getTransactionId();
    }

    @Override
    protected int getMinimumColumns() {
        return 10;
    }

    @Override
    protected String getEntityName() {
        return "transaction";
    }

    @Override
    protected int getStartingId() {
        return 3001;
    }

    // =========================================
    //           TRANSACTION OPERATIONS
    // =========================================

    public boolean deposit(int userId, double amount, String description) {
        if (amount <= 0 || um == null) return false;
        User user = um.findById(userId);
        if (user == null) return false;

        // Update balance
        boolean adjusted = um.adjustBalance(userId, amount);
        if (!adjusted) return false;

        // Create transaction record
        int id = nextId();
        String timestamp = Transaction.getCurrentTimestamp();
        Transaction t = new Transaction(
            id, 0, "SYSTEM", userId, user.getFullName(),
            amount, "DEPOSIT", description != null ? description : "Deposit",
            timestamp, "Completed"
        );
        entities.add(t);
        persist();
        return true;
    }

    public boolean withdraw(int userId, double amount, String description) {
        if (amount <= 0 || um == null) return false;
        User user = um.findById(userId);
        if (user == null) return false;

        // Check sufficient balance
        double balance = um.getBalance(userId);
        if (balance < amount) return false;

        // Update balance
        boolean adjusted = um.adjustBalance(userId, -amount);
        if (!adjusted) return false;

        // Create transaction record
        int id = nextId();
        String timestamp = Transaction.getCurrentTimestamp();
        Transaction t = new Transaction(
            id, userId, user.getFullName(), 0, "SYSTEM",
            amount, "WITHDRAW", description != null ? description : "Withdrawal",
            timestamp, "Completed"
        );
        entities.add(t);
        persist();
        return true;
    }

    public boolean transfer(int fromUserId, int toUserId, double amount, String description) {
        if (amount <= 0 || um == null) return false;
        User from = um.findById(fromUserId);
        User to = um.findById(toUserId);
        if (from == null || to == null) return false;

        // Check sufficient balance
        double fromBalance = um.getBalance(fromUserId);
        if (fromBalance < amount) return false;

        // Perform transfer
        if (!um.adjustBalance(fromUserId, -amount)) return false;
        if (!um.adjustBalance(toUserId, amount)) {
            um.adjustBalance(fromUserId, amount); // rollback
            return false;
        }

        // Create transaction record
        int id = nextId();
        String timestamp = Transaction.getCurrentTimestamp();
        Transaction t = new Transaction(
            id, fromUserId, from.getFullName(), toUserId, to.getFullName(),
            amount, "TRANSFER", description != null ? description : "Transfer",
            timestamp, "Completed"
        );
        entities.add(t);
        persist();
        return true;
    }

    public boolean recordPurchase(int buyerId, String buyerName, int productId, 
                                 String productName, int quantity, double totalAmount) {
        if (um == null) return false;
        
        int id = nextId();
        String timestamp = Transaction.getCurrentTimestamp();
        String description = String.format("Purchased %dx %s", quantity, productName);
        
        Transaction t = new Transaction(
            id, buyerId, buyerName, 0, "MARKETPLACE",
            totalAmount, "PURCHASE", description,
            timestamp, "Completed"
        );
        entities.add(t);
        persist();
        return true;
    }

    public boolean recordSalary(int fromRecruiterId, String recruiterName,
                               int toJobseekerId, String jobseekerName,
                               double amount, String jobName) {
        if (um == null) return false;
        
        int id = nextId();
        String timestamp = Transaction.getCurrentTimestamp();
        String description = String.format("Salary for: %s", jobName);
        
        Transaction t = new Transaction(
            id, fromRecruiterId, recruiterName, toJobseekerId, jobseekerName,
            amount, "SALARY", description,
            timestamp, "Completed"
        );
        entities.add(t);
        persist();
        return true;
    }

    // Query methods
    public List<Transaction> findByUserId(int userId) {
        return entities.stream()
                .filter(tx -> tx.getFromUserId() == userId || tx.getToUserId() == userId)
                .collect(Collectors.toList());
    }

    public List<Transaction> findByFromUserId(int userId) {
        return entities.stream()
                .filter(tx -> tx.getFromUserId() == userId)
                .collect(Collectors.toList());
    }

    public List<Transaction> findByToUserId(int userId) {
        return entities.stream()
                .filter(tx -> tx.getToUserId() == userId)
                .collect(Collectors.toList());
    }

    public List<Transaction> findByType(String type) {
        return entities.stream()
                .filter(tx -> tx.getType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }
}