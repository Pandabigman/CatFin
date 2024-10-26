package com.panda.scenes;



import java.sql.SQLException;

import com.panda.controller.UserDAO;
import com.panda.utils.AlertBox;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class SignUpScene {
    private Scene signUpScene;
    private Stage primaryStage;
   

    public SignUpScene(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        initSignUpScene();
    }

    private void initSignUpScene() {
        VBox layout = new VBox(10);
        layout.setPadding(new javafx.geometry.Insets(10, 10, 10, 10));
    
        Label titleLabel = new Label("Sign Up");
        titleLabel.setStyle("-fx-alignment: CENTER; -fx-max-width: 400px;");
        VBox titleBox = new VBox(10);
        titleBox.getChildren().add(titleLabel);
        titleBox.setAlignment(Pos.CENTER);
    
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        emailField.setStyle("-fx-alignment: CENTER; -fx-max-width: 400px;");

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        usernameField.setStyle("-fx-alignment: CENTER; -fx-max-width: 400px;");

        Label startingBalanceLabel = new Label("Starting Balance:");
        TextField startingBalanceField = new TextField();
        startingBalanceField.setStyle("-fx-alignment: CENTER; -fx-max-width: 400px;");
            
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setStyle("-fx-alignment: CENTER; -fx-max-width: 400px;");

        Label confirmPasswordLabel = new Label("Confirm Password:");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setStyle("-fx-alignment: CENTER; -fx-max-width: 400px;");
    
        Button signUpButton = new Button("Sign Up");
        signUpButton.setAlignment(Pos.CENTER);
        signUpButton.setOnAction(e -> handleSignUp(emailField.getText(), usernameField.getText(),startingBalanceField.getText(), passwordField.getText(), confirmPasswordField.getText()));
        Button loginButton = new Button("Back to Login");
        loginButton.setAlignment(Pos.CENTER);
        loginButton.setOnAction(e -> {
            LoginScene loginScene = new LoginScene(primaryStage);
            primaryStage.setScene(loginScene.getScene());
        });
        //layout customization
        VBox inputBoxes = new VBox(10);
        inputBoxes.getChildren().addAll(emailLabel, emailField, usernameLabel, usernameField,startingBalanceLabel,startingBalanceField,
        passwordLabel, passwordField, confirmPasswordLabel, confirmPasswordField);
        inputBoxes.setAlignment(Pos.CENTER);
        VBox buttons = new VBox(10);
        buttons.getChildren().addAll(signUpButton,loginButton);
        buttons.setAlignment(Pos.CENTER);
    
        layout.getChildren().addAll(titleBox, inputBoxes, buttons);
        signUpScene = new Scene(layout, 600, 500);
        signUpScene.getStylesheets().add(getClass().getResource("../resources/styles/modernTeal.css").toExternalForm());
    }

    public Scene getScene() {
        return signUpScene;
    }


private void handleSignUp(String email, String username, String balance, String password, String confirmPassword) {
        if (email.isEmpty()||username.isEmpty() || password.isEmpty()) {
            AlertBox.show( "Sign Up Failed", "Please fill in all fields.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            AlertBox.show( "Sign Up Failed", "Passwords do not match.");
            return;
        }
        UserDAO userDAO = new UserDAO();
        try {
            //allows user to create account with or without starting balance
            double startingBalance = balance.isEmpty() ? 0.0 : Double.parseDouble(balance);
            userDAO.createUser(email, username, password, startingBalance);
            AlertBox.show("Success", "Account created successfully!");
            LoginScene loginScene = new LoginScene(primaryStage);
            primaryStage.setScene(loginScene.getScene());
        } 
        catch (NumberFormatException e) {
            AlertBox.show( "Sign Up Failed", "Invalid starting balance.");}
        catch (SQLException e) {
            AlertBox.show( "Sign Up Failed", "Error creating account: " + e.getMessage());
        }
    }
}
