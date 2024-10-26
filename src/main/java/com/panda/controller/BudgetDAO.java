package com.panda.controller;


//used
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//used classes
import com.panda.model.Budget;
import com.panda.utils.DatabaseConnection;

public class BudgetDAO {
    private Connection connection;
    
    
    public BudgetDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    // public Budget getBudgetByCategoryId(int categoryId) throws SQLException {
    //     String query = "SELECT id, amount FROM budget WHERE category_id = ?";
    //     try (PreparedStatement statement = connection.prepareStatement(query)) {
    //         statement.setInt(1, categoryId);
    //         ResultSet resultSet = statement.executeQuery();

    //         if (resultSet.next()) {
    //             int budgetId = resultSet.getInt("id");
    //             double amount = resultSet.getDouble("amount");
    //             return new Budget(budgetId, amount, categoryId); // Return the budget if it exists
    //         }
    //     }
    //     return null; // Return null if no budget exists for the category
    // }

    // user get
    public Budget getUserBudgetByCatId(int categoryId) throws SQLException {
        String query = "SELECT * FROM budget WHERE category_id =? AND user_id =?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            int userId = SessionManager.getCurrentSession().getUserId();
            statement.setInt(1, categoryId);
            statement.setInt(2, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int budgetId = resultSet.getInt("id");
                double amount = resultSet.getDouble("amount");
                return new Budget(budgetId, amount, categoryId); // Return the budget if it exists
            }
        }
        return null; // Return null if no budget exists for the user
    }

    // Method to insert a new budget - delete later
    public void insertBudget(Budget budget) throws SQLException {
        String query = "INSERT INTO budget (amount, category_id) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDouble(1, budget.getAmount());
            statement.setInt(2, budget.getCategoryId());
            statement.executeUpdate();
        }
    }

    // insert budget for user
    public void insertUserBudget( Budget budget) throws SQLException {
        String query = "INSERT INTO budget (amount, category_id, user_id) VALUES (?,?,?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            int userId = SessionManager.getCurrentSession().getUserId();
            statement.setInt(1, userId);
            statement.setDouble(2, budget.getAmount());
            statement.setInt(3, budget.getCategoryId());
            statement.executeUpdate();
        }
    }

    // Method to update an existing budget  old- delete later
    // public void updateBudget(Budget budget) throws SQLException {
    //     String query = "UPDATE budget SET amount = ? WHERE id = ?";
    //     try (PreparedStatement statement = connection.prepareStatement(query)) {
    //         statement.setDouble(1, budget.getAmount());
    //         statement.setInt(2, budget.getBudgetId());
    //         statement.executeUpdate();
    //     }
    // }
    public void updateUserBudget( Budget budget) throws SQLException {
        String query = "UPDATE budget SET amount =? WHERE user_id =? AND category_id =?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            int userId = SessionManager.getCurrentSession().getUserId();
            statement.setDouble(1, budget.getAmount());
            statement.setInt(2, userId);
            statement.setInt(3, budget.getCategoryId());
            statement.executeUpdate();
        }
    }
    // public void updateUserBudget(int userId,

    // Method to either insert or update based on whether the budget exists
    // public void saveBudget(Budget budget) throws SQLException {
    //     Budget existingBudget = getUserBudgetByCatId(budget.getCategoryId());
    //     if (existingBudget != null) {
    //         // Update the existing budget
    //         existingBudget.setAmount(budget.getAmount());
    //         updateUserBudget(existingBudget);
    //     } else {
    //         // Insert a new budget
    //         insertUserBudget(budget);
    //     }
    // }
    public void saveUserBudget(Budget budget) throws SQLException {
        Budget existingBudget = getUserBudgetByCatId(budget.getCategoryId());
        if (existingBudget!= null) {
            // Update the existing budget
            existingBudget.setAmount(budget.getAmount());
            updateUserBudget(existingBudget);
        } else {
            // Insert a new budget
            insertUserBudget( budget);
        }
    }

    public List<Budget> getAllBudget() throws SQLException {
        String sql = "SELECT * FROM budget";
        List<Budget> budgets = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Budget budget = new Budget(
                        rs.getInt("id"),
                        rs.getDouble("amount"),
                        rs.getInt("category_id")

                );
                budgets.add(budget);
            }
        }
        return budgets;
    }

    public List<Budget> getUserBudgets() throws SQLException {
        String sql = "SELECT * FROM budget WHERE user_id =?";
        List<Budget> budgets = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            int userId = SessionManager.getCurrentSession().getUserId();
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Budget budget = new Budget(
                    rs.getInt("id"),
                    rs.getDouble("amount"),
                    rs.getInt("category_id")
                );
                budgets.add(budget);
            }
        }
        return budgets;
    }
}