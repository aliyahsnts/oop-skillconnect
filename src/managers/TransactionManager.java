package managers;

import models.Transaction;

public class TransactionManager extends BaseManager<Transaction> {

    public TransactionManager(String csvFilePath) {
        super(csvFilePath, "transactionId,userId,username,productId,productName,quantity,totalAmount,timestamp,status");
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
}