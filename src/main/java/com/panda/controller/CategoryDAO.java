package com.panda.controller;

//import panda.financeapp.model.Category;
//import panda.financeapp.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.panda.model.Category;
import com.panda.utils.DatabaseConnection;

public class CategoryDAO {
    private Connection connection;

    public CategoryDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    public void addCategory(Category category) throws SQLException {
        String sql = "INSERT INTO categories (name) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, category.getName());
            stmt.executeUpdate();
        }
    }

    public List<String> getAllCategoryNames() throws SQLException {
        String query = "SELECT name FROM categories";
        List<String> categoryNames = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String categoryName = resultSet.getString("name");
                categoryNames.add(categoryName);
            }
        }
        return categoryNames;
    }

    // Method to fetch the category_id from the category name
    public int getCategoryIdByName(String categoryName) throws SQLException {
        String query = "SELECT id FROM categories WHERE name = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, categoryName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        }
        return -1; // Category not found
    }

    public List<Category> getAllCategories() throws SQLException {
        String sql = "SELECT * FROM categories";
        List<Category> categories = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Category category = new Category(
                    rs.getInt("id"),
                    rs.getString("name")
                );
                categories.add(category);
            }
        }
        return categories;
    }
    public String getCategoryNamebyID(int id) {
        String query = "SELECT name FROM categories WHERE id =?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Category not found
    }

    
}
