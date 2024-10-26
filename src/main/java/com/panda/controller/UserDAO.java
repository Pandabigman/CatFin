package com.panda.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.panda.model.User;
import com.panda.utils.DatabaseConnection;
import com.panda.utils.PasswordUtils;

// import panda.financeapp.model.User;
// import panda.financeapp.utils.DatabaseConnection;
// import panda.financeapp.utils.PasswordUtils;

public class UserDAO {
    private Connection connection;
    

    public UserDAO() {
        this.connection = DatabaseConnection.getConnection();
    }
    public User getUserByEmailAndPassword(String email, String password) throws SQLException {
        Boolean userCheck= userExists(email);
        if(!userCheck){
            return null;
        }
        //the only method to actually check password in bCRypt
        Boolean passCheck= PasswordUtils.checkPassword(password,getPasswordHash(email));
        if (passCheck){
            String sql = "SELECT * FROM users WHERE email = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, email);
                //statement.setString(2, password);
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    return new User(rs.getInt("id"), rs.getString("email"), rs.getDouble("income"), rs.getDouble("balance"),rs.getString("user_name"));
                }}
        }
        return null;
    }


    public boolean userExists(String email) throws SQLException {
        String query = "SELECT * FROM users where email= ? ;";
        Boolean userExists=false;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                 userExists=true;
            }
        }
        return userExists;
    }
    public String getPasswordHash(String email) throws SQLException {
        String query = "SELECT password FROM users where email=?;";
        String hashedPassword = null;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                hashedPassword = rs.getString("password");
            }
        }
        return hashedPassword;
    }
    public void createUser( String email,String username, String password) throws SQLException {
        String hashedPassword = PasswordUtils.hashPassword(password);
        String sql = "INSERT INTO users ( email, password, user_name) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            statement.setString(2, hashedPassword);
            statement.setString(3, username);
            statement.executeUpdate();
            System.out.println("new user added successfully");
        }
    }
    public void createUser(String email,String username, String password, double balance) throws SQLException {
        String hashedPassword = PasswordUtils.hashPassword(password);
        String sql = "INSERT INTO users (email,user_name, password, balance) VALUES (?,?,?,?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            statement.setString(2, username);
            statement.setString(3, hashedPassword);
            statement.setDouble(4, balance);
            statement.executeUpdate();
            System.out.println("new user added successfully with initial balance");
        }
    }
    public void updateIncome(User user)throws SQLException {
        String sql = "UPDATE users SET income =? WHERE id =?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDouble(1, user.getIncome());
            statement.setInt(2, user.getId());
            statement.executeUpdate();
        }
    }
    public void updateIncome(double income) throws SQLException {
        int userId = SessionManager.getCurrentSession().getUserId();
        String sql = "UPDATE users SET income = ? WHERE id = ?";
    
        try (PreparedStatement statement = connection.prepareStatement(sql)){
            
            statement.setDouble(1, income);
            statement.setInt(2, userId);
            statement.executeUpdate();
        }
    }
    //get user info by id -can only be used when logged in
    public User getUser() throws SQLException {
        int userId = SessionManager.getCurrentSession().getUserId();
        String sql = "SELECT * FROM users WHERE id =?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt("id"), rs.getString("email"), rs.getDouble("income"), rs.getDouble("balance"), rs.getString("user_name"));
            }
        }
        return null;
    }
    public double getBalance() throws SQLException{
        int userId = SessionManager.getCurrentSession().getUserId();
        String sql = "SELECT balance FROM users WHERE id =?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getDouble("balance");
            }
        }
        return 0.00;
    }
    public void setBalance(double amount) throws SQLException {
        int userId = SessionManager.getCurrentSession().getUserId();
        String sql = "UPDATE users SET balance =? WHERE id =?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDouble(1, amount);
            statement.setInt(2, userId);
            statement.executeUpdate();
        }
    }
    public String getUserName() throws SQLException {
        int userId = SessionManager.getCurrentSession().getUserId();
        String sql = "SELECT user_name FROM users WHERE id =?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getString("user_name");
            }
        }
        return "";
    }
    // public void deposit(int amount) throws SQLException {
    //     int userId = SessionManager.getCurrentSession().getUserId();
    //     int currentBalance = getBalance();
    //     setBalance(currentBalance + amount);
    //     String sql = "INSERT INTO transactions (user_id, type, amount) VALUES (?,?,?)";
    //     try (PreparedStatement statement = connection.prepareStatement(sql)) {
    //         statement.setInt(1, userId);
    //         statement.setString(2, "Deposit");
    //         statement.setInt(3, amount);
    //         statement.executeUpdate();
    //     }
    // }
}
