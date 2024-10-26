package com.panda.model;


public class Budget {
    private static int income;
    private int id;
    private double amount;
    private int category_id;


    public Budget(double amount, int category_id){
        this.amount=amount;
        this.category_id=category_id;
    }

    public Budget(int id, double amount, int category_id){
        this.amount=amount;
        this.category_id=category_id;
        this.id=id;
    }
    
    //getters and setters
    public int getBudgetId() {
        return id;
    }

    public void setBudgetId(int id) {
        this.id = id;
    }

    public double getAmount(){
        return amount;
    }
    public void setAmount(double amount){
        this.amount=amount;
    }
    public int getCategoryId(){
        return category_id;
    }
    public void setCategoryId(int category){
        this.category_id= category;
    }

    public static void setIncome(int income) {
        Budget.income = income;
    }
    public static int getIncome(){
        return income;
    }
    
}
