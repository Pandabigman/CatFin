package com.panda.controller;

//outdated version
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
//
//import panda.financeapp.utils.DatabaseConnection;

import com.panda.utils.DatabaseConnection;

public class ChartsDataFetch {
    private Connection connection;
    int userID = SessionManager.getCurrentSession().getUserId();

    public ChartsDataFetch(){
        this.connection=DatabaseConnection.getConnection();
    }

    public ChartsDataFetch (Connection connection){
        this.connection = connection;
    }
    // from budget table, return the amount for each and the category name referenced by its category_id foreign key
    public Map<String, Double> getBudgetData () throws Exception{
        Map<String, Double> budgetData = new HashMap<>();
        //get category name and budget amount by joining cat and budget by foreign key using user id
        String query = "SELECT c.name, b.amount " +
                        "FROM budget b " +
                        "JOIN categories c ON b.category_id = c.id " +
                        "WHERE b.user_id = " + userID + ";";
        
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        
        while (resultSet.next()) {
            String categoryName = resultSet.getString("name");
            double totalbudget = resultSet.getDouble("amount");
            budgetData.put(categoryName, totalbudget);
        }
        
        resultSet.close();
        statement.close();
        return budgetData;
    }

    // return all income String query =" Select "
    
    //from tacsactions table, select all that have type=expense, group by category id and return the category name which is in a categories table and sum of amount for items with the same category id
    public Map <String, Double> getExpenseData() throws Exception{
        Map<String,Double> expenseData =new HashMap<>();
        //get category name and sum of amount for items with the same category id 
        String query = "SELECT c.name, SUM(t.amount) AS total_amount FROM transactions t JOIN categories c ON t.category_id = c.id "+
                        "WHERE t.type = 'expense' AND user_id = " +userID +" GROUP BY t.category_id;";
        
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        
        while (resultSet.next()) {
            String categoryName = resultSet.getString("name");
            double totalExpense = resultSet.getDouble("total_amount");
            expenseData.put(categoryName, totalExpense);
        }
                        
        resultSet.close();
        statement.close();
        return expenseData;
                    
    }
    //reuses old functions and parses results to return tips from them
    public Map<String, String> getCategoryTips() throws Exception {
        Map<String, String> categoryTips = new HashMap<>();
        Map<String, Double> budgetData = getBudgetData();
        Map<String, Double> expenseData = getExpenseData();
    
        for (String category : budgetData.keySet()) {
            double budget = budgetData.get(category);
            double expense = expenseData.getOrDefault(category, 0.0);
    
            if (expense > budget) {
                categoryTips.put(category, "You have exceeded your budget for " + category + ". :-(");
            } else if (expense > 0.8 * budget) {
                categoryTips.put(category, "You are approaching your budget limit for " + category + ". Slow down Bucko.-_-");
            } else {
                categoryTips.put(category, "You are on track for " + category + ". KEEP IT UP! ;-)");
            }
        }
        return categoryTips;
    }

    public Map<String, String> getTransactionInsights() throws Exception {
        Map<String, String> transactionInsights = new HashMap<>();
        String query = "SELECT c.name, SUM(t.amount) AS total_amount " +
                        "FROM transactions t " +
                        "JOIN categories c ON t.category_id = c.id " +
                        "WHERE t.user_id = " + userID + " " +
                        "GROUP BY t.category_id;";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        double highestExpense = 0.0;
        String highestExpenseCategory = "";
        double lowestExpense = Double.MAX_VALUE;
        String lowestExpenseCategory = "";

        while (resultSet.next()) {
            String categoryName = resultSet.getString("name");
            double totalAmount = resultSet.getDouble("total_amount");

            if (totalAmount > highestExpense) {
                highestExpense = totalAmount;
                highestExpenseCategory = categoryName;
            }

            if (totalAmount < lowestExpense) {
                lowestExpense = totalAmount;
                lowestExpenseCategory = categoryName;
            }
        }

        transactionInsights.put(highestExpenseCategory,"Highest Expense Category "+highestExpenseCategory);
        transactionInsights.put(String.valueOf(highestExpense),"Highest Expense Amount £"+String.valueOf(highestExpense));
        transactionInsights.put(lowestExpenseCategory,"Lowest Expense Category "+ lowestExpenseCategory);
        transactionInsights.put(String.valueOf(lowestExpense),"Lowest Expense Amount £"+ String.valueOf(lowestExpense));

        resultSet.close();
        statement.close();
        return transactionInsights;
    }

}