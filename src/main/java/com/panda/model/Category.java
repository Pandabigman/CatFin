package com.panda.model;

//package panda.financeapp.model;

import java.util.Objects;

public class Category {
    private int id;
    private String name;

    // Default constructor
    public Category() {}

    // Parameterized constructor
    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Override toString method for better readability
    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    // Override equals method to compare Category objects based on id and name
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return id == category.id &&
                Objects.equals(name, category.name);
    }

    // Override hashCode method to generate a unique hash code for each Category object
    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
    
}
