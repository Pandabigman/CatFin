package com.panda.model;

//package panda.financeapp.model;


import java.util.Date;

//import java.sql.Date;

import java.util.Objects;

public class Transaction {
    private int id;
    private String description;
    private double amount;
    private Date date;
    private String type;
    private int categoryId;

    // Constructor, Getters, and Setters
    public Transaction() {}

    public Transaction(int id, String description,double amount, Date date, int categoryId, String type){
        this.id= id;
        this.description= description;
        this.amount =amount;
        this.date = date;
        this.categoryId= categoryId;
        this.type= type;
    }
    //creating a transaction object in scene where id will be auto incremented in database 
    public Transaction(String description,double amount, Date date, int categoryId, String type){
        this.description= description;
        this.amount = amount;
        this.date = date;
        this.categoryId= categoryId;
        this.type= type;
    }
// Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    public String getType(){
        return type;
    }

    public void setType(String type){
        this.type=type;
    }

    // Override toString method for better readability
    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", date=" + date +
                ", type=" + type +
                ", categoryId=" + categoryId +
                '}';
    }

    // Override equals method to compare Transaction objects based on all fields
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return id == that.id &&
                Double.compare(that.amount, amount) == 0 &&
                categoryId == that.categoryId &&
                Objects.equals(description, that.description) &&
                Objects.equals(date, that.date);
    }

    // Override hashCode method to generate a unique hash code for each Transaction object
    @Override
    public int hashCode() {
        return Objects.hash(id, description, amount, date, categoryId, type);
    }

}
