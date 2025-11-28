package managers;

import models.Transaction;
import models.User;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionManager extends BaseManager<Transaction> {
    private final UserManager um; // new dependency

    public TransactionManager(String csvFilePath) {
        super(csvFilePath, "transactionId,userId,username,productId,productName,quantity,totalAmount,timestamp,status");
        this.um = null;
    }

    // Overloaded constructor to inject UserManager so transactions can update balances
    public TransactionManager(String csvFilePath, UserManager um) {
        super(csvFilePath, "transactionId,userId,username,productId,productName,quantity,totalAmount,timestamp,status");
        this.um = um;
    }
    // =========================================
    //        IMPLEMENT ABSTRACT METHODS
    // =========================================

    @Override
    protected Transaction parseEntity(String[] parts) {
        int transId = Integer.parseInt(parts[0].trim());
        int userId = Integer.parseInt(parts[1].trim());
        String username = parts[2].trim();
        int prodId = Integer.parseInt(parts[3].trim());
        String prodName = parts[4].trim();
        int qty = Integer.parseInt(parts[5].trim());
        double amount = Double.parseDouble(parts[6].trim());
        String timestamp = parts[7].trim();
        String status = parts[8].trim();
        
        return new Transaction(transId, userId, username, prodId, 
                              prodName, qty, amount, timestamp, status);
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
        return 9;
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
    //           CREATE & UPDATE
    // =========================================

    public Transaction create(int userId, String username, int productId, 
                             String productName, int quantity, double totalAmount) {
        int id = nextId();
        String timestamp = Transaction.getCurrentTimestamp();
        Transaction t = new Transaction(id, userId, username, productId, 
                                       productName, quantity, totalAmount, 
                                       timestamp, "Completed");
        entities.add(t);
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
    
    public List<Transaction> findByUserId(int userId) {
    return entities.stream()
            .filter(tx -> tx.getUserId() == userId)
            .collect(Collectors.toList());
}

    // =========================================
    //      DEPOSIT / WITHDRAW / TRANSFER
    // =========================================

    public boolean deposit(int userId, double amount, String description) {
        if (amount <= 0 || um == null) return false;
        User u = um.findById(userId);
        if (u == null) return false;

        // update balance first; ensure persistence succeeds
        boolean adjusted = um.adjustBalance(userId, amount);
        if (!adjusted) return false;

        // record deposit as a transaction
        create(userId, u.getFullName(), 0, description == null ? "Deposit" : description, 0, amount);
        return true;
    }

    public boolean withdraw(int userId, double amount, String description) {
        if (amount <= 0 || um == null) return false;
        User u = um.findById(userId);
        if (u == null) return false;

        // verify sufficient funds
        double balance = um.getBalance(userId);
        if (balance < amount) return false;

        // update balance first
        boolean adjusted = um.adjustBalance(userId, -amount);
        if (!adjusted) return false;

        // record withdrawal as a negative-amount transaction
        create(userId, u.getFullName(), 0, description == null ? "Withdraw" : description, 0, -amount);
        return true;
    }

    public boolean transfer(int fromUserId, int toUserId, double amount, String description) {
        if (amount <= 0 || um == null) return false;
        User from = um.findById(fromUserId);
        User to = um.findById(toUserId);
        if (from == null || to == null) return false;

        double fromBalance = um.getBalance(fromUserId);
        if (fromBalance < amount) return false;

        // perform debit then credit; rollback if credit fails
        if (!um.adjustBalance(fromUserId, -amount)) return false;
        if (!um.adjustBalance(toUserId, amount)) {
            um.adjustBalance(fromUserId, amount); // rollback
            return false;
        }

        // create both transactions
        create(fromUserId, from.getFullName(), 0,
               "Transfer to " + to.getFullName() + (description != null ? ": " + description : ""),
               0, -amount);

        create(toUserId, to.getFullName(), 0,
               "Transfer from " + from.getFullName() + (description != null ? ": " + description : ""),
               0, amount);

        return true;
    }
}