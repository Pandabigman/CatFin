package com.panda.controller;




import java.sql.*;
import java.util.ArrayList;
import java.util.List;
//import java.util.stream.Collectors;

import com.panda.model.Transaction;
import com.panda.utils.DatabaseConnection;

public class TransactionDAO {
    private Connection connection;
    //Double totalSpend;

    public TransactionDAO() {
        // Use the DatabaseConnection class to get the connection
        this.connection = DatabaseConnection.getConnection();
    }

    public void addTransaction(Transaction transaction) throws SQLException {
        String sql = "INSERT INTO transactions (description, amount, date, category_id,type,user_id) VALUES (?, ?, ?,?, ?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            int userId = SessionManager.getCurrentSession().getUserId();  // Get the current user's ID from the session manager
            stmt.setString(1, transaction.getDescription());
            stmt.setDouble(2, transaction.getAmount());
            stmt.setDate(3, new java.sql.Date(transaction.getDate().getTime()));
            stmt.setInt(4, transaction.getCategoryId());
            stmt.setString(5, transaction.getType());
            stmt.setInt(6, userId);
            stmt.executeUpdate();
        }
        // update user balance
        UserDAO userDAO = new UserDAO();
        double balance = userDAO.getBalance();

        if (transaction.getType() == "expense"){
            balance -= transaction.getAmount();
        } else {
            balance += transaction.getAmount();}
        userDAO.setBalance(balance);
    }

    public List<Transaction> getAllUserTransactions()throws SQLException {
        String sql = "SELECT * FROM transactions WHERE user_id =?";
        List<Transaction> transactions = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            int userId = SessionManager.getCurrentSession().getUserId();
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Transaction transaction = new Transaction(
                    rs.getInt("id"),
                    rs.getString("description"),
                    rs.getDouble("amount"),
                    rs.getDate("date"),
                    rs.getInt("category_id"),
                    rs.getString("type")
                );
                transactions.add(transaction);
            }
        }
        return transactions;
    }

    public Double getUserCategorySpend( int category_Id) throws SQLException {
        String sql =" SELECT sum(amount) FROM transactions WHERE user_id = ? AND category_id =? AND type = 'expense'";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            int userId = SessionManager.getCurrentSession().getUserId();
            stmt.setInt(1, userId);
            stmt.setInt(2, category_Id);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()){
                return rs.getDouble("sum(amount)");
            }
            
            return 0.0;
        }
    }
    public void deleteTransaction(Transaction tx) throws SQLException {
        String sql = "DELETE FROM transactions WHERE id = ? AND user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            int userId = SessionManager.getCurrentSession().getUserId();
            stmt.setInt(1, tx.getId());
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
        // update user balance
        UserDAO userDAO = new UserDAO();
        double balance = userDAO.getBalance();
        
        if (tx.getType() == "expense"){
            balance+= tx.getAmount();
        } else {
            balance -= tx.getAmount();}
            
        userDAO.setBalance(balance);
    }
    //can be used in deleted transactions table, after restoring them
//     public void deleteRowsByIds(List<Integer> ids) throws SQLException {
//     String sql = "DELETE FROM transactions WHERE id IN (";
//     // Create placeholders for the IDs
//     sql += ids.stream().map(id -> "?").collect(Collectors.joining(", "));
//     sql += ")";
//     try (Connection connection = DatabaseConnection.getConnection();
//          PreparedStatement statement = connection.prepareStatement(sql)) {
//         // Set the ID values in the PreparedStatement
//         for (int i = 0; i < ids.size(); i++) {
//             statement.setInt(i + 1, ids.get(i));
//         }
        
//         // Execute the deletion
//         statement.executeUpdate();
//     }
// }

    public void deleteAllTransactions() throws SQLException {
        String sql = "DELETE FROM transactions where userId = ?";
        int userId = SessionManager.getCurrentSession().getUserId();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.executeUpdate();
        }
        // update user balance
        UserDAO userDAO = new UserDAO();
        userDAO.setBalance(0);
    }
}