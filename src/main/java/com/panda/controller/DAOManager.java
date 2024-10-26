package com.panda.controller;

        //package panda.financeapp.controller;



public class DAOManager {
    private TransactionDAO transactionDAO;
    private CategoryDAO categoryDAO;
    private BudgetDAO budgetDAO;
    private ChartsDataFetch chartsDataFetch;
    private UserDAO userDAO;

    public DAOManager() {
        this.transactionDAO = new TransactionDAO();
        this.categoryDAO = new CategoryDAO();
        this.chartsDataFetch = new ChartsDataFetch();
        this.userDAO = new UserDAO();
        this.budgetDAO = new BudgetDAO();
    }
    
    public TransactionDAO getTransactionDAO() {
        return transactionDAO;
    }

    public CategoryDAO getCategoryDAO() {
        return categoryDAO;
    }
    public BudgetDAO getBudgetDAO(){
        return budgetDAO;
    }
    public ChartsDataFetch getChartsData(){
        return chartsDataFetch;
    }
    public UserDAO getUserDAO(){
        return userDAO;
    }
    
}