package com.panda.model;

public class User {
    private int id;
    private String email;
    private double income;
    private double balance;
    private String username;

    public User(int id, String email,String username) {
        this.id = id;
        this.email = email;
        this.income = 0;
        this.balance = 0;
    }
    public User(int id, String email, double income,String username) {
        this.id = id;
        this.email = email;
        this.income = income;
        this.balance = 0;
    }


    public User(int id, String email, double income, double balance, String username) {
        this.id = id;
        this.email = email;
        this.income = income;
        this.balance = balance;
    }

    // Getters and setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public double getIncome() {
        return income;
    }
    
    public void setIncome(double income) {
        this.income = income;
    }
    
    public double getBalance() {
        return balance;
    }
    
    public void setBalance(double balance) {
        this.balance = balance;
    }
    
    public void deposit(double amount) {
        balance += amount;
    }
    public String getUserName(){
        return username;
    }
    public void setUserName(String username){
        this.username=username;
    }
    public void printUser(){
        System.out.println("User ID: " + id);
        System.out.println("Email: " + email);
        System.out.println("Income: " + income);
        System.out.println("Balance: " + balance);
        System.out.println("Username: " + username);
        System.out.println("--------------------");
    }
}
